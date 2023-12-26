package com.wanmi.sbc.videotutorials;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceListRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourcePageRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateListRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCatePageRequest;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourceAppResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceListResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceMapResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourcePageResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateListResponse;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 视频教程管理APP
 * @author hudong
 * @date 2023-09-25
 */
@Slf4j
@RestController
@RequestMapping("/videoTutorials")
@Api(tags = "VideoTutorialsController", description = "视频教程管理移动端-API")
public class VideoTutorialsController {

    @Autowired
    private VideoResourceQueryProvider videoResourceQueryProvider;

    @Autowired
    private VideoResourceCateQueryProvider videoResourceCateQueryProvider;



    /**
     * 查询后台视频教程
     */
    @ApiOperation(value = "查询视频教程默认后台视频教程")
    @RequestMapping(value = {"/default/data"}, method = RequestMethod.GET)
    public BaseResponse defaultList() {
        VideoResourcePageRequest pageReq = VideoResourcePageRequest.builder()
                .cateId("8888")
                .build();
        BaseResponse<VideoResourcePageResponse> response = videoResourceQueryProvider.defaultList
                (pageReq);
        return BaseResponse.success(response.getContext().getVideoResourceVOPage());
    }


    @ApiOperation(value = "分页查询视频教程素材分类")
    @PostMapping("/cateInfo/data")
    public BaseResponse<VideoResourceCateListResponse> getCateInfo(@RequestBody @Valid VideoResourceCatePageRequest pageReq) {
        if (Objects.nonNull(pageReq)) {
            int cateType = Objects.equals(pageReq.getCateType(), 0) ? 1 : pageReq.getCateType();
            pageReq.setCateType(cateType);
        }
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest.builder()
                .storeId(0L).cateGrade(1).cateType(pageReq.getCateType()).build();
        return videoResourceCateQueryProvider.list(queryRequest);
    }

    @ApiOperation(value = "分页查询视频教程素材分类")
    @PostMapping("/videoInfo/data")
    public BaseResponse<List<VideoResourceListResponse>> getVideoInfo(@RequestBody @Valid VideoResourceListRequest listRequest) {
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest.builder()
                .storeId(0L).cateGrade(2).cateParentId(listRequest.getCateId()).build();
        BaseResponse<VideoResourceCateListResponse> videoResourceCateResponse = videoResourceCateQueryProvider.list(queryRequest);
        log.info("videoResourceCateResponse--->:{}",videoResourceCateResponse);
        Map<String,String> cateMap = videoResourceCateResponse.getContext().getVideoResourceCateVOList().stream().collect(Collectors.toMap(VideoResourceCateVO::getCateId, t->t.getCateName()));
        listRequest.setCateMap(cateMap);
        log.info("listRequest--->:{}",listRequest);
        return videoResourceQueryProvider.list(listRequest);
    }


    /**
     * 查询视频分类以及各分类下的视频信息
     */
    @ApiOperation(value = "查询视频分类以及各分类下的视频信息")
    @RequestMapping(value = {"/cateInfo/data"}, method = RequestMethod.GET)
    public BaseResponse cateInfo() {
        List<Integer> cateGradeList = new ArrayList<>();
        cateGradeList.add(1);
        cateGradeList.add(2);
        //查询视频分类
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest.builder()
                .storeId(0L).cateGradeList(cateGradeList).isDefault(DefaultFlag.NO).build();
        BaseResponse<VideoResourceCateListResponse> cateResponse = videoResourceCateQueryProvider.list
                (queryRequest);
        if(Objects.isNull(cateResponse) && CollectionUtils.isEmpty(cateResponse.getContext().getVideoResourceCateVOList())){
            return BaseResponse.SUCCESSFUL();
        }
        //一级菜单
        List<String> firstCateIds = cateResponse.getContext().getVideoResourceCateVOList().stream().filter(t -> t.getCateGrade() == 1).map(VideoResourceCateVO::getCateId).collect(Collectors.toList());
        //二级菜单
        List<String> secondCateIds = cateResponse.getContext().getVideoResourceCateVOList().stream().filter(t -> t.getCateGrade() == 2).map(VideoResourceCateVO::getCateId).collect(Collectors.toList());
        List<String> cateIds;
        if(!CollectionUtils.isEmpty(secondCateIds)){
            cateIds = secondCateIds;
        }else{
            cateIds = firstCateIds;
        }
        //查询分类下的视频信息
        VideoResourcePageRequest pageReq = VideoResourcePageRequest.builder()
                .cateIds(cateIds)
                .build();
        BaseResponse<VideoResourceMapResponse> videoResponse = videoResourceQueryProvider.mapInfo
                (pageReq);
        log.info("查询返回的map:{}",videoResponse);
        if(Objects.isNull(videoResponse) && CollectionUtils.isEmpty(videoResponse.getContext().getVideoResourceVOMap())){
            return BaseResponse.SUCCESSFUL();
        }
        SystemResourceAppResponse resourceAppResponse = new SystemResourceAppResponse();
        resourceAppResponse.setVideoResourceCateVOList(cateResponse.getContext().getVideoResourceCateVOList());
        resourceAppResponse.setVideoResourceVOMap(videoResponse.getContext().getVideoResourceVOMap());
        return BaseResponse.success(resourceAppResponse);
    }

}
