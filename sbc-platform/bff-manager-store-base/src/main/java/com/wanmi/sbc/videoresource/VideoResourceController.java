package com.wanmi.sbc.videoresource;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceQueryProvider;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourcePageRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateListRequest;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourcePageResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 视频资源管理API
 * @author hudong
 * @date 2023-06-26 14:28:22
 */
@Api(description = "视频资源管理API", tags = "VideoResourceController")
@RestController
@RequestMapping(value = "/videoResource")
public class VideoResourceController {

    @Autowired
    private VideoResourceQueryProvider videoResourceQueryProvider;


    /**
     * 分页视频教程素材
     *
     * @param pageReq 视频教程素材参数
     * @return
     */
    @ApiOperation(value = "分页视频教程素材")
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public BaseResponse page(@RequestBody @Valid VideoResourcePageRequest pageReq) {

        pageReq.setStoreId(0L);
        BaseResponse<VideoResourcePageResponse> response = videoResourceQueryProvider.page(pageReq);
        return BaseResponse.success(response.getContext().getVideoResourceVOPage());
    }

    /**
     * 查询后台视频教程
     */
    @ApiOperation(value = "查询视频教程默认后台视频教程")
    @RequestMapping(value = {"/default/resources"}, method = RequestMethod.GET)
    public BaseResponse defaultList() {
        VideoResourcePageRequest pageReq = VideoResourcePageRequest.builder()
                .cateId("9999")
                .build();
        BaseResponse<VideoResourcePageResponse> response = videoResourceQueryProvider.defaultList
                (pageReq);
        return BaseResponse.success(response.getContext().getVideoResourceVOPage());
    }


}
