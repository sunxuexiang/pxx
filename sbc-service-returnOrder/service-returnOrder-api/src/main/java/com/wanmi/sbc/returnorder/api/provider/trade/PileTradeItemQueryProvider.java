package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemStockOutResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 囤货订单商品服务查询接口
 * @author: jiangxin
 * @create: 2021-09-30 16:01
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPileTradeItemQueryProvider")
public interface PileTradeItemQueryProvider {

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/trade/item/list-by-customer-id")
    BaseResponse<TradeItemByCustomerIdResponse> listByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                         request);

    @PostMapping("/returnOrder/${application.order.version}/pile/trade/item/list-stock-out-storeId")
    BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId(@RequestBody @Valid TradeItemStockOutRequest request);
}
