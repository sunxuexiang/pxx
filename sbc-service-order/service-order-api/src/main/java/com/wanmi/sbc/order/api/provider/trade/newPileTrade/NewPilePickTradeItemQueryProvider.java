package com.wanmi.sbc.order.api.provider.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.FindPickTradeRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPickTradeResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemStockOutResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>订单商品服务查询接口</p>
 * @Author: daiyitian
 * @Description: 退单服务查询接口
 * @Date: 2018-12-03 15:40
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "NewPilePickTradeItemQueryProvider")
public interface NewPilePickTradeItemQueryProvider {

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/item/list-by-customer-id")
    BaseResponse<TradeItemByCustomerIdResponse> listByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                         request);

    /**
     * 根据客户id查询已确认订单商品快照（批发+零售）
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/item/list-all-by-customer-id")
    BaseResponse<TradeItemByCustomerIdResponse> listAllByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                            request);

    @PostMapping("/order/${application.order.version}/newPilePickTrade/item/list-stock-out-storeId")
    BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId(@RequestBody @Valid TradeItemStockOutRequest request);

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/item/item_list-by-customer-id")
    BaseResponse<TradeItemByCustomerIdResponse> itemListByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                             request);

    /**
     * 保存订单商品快照(批发+零售)
     *
     * @param request 保存订单商品快照请求结构 {@link TradeItemSnapshotRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/item/snapshot")
    BaseResponse snapshotRetail(@RequestBody @Valid TradeItemSnapshotRequest request);

    /**
     * 根据囤货订单id查询提货记录
     * @param request 根据囤货订单id查询提货记录请求结构 {@link FindPickTradeRequest}
     * @return 提货记录列表 {@link FindPickTradeResponse}
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/list_pick_trades_by_pile_trade_id")
    BaseResponse<FindPickTradeResponse> findPickTradeList(@RequestBody @Valid FindPickTradeRequest request);

}
