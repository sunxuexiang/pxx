package com.wanmi.sbc.order.api.provider.suit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.purchase.PurchaseSaveRequest;
import com.wanmi.sbc.order.api.request.suit.SuitOrderTempQueryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "SuitOrderTempProvider")
public interface SuitOrderTempProvider {

    /**
     * 查询用户当前套装已购买数
     * @param request 查询用户当前套装已购买数 {@link SuitOrderTempQueryRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/suit/order/temp/getSuitBuyCountByCustomerAndMarketingId")
    BaseResponse<Integer> getSuitBuyCountByCustomerAndMarketingId(@RequestBody @Valid SuitOrderTempQueryRequest request);
}
