package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoSearchRecordProvider;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementPageRequest;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoSearchRecordResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>视频搜索记录接口<p>
 * @author zzg
 * @date 2023/7/19
 */
@Api(description = "视频搜索API", tags = "VideoSearchRecordController")
@RestController
@RequestMapping(value = "/videoSearch")
public class VideoSearchRecordController {

    @Autowired
    private VideoSearchRecordProvider videoSearchRecordProvider;

    @ApiOperation(value = "查询搜索历史")
    @PostMapping("/history")
    public BaseResponse<MicroServicePage<VideoSearchRecordResponse>> getSearchHistory (@RequestBody VideoManagementPageRequest request) {
        if (StringUtils.isEmpty(request.getCustomerId())) {
            return BaseResponse.error("参数错误");
        }
        return videoSearchRecordProvider.getSearchHistory(request);
    }

}
