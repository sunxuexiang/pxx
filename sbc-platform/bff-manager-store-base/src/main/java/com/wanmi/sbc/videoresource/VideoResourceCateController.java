package com.wanmi.sbc.videoresource;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateCheckChildRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateCheckResourceRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateListRequest;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateListResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频教程资源管理API
 * @author hudong
 * @date 2023-06-26 14:28:22
 */
@Api(description = "视频教程资源管理API", tags = "VideoResourceCateController")
@RestController
@RequestMapping(value = "/videoResourceCate")
public class VideoResourceCateController {
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private VideoResourceCateQueryProvider videoResourceCateQueryProvider;


    /**
     * 查询视频教程素材分类
     */
    @ApiOperation(value = "查询视频教程素材分类")
    @RequestMapping(value = {"/video/resourceCates"}, method = RequestMethod.GET)
    public BaseResponse<VideoResourceCateListResponse> list() {
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest.builder()
                .storeId(0L).build();
        BaseResponse<VideoResourceCateListResponse> response = videoResourceCateQueryProvider.list
                (queryRequest);
        return response;
    }

    /**
     * 查询后台视频教程素材分类
     */
    @ApiOperation(value = "查询视频教程默认素材分类后台视频教程")
    @RequestMapping(value = {"/video/default/resourceCates"}, method = RequestMethod.GET)
    public BaseResponse<VideoResourceCateListResponse> defaultList() {
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest
                .builder()
                .storeId(0L)
                .build();
        queryRequest.setCateId("9999");
        BaseResponse<VideoResourceCateListResponse> response = videoResourceCateQueryProvider.defaultList
                (queryRequest);
        return response;
    }


    /**
     * 检测视频教程素材分类是否有子类
     */
    @ApiOperation(value = "检测视频教程素材分类是否有子类")
    @RequestMapping(value = "/video/resourceCate/child", method = RequestMethod.POST)
    public BaseResponse checkChild(@RequestBody VideoResourceCateCheckChildRequest request) {
        if (request == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setStoreId(commonUtil.getStoreId());
        BaseResponse<Integer> response =videoResourceCateQueryProvider.checkChild(request);
        return response;
    }


    /**
     * 检测视频教程素材分类是否已关联素材
     */
    @ApiOperation(value = "检测视频教程素材分类是否已关联素材")
    @RequestMapping(value = "/video/resourceCate/resource", method = RequestMethod.POST)
    public BaseResponse checkResource(@RequestBody VideoResourceCateCheckResourceRequest request) {
        if (request == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        request.setStoreId(commonUtil.getStoreId());
        BaseResponse<Integer> response =videoResourceCateQueryProvider.checkResource(request);
        return BaseResponse.success(response);
    }







}
