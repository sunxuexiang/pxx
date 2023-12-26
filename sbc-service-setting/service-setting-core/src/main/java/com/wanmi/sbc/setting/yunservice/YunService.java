package com.wanmi.sbc.setting.yunservice;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PolicyConditions;
import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.osd.bean.OsdConfig;
import com.wanmi.osd.bean.OsdResource;
import com.wanmi.osd.oss.AliOssClient;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.request.storeresource.StoreResourceQueryRequest;
import com.wanmi.sbc.setting.api.request.storeresourcecate.StoreResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourceQueryRequest;
import com.wanmi.sbc.setting.api.request.systemresourcecate.SystemResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceQueryRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadVideoResourceRequest;
import com.wanmi.sbc.setting.api.response.yunservice.YunParamResponse;
import com.wanmi.sbc.setting.bean.enums.CateParentTop;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.setting.config.ConfigRepository;
import com.wanmi.sbc.setting.storeresource.model.root.StoreResource;
import com.wanmi.sbc.setting.storeresource.service.StoreResourceService;
import com.wanmi.sbc.setting.storeresourcecate.model.root.StoreResourceCate;
import com.wanmi.sbc.setting.storeresourcecate.service.StoreResourceCateService;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.systemresource.model.root.SystemResource;
import com.wanmi.sbc.setting.systemresource.service.SystemResourceService;
import com.wanmi.sbc.setting.systemresourcecate.model.root.SystemResourceCate;
import com.wanmi.sbc.setting.systemresourcecate.service.SystemResourceCateService;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.service.VideoResourceService;
import com.wanmi.sbc.setting.videoresourcecate.model.root.VideoResourceCate;
import com.wanmi.sbc.setting.videoresourcecate.service.VideoResourceCateService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 云服务
 */
@Service
@Data
@Slf4j
public class YunService {

	@Autowired
	private ConfigRepository configRepository;

	@Autowired
	private SystemResourceCateService systemResourceCateService;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private SystemResourceService systemResourceService;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private StoreResourceCateService storeResourceCateService;

	@Autowired
	private StoreResourceService storeResourceService;

	@Autowired
	private VideoResourceCateService videoResourceCateService;

	@Autowired
	private VideoResourceService videoResourceService;

	private static final String RESOURCE_KEY = "SYSTEM_RESOURCE_CAT";

	private static final String SPLIT_CHAR = "|";

	@Transactional
	public String justUploadFile(String resourceKey, byte[] content) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();

		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		// 上传
		String resourceUrl = upload(resourceKey, osdClientParam, content);
		return resourceUrl;
	}

	/**
	 * 上传文件
	 *
	 * @param yunUploadResourceRequest
	 * @return
	 */
	@Transactional
	public String uploadFile(YunUploadResourceRequest yunUploadResourceRequest) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();
        log.info("----------------------查询到可用的云服务:{}",availableYun.getContext());
		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		String resourceKey = yunUploadResourceRequest.getResourceKey();
		String fileName = yunUploadResourceRequest.getResourceName();
		byte[] content = yunUploadResourceRequest.getContent();
		if (Objects.isNull(resourceKey)) {
			resourceKey = getKey(fileName);
		}
		log.info("----------------------资源key是什么:{}",resourceKey);
		// 上传
		String resourceUrl = upload(resourceKey, osdClientParam, content);
		// 喜丫丫迭代3 视频管理使用视频上传
		if (Objects.nonNull(yunUploadResourceRequest.getResourceType())
				&& ResourceType.SHORT_VIDEO.equals(yunUploadResourceRequest.getResourceType())) {
			return resourceKey;
		}
		// 保存上传信息
		log.info("----------------------保存上传信息:{}",resourceKey);
		yunUploadResourceRequest.setArtworkUrl(resourceKey);
		yunUploadResourceRequest.setResourceKey(resourceKey);
		if (Objects.isNull(yunUploadResourceRequest.getStoreId())) {
			saveFile(yunUploadResourceRequest);
		} else {
			saveStoreFile(yunUploadResourceRequest);
		}
		return resourceUrl;
	}


	@Transactional
	public String uploadVideoFile(YunUploadVideoResourceRequest yunUploadVideoResourceRequest) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();

		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		String resourceKey = yunUploadVideoResourceRequest.getResourceKey();
		String fileName = yunUploadVideoResourceRequest.getResourceName();
		//byte[] content = yunUploadVideoResourceRequest.getContent();
		if (Objects.isNull(resourceKey)) {
			resourceKey = getKey(fileName);
		}
		// 上传
		String resourceUrl = upload(resourceKey, osdClientParam, yunUploadVideoResourceRequest.getContent());
		// 喜丫丫迭代3 视频管理使用视频上传
		if (Objects.nonNull(yunUploadVideoResourceRequest.getResourceType())
				&& ResourceType.SHORT_VIDEO.equals(yunUploadVideoResourceRequest.getResourceType())) {
			return resourceKey;
		}
		// 保存上传信息
		yunUploadVideoResourceRequest.setArtworkUrl(resourceKey);
		yunUploadVideoResourceRequest.setResourceKey(resourceKey);
		saveVideoFile(yunUploadVideoResourceRequest);
		return resourceUrl;
	}


	/**
	 * 删除文件
	 *
	 * @param resourceIds
	 * @param storeId
	 */
	@Transactional
	public void deleteResources(List<Long> resourceIds, Long storeId) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		List<String> resourceKeys;
		if (Objects.isNull(storeId)) {
			List<SystemResource> systemResources = systemResourceService.list(
					SystemResourceQueryRequest.builder().resourceIdList(resourceIds).delFlag(DeleteFlag.NO).build());
			resourceKeys = systemResources.stream().map(SystemResource::getResourceKey).collect(Collectors.toList());
			systemResourceService.deleteByIdList(resourceIds);
		} else {
			List<StoreResource> storeResources = storeResourceService.list(StoreResourceQueryRequest.builder()
					.resourceIdList(resourceIds).storeId(storeId).delFlag(DeleteFlag.NO).build());
			resourceKeys = storeResources.stream().map(StoreResource::getResourceKey).collect(Collectors.toList());
			storeResourceService.deleteByIdList(resourceIds, storeId);
		}
		// 删除云文件
		try {
			OsdClient.instance().deleteObject(osdClientParam, resourceKeys);
		} catch (Exception e) {
			throw new SbcRuntimeException(e);
		}

	}

	/**
	 * 删除文件
	 *
	 * @param resourceIds
	 * @param storeId
	 */
	@Transactional
	public void deleteVideoResources(List<Long> resourceIds, Long storeId) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		List<String> resourceKeys;
		List<VideoResource> videoResources = videoResourceService.list(VideoResourceQueryRequest.builder()
				.resourceIdList(resourceIds).storeId(storeId).delFlag(DeleteFlag.NO).build());
		resourceKeys = videoResources.stream().map(VideoResource::getResourceKey).collect(Collectors.toList());
		videoResourceService.deleteByIdList(resourceIds, storeId);
		// 删除云文件
		try {
			OsdClient.instance().deleteObject(osdClientParam, resourceKeys);
		} catch (Exception e) {
			throw new SbcRuntimeException(e);
		}

	}

	/**
	 * 获取文件信息
	 *
	 * @param resourceKey
	 * @return
	 */
	public byte[] getFile(String resourceKey) {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		try {
			OsdClient osdClient = OsdClient.instance();
			if (!osdClient.doesObjectExist(osdClientParam, resourceKey)) {
				return null;
			}
			return osdClient.getObject(osdClientParam, resourceKey);
		} catch (Exception e) {
			throw new SbcRuntimeException(e);
		}
	}

	/**
	 * 保存资源信息
	 *
	 * @param yunUploadResourceRequest
	 * @return
	 */
	private void saveFile(YunUploadResourceRequest yunUploadResourceRequest) {
		boolean isSaveResource = true;// 自动生成素材（图片、视频）库记录
		final Long cateIdNew;
		Long cateId = yunUploadResourceRequest.getCateId();
		if (cateId == null || cateId == 0) {
			List<SystemResourceCate> resourceCates = systemResourceCateService.list(
					SystemResourceCateQueryRequest.builder().delFlag(DeleteFlag.NO).isDefault(DefaultFlag.YES).build());
			if (Objects.isNull(resourceCates)) {
				if (redisTemplate.opsForValue().setIfAbsent(RESOURCE_KEY.concat("-0"), RESOURCE_KEY.concat("-0"))) {
					redisTemplate.opsForValue().set(RESOURCE_KEY.concat("-0"), RESOURCE_KEY.concat("-0"), 10L,
							TimeUnit.SECONDS);
				} else {
					throw new SbcRuntimeException("K-061002");
				}
				SystemResourceCate resourceCate = new SystemResourceCate();
				resourceCate.setCateName("默认分类");
				resourceCate.setCateParentId((long) (CateParentTop.ZERO.toValue()));
				resourceCate.setIsDefault(DefaultFlag.YES);
				resourceCate.setDelFlag(DeleteFlag.NO);
				resourceCate.setCateGrade(0);
				resourceCate.setCatePath(String.valueOf(resourceCate.getCateParentId()).concat(SPLIT_CHAR));
				resourceCate = systemResourceCateService.add(resourceCate);
				cateIdNew = resourceCate.getCateId();
			} else {
				cateIdNew = resourceCates.get(0).getCateId();
			}
			isSaveResource = false;
		} else {
			cateIdNew = cateId;
		}

		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();

		if (Objects.nonNull(availableYun)) {
			if (isSaveResource) {
				SystemResource resource = new SystemResource();
				resource.setCateId(cateIdNew);
				resource.setArtworkUrl(yunUploadResourceRequest.getArtworkUrl());
				resource.setResourceKey(yunUploadResourceRequest.getResourceKey());
				resource.setResourceName(yunUploadResourceRequest.getResourceName());
				resource.setCreateTime(LocalDateTime.now());
				resource.setUpdateTime(resource.getCreateTime());
				resource.setDelFlag(DeleteFlag.NO);
				resource.setServerType(availableYun.getConfigType());
				resource.setResourceType(yunUploadResourceRequest.getResourceType());
				systemResourceService.add(resource);
			}
		}
	}

	/**
	 * 保存店铺上传资源信息
	 *
	 * @param yunUploadResourceRequest
	 */
	private void saveStoreFile(YunUploadResourceRequest yunUploadResourceRequest) {
		boolean isSaveResource = true;// 自动生成素材库记录
		final Long cateIdNew;
		Long cateId = yunUploadResourceRequest.getCateId();
		Long storeId = yunUploadResourceRequest.getStoreId();
		Long companyInfoId = yunUploadResourceRequest.getCompanyInfoId();
		if (cateId == null || cateId == 0) {
			List<StoreResourceCate> storeResourceCates = storeResourceCateService.list(StoreResourceCateQueryRequest
					.builder().delFlag(DeleteFlag.NO).isDefault(DefaultFlag.YES).storeId(storeId).build());
			if (CollectionUtils.isEmpty(storeResourceCates)) {
				if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(RESOURCE_KEY.concat("-" + storeId),
						RESOURCE_KEY.concat("-" + storeId), 10l, TimeUnit.SECONDS))) {
					throw new SbcRuntimeException("K-061002");
				}
				StoreResourceCate storeResourceCate = new StoreResourceCate();
				storeResourceCate.setCateName("默认分类");
				storeResourceCate.setCateParentId((long) (CateParentTop.ZERO.toValue()));
				storeResourceCate.setIsDefault(DefaultFlag.YES);
				storeResourceCate.setDelFlag(DeleteFlag.NO);
				storeResourceCate.setCateGrade(1);
				storeResourceCate.setCatePath(String.valueOf(storeResourceCate.getCateParentId()).concat(SPLIT_CHAR));

				// 商家和店铺id
				storeResourceCate.setCompanyInfoId(companyInfoId);
				storeResourceCate.setStoreId(storeId);
				storeResourceCate.setSort(0);
				storeResourceCate.setCreateTime(LocalDateTime.now());
				storeResourceCate.setUpdateTime(LocalDateTime.now());
				storeResourceCate = storeResourceCateService.add(storeResourceCate);
				cateIdNew = storeResourceCate.getCateId();
			} else {
				cateIdNew = storeResourceCates.get(0).getCateId();
			}
			isSaveResource = false;
		} else {
			cateIdNew = cateId;
		}

		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();

		if (Objects.nonNull(availableYun) && isSaveResource) {
			StoreResource storeResource = new StoreResource();
			storeResource.setStoreId(storeId);
			storeResource.setCompanyInfoId(companyInfoId);
			storeResource.setCateId(cateIdNew);
			storeResource.setResourceKey(yunUploadResourceRequest.getResourceKey());
			storeResource.setArtworkUrl(yunUploadResourceRequest.getArtworkUrl());
			storeResource.setResourceName(yunUploadResourceRequest.getResourceName());
			storeResource.setCreateTime(LocalDateTime.now());
			storeResource.setUpdateTime(storeResource.getCreateTime());
			storeResource.setServerType(availableYun.getConfigType());
			storeResource.setResourceType(yunUploadResourceRequest.getResourceType());
			storeResource.setDelFlag(DeleteFlag.NO);
			storeResourceService.add(storeResource);
		}
	}

	/**
	 * 保存视频教程上传资源信息
	 *
	 * @param yunUploadResourceRequest
	 */
	private void saveVideoFile(YunUploadVideoResourceRequest yunUploadResourceRequest) {
		boolean isSaveResource = true;// 自动生成素材库记录
		String cateIdNew;
		String cateId = yunUploadResourceRequest.getCateId();
		Long storeId = yunUploadResourceRequest.getStoreId();
		Long companyInfoId = yunUploadResourceRequest.getCompanyInfoId();
		if (StringUtils.isBlank(cateId)) {
			List<VideoResourceCate> videoResourceCates = videoResourceCateService.list(VideoResourceCateQueryRequest
					.builder().delFlag(DeleteFlag.NO).isDefault(DefaultFlag.YES).storeId(storeId).build());
			if (CollectionUtils.isEmpty(videoResourceCates)) {
				if (redisTemplate.opsForValue().setIfAbsent(RESOURCE_KEY.concat("-" + storeId),
						RESOURCE_KEY.concat("-" + storeId))) {
					redisTemplate.opsForValue().set(RESOURCE_KEY.concat("-" + storeId),
							RESOURCE_KEY.concat("-" + storeId), 10L, TimeUnit.SECONDS);
				} else {
					throw new SbcRuntimeException("K-061002");
				}
				VideoResourceCate videoResourceCate = new VideoResourceCate();
				videoResourceCate.setCateName("默认分类");
				videoResourceCate.setCateParentId(CateParentTop.ZERO.toString());
				videoResourceCate.setIsDefault(DefaultFlag.YES);
				videoResourceCate.setDelFlag(DeleteFlag.NO);
				videoResourceCate.setCateGrade(1);
				videoResourceCate.setCatePath(String.valueOf(videoResourceCate.getCateParentId()).concat(SPLIT_CHAR));

				// 商家和店铺id
				videoResourceCate.setCompanyInfoId(companyInfoId);
				videoResourceCate.setStoreId(storeId);
				videoResourceCate.setSort(0);
				videoResourceCate.setCreateTime(LocalDateTime.now());
				videoResourceCate.setUpdateTime(LocalDateTime.now());
				videoResourceCate = videoResourceCateService.add(videoResourceCate);
				cateIdNew = videoResourceCate.getCateId();
			} else {
				cateIdNew = videoResourceCates.get(0).getCateId();
			}
			isSaveResource = false;
		} else {
			cateIdNew = cateId;
		}

		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();

		if (Objects.nonNull(availableYun) && isSaveResource) {
			VideoResource videoResource = new VideoResource();
			videoResource.setStoreId(storeId);
			videoResource.setCompanyInfoId(companyInfoId);
			videoResource.setCateId(cateIdNew);
			videoResource.setResourceKey(yunUploadResourceRequest.getResourceKey());
			videoResource.setArtworkUrl(yunUploadResourceRequest.getArtworkUrl());
			videoResource.setResourceName(yunUploadResourceRequest.getResourceName());
			videoResource.setCreateTime(LocalDateTime.now());
			videoResource.setUpdateTime(videoResource.getCreateTime());
			videoResource.setServerType(availableYun.getConfigType());
			videoResource.setResourceType(yunUploadResourceRequest.getResourceType());
			videoResource.setHvType(yunUploadResourceRequest.getHvType());
			videoResource.setDelFlag(DeleteFlag.NO);
			videoResourceService.add(videoResource);
		}
	}

	/**
	 * 获取资源key
	 *
	 * @param fileName
	 * @return
	 */
	private String getKey(String fileName) {
		int last;
		if (StringUtils.isNotBlank(fileName) && (last = fileName.lastIndexOf(".")) > -1) {
			// 图片类型
			String fileType = fileName.substring(last + 1);
			String fileRe = FastDateFormat.getInstance("yyyyMMddHHmmss").format(System.currentTimeMillis());
			String key = fileRe
					.concat(StringUtils.leftPad(String.valueOf(ThreadLocalRandom.current().nextInt(0, 9999)), 4, "0"))
					.concat(".").concat(fileType);
			return key;
		} else {
			throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
		}
	}

	/**
	 * 上传
	 *
	 * @param key
	 * @param osdClientParam
	 * @param content
	 * @return
	 */
	private String upload(String key, OsdClientParam osdClientParam, byte[] content) {
		try {
			log.info("--------------上传文件byte转inputStream------------------");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(byteArrayInputStream);
			OsdResource osdResource = OsdResource.builder().osdResourceKey(key).osdInputStream(bufferedInputStream)
					.build();
			OsdClient osdClient = OsdClient.instance();
			log.info("--------------osdClient组装完成------------------");
			// 上传文件
			osdClient.putObject(osdClientParam, osdResource);
			log.info("--------------putObject方法完成------------------");
			byteArrayInputStream.close();
			bufferedInputStream.close();
			log.info("--------------关闭inputStream完成------------------");
			return osdClient.getResourceUrl(osdClientParam, key);
		} catch (Exception e) {
			throw new SbcRuntimeException(e);
		}
	}


	public YunParamResponse getOssToken() {
		// 查询可用云服务
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		log.info("----------------------查询到可用的云服务:{}",availableYun.getContext());
		OsdClientParam osdClientParam = OsdClientParam.builder().configType(availableYun.getConfigType())
				.context(availableYun.getContext()).build();
		OsdClient osdClient = OsdClient.instance();
		OsdConfig osdConfig = osdClient.buildConfig(osdClientParam);
		OSSClient ossClient = AliOssClient.instance().init(osdConfig);
		try{
			long expireTime = 30;
			long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
			Date expiration = new Date(expireEndTime);
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1024 * 1024 * 1024);

			String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes("utf-8");
			String encodedPolicy = BinaryUtil.toBase64String(binaryData);
			String postSignature = ossClient.calculatePostSignature(postPolicy);
			YunParamResponse yunParamResponse = new YunParamResponse();
			yunParamResponse.setHost("https://" + osdConfig.getBucketName() + "." + osdConfig.getEndPoint());
			yunParamResponse.setExpire(expireEndTime / 1000);
			yunParamResponse.setAccessid(osdConfig.getAccessKeyId());
			yunParamResponse.setSignature(postSignature);
			yunParamResponse.setPolicy(encodedPolicy);
			return yunParamResponse;
		}catch (Exception e){
            log.error("{}",e);
		}finally {
			ossClient.shutdown();
		}
		return null;
	}

}