package com.wanmi.sbc.setting.provider.impl.videoresource;

import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceQueryProvider;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceByIdRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceListRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourcePageRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceQueryRequest;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceByIdResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceListResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceMapResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourcePageResponse;
import com.wanmi.sbc.setting.bean.vo.VideoResourceVO;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.service.VideoResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频教程资源库查询服务接口实现
 * @author hudong
 * @date 2023-06-26 09:17
 */
@Slf4j
@RestController
@Validated
public class VideoResourceQueryController implements VideoResourceQueryProvider {
    @Autowired
    private VideoResourceService videoResourceService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public BaseResponse<VideoResourcePageResponse> page(@RequestBody @Valid VideoResourcePageRequest videoResourcePageRequest) {
        VideoResourceQueryRequest queryReq = new VideoResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(videoResourcePageRequest, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        queryReq.putSort("createTime", SortType.DESC.toValue());
        queryReq.putSort("resourceId", SortType.ASC.toValue());
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<VideoResource> videoResourcePage = videoResourceService.page(queryReq);
        Page<VideoResourceVO> newPage = videoResourcePage.map(entity -> {
                    //获取url
                    String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, entity.getArtworkUrl());
                    entity.setArtworkUrl(resourceUrl);
                    return videoResourceService.wrapperVo(entity);
                }
        );
        MicroServicePage<VideoResourceVO> microPage = new MicroServicePage<>(newPage, videoResourcePageRequest.getPageable());
        VideoResourcePageResponse finalRes = new VideoResourcePageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<VideoResourcePageResponse> defaultList(VideoResourcePageRequest videoResourcePageRequest) {
        return this.page(videoResourcePageRequest);
    }

    @Override
    public BaseResponse<VideoResourceMapResponse> mapInfo(VideoResourcePageRequest videoResourcePageRequest) {
        VideoResourceQueryRequest queryReq = new VideoResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(videoResourcePageRequest, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        return videoResourceService.map(queryReq,osdClientParam);
    }

    @Override
    public BaseResponse<List<VideoResourceListResponse>> list(@RequestBody @Valid VideoResourceListRequest videoResourceListReq) {
        List<VideoResourceListResponse> videoResourceListResponseList = new ArrayList<>();
        boolean secondFlag = false;
        VideoResourceQueryRequest queryReq = new VideoResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(videoResourceListReq, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        List<VideoResource> storeResourceList = videoResourceService.list(queryReq);
        //一级分类下没有数据则查询二级分类
        if(CollectionUtils.isEmpty(storeResourceList)){
            if(videoResourceListReq.getCateMap().size() > 0){
                List<String> cateIds = new ArrayList<>();
                for(String key : videoResourceListReq.getCateMap().keySet()){
                    cateIds.add(key);
                }
                queryReq.setCateId(null);
                queryReq.setCateIds(cateIds);
                storeResourceList = videoResourceService.list(queryReq);
            }
            if(CollectionUtils.isEmpty(storeResourceList)){
                return BaseResponse.SUCCESSFUL();
            }
            secondFlag = true;
        }
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        List<VideoResourceVO> videoResourceVOS = storeResourceList.stream().map(t ->{
            //获取url
            String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, t.getArtworkUrl());
            t.setArtworkUrl(resourceUrl);
            return videoResourceService.wrapperVo(t);
        }).collect(Collectors.toList());
        //如果是二级分类下的视频资源，转换二级分类名，获取资源集合后返回
        if(secondFlag){
            Map<String,List<VideoResourceVO>> videoResourceMap = videoResourceVOS.stream().collect(Collectors.groupingBy(VideoResourceVO::getCateId));
            for(String cateId : videoResourceMap.keySet()){
                String cateName = videoResourceListReq.getCateMap().get(cateId);
                videoResourceListResponseList.add(new VideoResourceListResponse(cateName,videoResourceMap.get(cateId)));
            }
            return BaseResponse.success(videoResourceListResponseList);
        }
        //一级分类下的视频无需二级分类名
        videoResourceListResponseList.add(new VideoResourceListResponse("",videoResourceVOS));
        return BaseResponse.success(videoResourceListResponseList);
    }

    @Override
    public BaseResponse<VideoResourceByIdResponse> getById(@RequestBody @Valid VideoResourceByIdRequest videoResourceByIdRequest) {
        VideoResource videoResource = videoResourceService.getById(videoResourceByIdRequest.getResourceId());
        return BaseResponse.success(new VideoResourceByIdResponse(videoResourceService.wrapperVo(videoResource)));
    }

}

