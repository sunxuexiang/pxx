package com.wanmi.sbc.returnorder.api.provider.suit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.suit.SuitOrderTempQueryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnSuitOrderTempProvider")
public interface SuitOrderTempProvider {

    /**
     * 查询用户当前套装已购买数
     * @param request 查询用户当前套装已购买数 {@link SuitOrderTempQueryRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/suit/returnOrder/temp/getSuitBuyCountByCustomerAndMarketingId")
    BaseResponse<Integer> getSuitBuyCountByCustomerAndMarketingId(@RequestBody @Valid SuitOrderTempQueryRequest request);
}
