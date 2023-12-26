package com.wanmi.sbc.account.api.provider.cashBack;

import com.wanmi.sbc.account.api.request.cashBack.CashBackPageRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsPageRequest;
import com.wanmi.sbc.account.api.response.cashBack.CashBackPageResponse;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsPageResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "CashBackQueryProvider")
public interface CashBackQueryProvider {

    @PostMapping("/account/${application.account.version}/cashBack/page")
    BaseResponse<CashBackPageResponse> page(@RequestBody @Valid CashBackPageRequest request);
}
