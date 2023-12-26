package com.wanmi.sbc.customer.api.provider.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.customer.CustomerDetailPageRequest;
import com.wanmi.sbc.customer.api.request.customer.PushNotifyRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "PushNotifyProvider")
public interface PushNotifyProvider {

    @PostMapping("/customer/${application.customer.version}/customer/pushNotify")
    BaseResponse pushNotify(@RequestBody PushNotifyRequest request);
}
