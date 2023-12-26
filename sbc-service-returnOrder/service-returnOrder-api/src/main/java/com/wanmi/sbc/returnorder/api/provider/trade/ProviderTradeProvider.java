package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeProviderResponse;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.FindProviderTradeResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeDeliverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 供应商订单处理
 * @Autho qiaokang
 * @Date：2020-03-27 09:08
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnProviderTradeProvider")
public interface ProviderTradeProvider {

    /**
     * 更新供应商订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/provider-update")
    BaseResponse providerUpdate(@RequestBody @Valid TradeUpdateRequest tradeUpdateRequest);

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tradeDeliveryCheckRequest 订单号 物流信息 {@link TradeDeliveryCheckRequest}
     * @return 处理结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/provider-delivery-check")
    BaseResponse providerDeliveryCheck(@RequestBody @Valid TradeDeliveryCheckRequest tradeDeliveryCheckRequest);

    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/provider-deliver")
    BaseResponse<TradeDeliverResponse> providerDeliver(@RequestBody @Valid TradeDeliverRequest tradeDeliverRequest);

    /**
     * 修改备注
     *
     * @param providerTradeRemedyBuyerRemarkRequest 订单修改相关必要信息 {@link ProviderTradeRemedyBuyerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/provider-remedy-seller-remark")
    BaseResponse remedyBuyerRemark(@RequestBody @Valid ProviderTradeRemedyBuyerRemarkRequest providerTradeRemedyBuyerRemarkRequest);

    /** 查询子订单
     * @param findProviderTradeRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/findByParentId")
    BaseResponse<FindProviderTradeResponse> findByParentIdList(@RequestBody @Valid FindProviderTradeRequest findProviderTradeRequest);

    /**
     *
     * @param tradeGetByIdAndPidRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/provider-by-id-pid")
    BaseResponse<TradeProviderResponse> providerByidAndPid(@RequestBody @Valid TradeGetByIdAndPidRequest tradeGetByIdAndPidRequest);


    /**
     * 发货记录作废
     *
     * @param tradeDeliverRecordObsoleteRequest 订单编号 物流单号 操作信息 {@link TradeDeliverRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/provider/trade/deliver-record-obsolete")
    BaseResponse deliverRecordObsolete(@RequestBody @Valid TradeDeliverRecordObsoleteRequest tradeDeliverRecordObsoleteRequest);

}
