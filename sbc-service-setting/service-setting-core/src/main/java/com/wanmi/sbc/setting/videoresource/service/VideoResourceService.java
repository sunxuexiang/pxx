package com.wanmi.sbc.setting.videoresource.service;

import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceQueryRequest;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceMapResponse;
import com.wanmi.sbc.setting.bean.vo.NewMenuInfoVO;
import com.wanmi.sbc.setting.bean.vo.VideoResourceVO;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.repository.VideoResourceRepository;
import com.wanmi.sbc.setting.yunservice.YunService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频教程资源库业务逻辑
 *
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@Slf4j
@Service("VideoResourceService")
public class VideoResourceService {
    @Autowired
    private VideoResourceRepository videoResourceRepository;

    @Autowired
    private YunService yunService;

    /**
     * 新增店铺资源库
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public VideoResource add(VideoResource entity) {
        entity.setDelFlag(DeleteFlag.NO);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        videoResourceRepository.save(entity);
        return entity;
    }

    /**
     * 修改店铺资源库
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public VideoResource modify(VideoResource newVideoResource) {
        VideoResource oldVideoResource = videoResourceRepository.findById(newVideoResource.getResourceId()).orElse(null);
        if (oldVideoResource == null || oldVideoResource.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_NOT_EXIST_ERROR);
        }
        //更新素材
        newVideoResource.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(newVideoResource, oldVideoResource);
        videoResourceRepository.save(oldVideoResource);
        return oldVideoResource;
    }

    /**
     * 单个删除店铺资源库
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        videoResourceRepository.deleteById(id);
    }

    /**
     * 批量更新素材的分类
     *
     * @param resourceIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCateByIds(String cateId, List<Long> resourceIds, Long storeId) {
        videoResourceRepository.updateCateByIds(cateId, resourceIds, storeId);
    }

    /**
     * 批量删除店铺资源库
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> resourceIds, Long storeId) {
        VideoResourceQueryRequest queryRequest = VideoResourceQueryRequest.builder()
                .resourceIdList(resourceIds)
                .storeId(storeId).build();
        List<VideoResource> resources = videoResourceRepository.findAll(VideoResourceWhereCriteriaBuilder.build(queryRequest));
        if (CollectionUtils.isNotEmpty(resources)) {
            //删除素材
            yunService.deleteVideoResources(resourceIds, storeId);
        }
    }

    /**
     * 批量删除店铺资源库
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdList(List<Long> ids, Long storeId) {
        videoResourceRepository.deleteByIdList(ids, storeId);
    }


    /**
     * 单个查询店铺资源库
     *
     * @author lq
     */
    public VideoResource getById(Long id) {
        return videoResourceRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询店铺资源库
     *
     * @author lq
     */
    public Page<VideoResource> page(VideoResourceQueryRequest queryReq) {
        return videoResourceRepository.findAll(
                VideoResourceWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询店铺资源库
     *
     * @author lq
     */
    public List<VideoResource> list(VideoResourceQueryRequest queryReq) {
        return videoResourceRepository.findAll(
                VideoResourceWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 列表查询店铺资源库
     *
     * @author lq
     */
    public List<VideoResource> findByIdList(List<NewMenuInfoVO> newMenuInfoVOS) {
        List<String> cateIdList = newMenuInfoVOS.stream().map(NewMenuInfoVO::getId).collect(Collectors.toList());
        List<VideoResource> videoResourceList = this.list(VideoResourceQueryRequest
                .builder()
                .cateIds(cateIdList)
                .delFlag(DeleteFlag.NO).build());
        return videoResourceList;
    }

    /**
     * 将实体包装成VO
     *
     * @author lq
     */
    public VideoResourceVO wrapperVo(VideoResource videoResource) {
        if (videoResource != null) {
            VideoResourceVO videoResourceVO = new VideoResourceVO();
            KsBeanUtil.copyPropertiesThird(videoResource, videoResourceVO);
            return videoResourceVO;
        }
        return null;
    }

    /**
     * 查询视频资源返回map
     *
     * @author lwp
     */
    public BaseResponse<VideoResourceMapResponse> map(VideoResourceQueryRequest queryReq,OsdClientParam osdClientParam) {
        List<VideoResource> videoResourceList = videoResourceRepository.findAll(VideoResourceWhereCriteriaBuilder.build(queryReq),
                 queryReq.getSort());
        if(CollectionUtils.isEmpty(videoResourceList)){
            return BaseResponse.SUCCESSFUL();
        }
        List<VideoResourceVO> videoResourceVOS = videoResourceList.stream().map(t ->{
            //获取url
            String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, t.getArtworkUrl());
            t.setArtworkUrl(resourceUrl);
            return this.wrapperVo(t);
        }).collect(Collectors.toList());
        log.info("videoResourceVOS:{}",videoResourceVOS);
        Map<String,List<VideoResourceVO>> videoResourceMap = videoResourceVOS.stream().collect(Collectors.groupingBy(VideoResourceVO::getCateId));
        VideoResourceMapResponse videoResourceMapResponse = new VideoResourceMapResponse();
        videoResourceMapResponse.setVideoResourceVOMap(videoResourceMap);
        log.info("videoResourceMap:{}",videoResourceMap);
        return BaseResponse.success(videoResourceMapResponse);
    }
}
