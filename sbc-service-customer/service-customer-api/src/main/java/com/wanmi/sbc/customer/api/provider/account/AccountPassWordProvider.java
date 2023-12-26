package com.wanmi.sbc.customer.api.provider.account;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.account.ResetAccountPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url = "${feign.url.customer:#{null}}", contextId = "AccountPassWordProvider")
public interface AccountPassWordProvider {


    @PostMapping("test/resetPassword")
    BaseResponse<String> resetPassword(@RequestBody @Valid ResetAccountPasswordRequest
                                               request);
}
