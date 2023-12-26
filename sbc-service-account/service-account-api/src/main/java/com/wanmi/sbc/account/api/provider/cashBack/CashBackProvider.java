package com.wanmi.sbc.account.api.provider.cashBack;

import com.wanmi.sbc.account.api.request.cashBack.CashBackAddRequest;
import com.wanmi.sbc.account.api.request.cashBack.CashBackModifyRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsAddRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "CashBackProvider")
public interface CashBackProvider {

    /**
     * 新增返现记录
     *
     * @param request 新增返现记录 {@link CashBackAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/account/${application.account.version}/cashBack/add")
    BaseResponse add(@RequestBody @Valid CashBackAddRequest request);

    @PostMapping("/account/${application.account.version}/cashBack/modify")
    BaseResponse modify(@RequestBody @Valid CashBackModifyRequest request);
}
