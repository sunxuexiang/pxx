package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemDeleteByCustomerIdRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemStockOutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>订单商品服务操作接口</p>
 * @Author: daiyitian
 * @Description: 退单服务操作接口
 * @Date: 2018-12-03 15:40
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPileTradeItemProvider")
public interface PileTradeItemProvider {

    /**
     * 保存订单商品快照
     *
     * @param request 保存订单商品快照请求结构 {@link TradeItemSnapshotRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pileTrade/item/snapshot")
    BaseResponse snapshot(@RequestBody @Valid TradeItemSnapshotRequest request);

    /**
     * 根据customerId删除订单商品快照
     *
     * @param request 根据customerId删除订单商品快照请求结构 {@link TradeItemDeleteByCustomerIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pileTrade/item/delete-by-customer-id")
    BaseResponse deleteByCustomerId(@RequestBody @Valid TradeItemDeleteByCustomerIdRequest request);

    @PostMapping("/returnOrder/${application.order.version}/pileTrade/item/stock-zero-out")
    BaseResponse updateUnStock(@RequestBody @Valid TradeItemStockOutRequest request);
}
