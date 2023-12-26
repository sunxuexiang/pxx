package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.TradeGroupByIdsRequest;
import com.wanmi.sbc.order.api.response.trade.PileTradeGroupByGroupIdsResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGroupByGroupIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "PileTradeGroupQueryProvider")
public interface PileTradeGroupQueryProvider {

    /**
     * 查询订单组信息
     *
     * @param tradeGroupByIdsRequest 包装信息参数 {@link TradeGroupByIdsRequest}
     * @return 订单组信息 {@link TradeGroupByGroupIdsResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-trade-group-by-group-ids")
    BaseResponse<TradeGroupByGroupIdsResponse> getTradeGroupByGroupIds(@RequestBody @Valid TradeGroupByIdsRequest tradeGroupByIdsRequest);
}
