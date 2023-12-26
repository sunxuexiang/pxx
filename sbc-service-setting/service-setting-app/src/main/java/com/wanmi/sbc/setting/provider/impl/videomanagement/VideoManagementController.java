package com.wanmi.sbc.setting.provider.impl.videomanagement;

import com.esotericsoftware.minlog.Log;
import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.bean.enums.StateType;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.service.VideoLikeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementProvider;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementAddResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementModifyResponse;
import com.wanmi.sbc.setting.videomanagement.service.VideoManagementService;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>视频管理保存服务接口实现</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@RestController
@Validated
public class VideoManagementController implements VideoManagementProvider {

	/**
	 * 第一帧配置信息
	 */
	@Value("${img.first.frame}")
	private String imgFirstFrame;

	@Autowired
	private VideoManagementService videoManagementService;

	@Autowired
	private VideoLikeService videoLikeService;

	@Autowired
	private SystemConfigService systemConfigService;

	@Override
	public BaseResponse<VideoManagementAddResponse> add(@RequestBody @Valid VideoManagementAddRequest videoManagementAddRequest) {
		VideoManagement videoManagement = KsBeanUtil.convert(videoManagementAddRequest, VideoManagement.class);
		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		if (Objects.nonNull(availableYun)) {
			videoManagement.setServerType(availableYun.getConfigType());
		}
		OsdClientParam osdClientParam = OsdClientParam.builder()
				.configType(availableYun.getConfigType())
				.context(availableYun.getContext())
				.build();
		if(StringUtils.isEmpty(videoManagementAddRequest.getCoverImg())){
			//获取url
			String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, videoManagementAddRequest.getArtworkUrl());
			Log.info("===========================配置地址：{}========================",imgFirstFrame);
			String coverImg =  resourceUrl  + imgFirstFrame;
			Log.info("================================封面地址：{}============================",coverImg);
			videoManagement.setCoverImg(coverImg);
		}
		// 发布视频，默认下架，需要运营后台审核后操作上架
		videoManagement.setState(StateType.OFF_SHELF);
		//获取地址
		return BaseResponse.success(new VideoManagementAddResponse(
				videoManagementService.wrapperVo(videoManagementService.add(videoManagement))));
	}

	/**
	 * 初始化所有小视频封面
	 * @return
	 */
	@Override
	public BaseResponse initCoverImg() {
		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder()
				.configType(availableYun.getConfigType())
				.context(availableYun.getContext())
				.build();
		//查询所有没有删除的小视频
		List<VideoManagement> list = videoManagementService.list(VideoManagementQueryRequest.builder().delFlag(DeleteFlag.NO).build());
		list = list.stream().filter((videoManagement) -> StringUtils.isNotEmpty(videoManagement.getArtworkUrl())).collect(Collectors.toList());
		list.stream().forEach(videoManagement -> {
			//获取url
			String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, videoManagement.getArtworkUrl());
			String coverImg =  resourceUrl  + imgFirstFrame;
			videoManagement.setCoverImg(coverImg);
		});
		if(CollectionUtils.isNotEmpty(list)){
			Object o = videoManagementService.saveAll(list);
			return BaseResponse.success(o);
		}
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<String> getCoverImg(GetCoverImgRequest getCoverImgRequest) {
		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder()
				.configType(availableYun.getConfigType())
				.context(availableYun.getContext())
				.build();
		//获取url
		String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, getCoverImgRequest.getArtworkUrl());
		String coverImg =  resourceUrl  + imgFirstFrame;
		return BaseResponse.success(coverImg);
	}

	@Override
	public BaseResponse modify(@RequestBody @Valid VideoManagementModifyRequest videoManagementModifyRequest) {
		VideoManagement videoManagement = KsBeanUtil.convert(videoManagementModifyRequest, VideoManagement.class);
		//获取可用服务器配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		OsdClientParam osdClientParam = OsdClientParam.builder()
				.configType(availableYun.getConfigType())
				.context(availableYun.getContext())
				.build();
		//如果封面地址为空
		if(StringUtils.isEmpty(videoManagement.getCoverImg())){
			//获取url
			String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, videoManagement.getArtworkUrl());
			Log.info("===========================配置地址：{}========================",imgFirstFrame);
			String coverImg =  resourceUrl  + imgFirstFrame;
			Log.info("================================封面地址：{}============================",coverImg);
			videoManagement.setCoverImg(coverImg);
		}
		// 修改视频，默认下架，需要运营后台审核后操作上架
		videoManagement.setState(StateType.OFF_SHELF);
		videoManagementService.modify(videoManagement);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse updateStateById(@Valid VideoManagementUpdateStateRequest request) {
		VideoManagement videoManagement = KsBeanUtil.convert(request, VideoManagement.class);
		videoManagementService.updateStateById(videoManagement);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid VideoManagementDelByIdRequest videoManagementDelByIdRequest) {
		VideoManagement videoManagement = KsBeanUtil.convert(videoManagementDelByIdRequest, VideoManagement.class);
		videoManagement.setDelFlag(DeleteFlag.YES);
		videoManagementService.deleteById(videoManagement);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid VideoManagementDelByIdListRequest videoManagementDelByIdListRequest) {
		List<VideoManagement> videoManagementList = videoManagementDelByIdListRequest.getVideoIdList().stream()
			.map(VideoId -> {
				VideoManagement videoManagement = KsBeanUtil.convert(VideoId, VideoManagement.class);
				videoManagement.setDelFlag(DeleteFlag.YES);
				return videoManagement;
			}).collect(Collectors.toList());
		videoManagementService.deleteByIdList(videoManagementList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse addVideoLike(@Valid VideoLikeAddRequest addReq) {
		VideoLike videoLike = KsBeanUtil.convert(addReq.getVideoLikeVO(), VideoLike.class);
		if (videoLikeService.queryByIdOrCustomerId(videoLike.getVideoId(), videoLike.getCustomerId()) > 0) {
			return BaseResponse.SUCCESSFUL();
		}
		videoLikeService.add(videoLike);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse cancelVideoLike(@Valid VideoLikeCancelRequest cancelReq) {
		VideoLike videoLike = KsBeanUtil.convert(cancelReq.getVideoLikeVO(), VideoLike.class);
		videoLikeService.delete(videoLike);
		return BaseResponse.SUCCESSFUL();
	}

}

