package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.FindProviderTradeRequest;
import com.wanmi.sbc.returnorder.api.response.trade.FindProviderTradeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 查询商品子订单
 * @Autho caiping
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnProviderSonTradeProvider")
public interface ProviderSonTradeProvider {

    /**
     * 查询子订单
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/provider/son-trade")
    BaseResponse<FindProviderTradeResponse> findSonTradeByParentId(@RequestBody @Valid FindProviderTradeRequest request);
}
