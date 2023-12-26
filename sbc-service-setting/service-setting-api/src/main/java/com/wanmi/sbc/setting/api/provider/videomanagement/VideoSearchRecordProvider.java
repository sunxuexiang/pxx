package com.wanmi.sbc.setting.api.provider.videomanagement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementPageRequest;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoSearchRecordResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>短视频搜索记录</p>
 * @author zzg
 * @date 2023-09-17 17:47:22
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoSearchRecordProvider")
public interface VideoSearchRecordProvider {

    @PostMapping("/setting/${application.setting.version}/videoSearch/getSearchHistory")
    BaseResponse<MicroServicePage<VideoSearchRecordResponse>> getSearchHistory(@RequestBody VideoManagementPageRequest request);
}
