package com.wanmi.sbc.setting.api.provider.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 信息发布查询API
 * @Author: lwp
 * @Version: 1.0
 */
@FeignClient(value = "${application.setting.name}",url="${feign.url.setting:#{null}}", contextId = "PublishInfoQueryProvider")
public interface PublishInfoQueryProvider {


    /**
     * 查询信息发布
     *
     */
    @PostMapping("/setting/${application.setting.version}/publishinfo/page")
    BaseResponse<PublishInfoResponse> getPublishInfo(@RequestBody PublishInfoRequest request);
}
