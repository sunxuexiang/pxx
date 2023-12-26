package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatMarkRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceChatMarkProvider")
public interface CustomerServiceChatMarkProvider {

    @PostMapping("/setting/${application.setting.version}/chatMark/add")
    BaseResponse add(@RequestBody CustomerServiceChatMarkRequest request);

    @PostMapping("/setting/${application.setting.version}/chatMark/delete")
    BaseResponse delete(@RequestBody CustomerServiceChatMarkRequest request);
}
