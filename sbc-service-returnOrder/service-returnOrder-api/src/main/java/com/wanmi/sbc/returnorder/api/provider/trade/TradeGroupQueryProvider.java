package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.TradeGroupByIdsRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeGroupByGroupIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnTradeGroupQueryProvider")
public interface TradeGroupQueryProvider {

    /**
     * 查询订单组信息
     *
     * @param tradeGroupByIdsRequest 包装信息参数 {@link TradeGroupByIdsRequest}
     * @return 订单组信息 {@link TradeGroupByGroupIdsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-trade-group-by-group-ids")
    BaseResponse<TradeGroupByGroupIdsResponse> getTradeGroupByGroupIds(@RequestBody @Valid TradeGroupByIdsRequest tradeGroupByIdsRequest);
}
