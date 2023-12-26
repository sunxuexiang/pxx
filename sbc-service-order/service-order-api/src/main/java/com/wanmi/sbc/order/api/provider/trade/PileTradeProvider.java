package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.purchase.PilePurchaseActionRequestVO;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.purchase.PilePurchaseActionResponse;
import com.wanmi.sbc.order.api.response.trade.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-03 17:24
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "PileTradeProvider")
public interface PileTradeProvider {

    /**
     * 新增交易单
     *
     * @param tradeAddWithOpRequest 交易单 操作信息 {@link TradeAddWithOpRequest}
     * @return 交易单 {@link TradeAddWithOpResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add-withOp")
    BaseResponse<TradeAddWithOpResponse> addWithOp(@RequestBody @Valid TradeAddWithOpRequest tradeAddWithOpRequest);

    /**
     * 批量新增交易单
     *
     * @param tradeAddBatchRequest 交易单集合 操作信息 {@link TradeAddBatchRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add-batch")
    BaseResponse<TradeAddBatchResponse> addBatch(@RequestBody @Valid TradeAddBatchRequest tradeAddBatchRequest);

    /**
     * 计算运费
     *
     * @param tradeAddBatchRequest 计算运费 {@link TradeAddBatchRequest}
     * @return 计算运费 {@link TradeAddBatchResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-delivery")
    BaseResponse<TradeWrapperBackendCommitListResponse> queryDelivery(@RequestBody @Valid TradeQueryDeliveryBatchRequest tradeAddBatchRequest);


    /**
     * 批量分组新增交易单
     *
     * @param tradeAddBatchWithGroupRequest 交易单集合 分组信息 操作信息 {@link TradeAddBatchWithGroupRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchWithGroupResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add-batch-with-group")
    BaseResponse<TradeAddBatchWithGroupResponse> addBatchWithGroup(@RequestBody @Valid TradeAddBatchWithGroupRequest tradeAddBatchWithGroupRequest);

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/commit")
    BaseResponse<TradeCommitResponse> commit(@RequestBody @Valid PileTradeCommitRequest tradeCommitRequest);

    /**
     * 取消订单
     *
     * @param tradeCancelRequest 店铺信息 交易单信息 操作信息 {@link TradeCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/cancel")
    BaseResponse cancel(@RequestBody @Valid TradeCancelRequest tradeCancelRequest);

    /**
     * 订单审核
     *
     * @param tradeAuditRequest 订单审核相关必要信息 {@link TradeAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/audit")
    BaseResponse audit(@RequestBody @Valid TradeAuditRequest tradeAuditRequest);

    /**
     * 新囤货订单审核
     * @param tradeAuditRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/newAudit")
    BaseResponse newAudit(@RequestBody @Valid TradeAuditRequest tradeAuditRequest);

    /**
     * 订单回审
     *
     * @param tradeRetrialRequest 订单审核相关必要信息 {@link TradeRetrialRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/retrial")
    BaseResponse retrial(@RequestBody @Valid TradeRetrialRequest tradeRetrialRequest);

    /**
     * 订单批量审核
     *
     * @param tradeAuditBatchRequest 订单审核相关必要信息 {@link TradeAuditBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/audit-batch")
    BaseResponse auditBatch(@RequestBody @Valid TradeAuditBatchRequest tradeAuditBatchRequest);

    /**
     * 修改卖家备注
     *
     * @param tradeRemedySellerRemarkRequest 订单修改相关必要信息 {@link TradeRemedySellerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/remedy-seller-remark")
    BaseResponse remedySellerRemark(@RequestBody @Valid TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest);

    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/deliver")
    BaseResponse<TradeDeliverResponse> deliver(@RequestBody @Valid TradeDeliverRequest tradeDeliverRequest);

    /**
     * 确认收货
     *
     * @param tradeConfirmReceiveRequest 订单编号 操作信息 {@link TradeConfirmReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/confirm-receive")
    BaseResponse confirmReceive(@RequestBody @Valid TradeConfirmReceiveRequest tradeConfirmReceiveRequest);

    /**
     * 退货 | 退款
     *
     * @param tradeReturnOrderRequest 订单编号 操作信息 {@link TradeReturnOrderRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/return-order")
    BaseResponse returnOrder(@RequestBody @Valid TradeReturnOrderRequest tradeReturnOrderRequest);

    /**
     * 作废订单
     *
     * @param tradeVoidedRequest 订单编号 操作信息 {@link TradeVoidedRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/voided")
    BaseResponse voided(@RequestBody @Valid TradeVoidedRequest tradeVoidedRequest);

    /**
     * 退单作废后的订单状态扭转
     *
     * @param tradeReverseRequest 订单编号 操作信息 {@link TradeReverseRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/reverse")
    BaseResponse reverse(@RequestBody @Valid TradeReverseRequest tradeReverseRequest);

    /**
     * 发货记录作废
     *
     * @param tradeDeliverRecordObsoleteRequest 订单编号 物流单号 操作信息 {@link TradeDeliverRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/deliver-record-obsolete")
    BaseResponse deliverRecordObsolete(@RequestBody @Valid TradeDeliverRecordObsoleteRequest tradeDeliverRecordObsoleteRequest);

    /**
     * 保存发票信息
     *
     * @param tradeAddInvoiceRequest 订单编号 发票信息 {@link TradeAddInvoiceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/save-invoice")
    BaseResponse saveInvoice(@RequestBody @Valid TradeAddInvoiceRequest tradeAddInvoiceRequest);

    /**
     * 支付作废
     *
     * @param tradePayRecordObsoleteRequest 订单编号 操作信息 {@link TradePayRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-pecord-obsolete")
    BaseResponse payRecordObsolete(@RequestBody @Valid TradePayRecordObsoleteRequest tradePayRecordObsoleteRequest);

    /**
     * 线上订单支付回调
     *
     * @param tradePayCallBackOnlineRequest 订单 支付单 操作信息 {@link TradePayCallBackOnlineRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-callBack-online")
    BaseResponse payCallBackOnline(@RequestBody @Valid TradePayCallBackOnlineRequest tradePayCallBackOnlineRequest);

    /**
     * 线上订单支付批量回调
     *
     * @param tradePayCallBackOnlineBatchRequest 订单 支付单 操作批量信息 {@link TradePayCallBackOnlineBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-callBack-online-batch")
    BaseResponse payCallBackOnlineBatch(@RequestBody @Valid TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest);

    /**
     * 订单支付回调
     *
     * @param tradePayCallBackRequest 订单号 支付金额 操作信息 支付方式{@link TradePayCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-callBack")
    BaseResponse payCallBack(@RequestBody @Valid TradePayCallBackRequest tradePayCallBackRequest);

    /**
     * 0 元订单默认支付
     *
     * @param tradeDefaultPayRequest 订单号{@link TradeDefaultPayRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/default-pay")
    BaseResponse<TradeDefaultPayResponse> defaultPay(@RequestBody @Valid TradeDefaultPayRequest tradeDefaultPayRequest);

    /**
     * 0 元订单默认批量支付
     *
     * @param tradeDefaultPayBatchRequest 订单号{@link TradeDefaultPayBatchRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/default-pay-batch")
    BaseResponse defaultPayBatch(@RequestBody @Valid TradeDefaultPayBatchRequest tradeDefaultPayBatchRequest);

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param tradeAddReceivableRequest 收款单平台信息{@link TradeAddReceivableRequest}
     * @return 支付单 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add-receivable")
    BaseResponse addReceivable(@RequestBody @Valid TradeAddReceivableRequest tradeAddReceivableRequest);

    /**
     * 确认支付单
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return 支付单集合 {@link TradeConfirmPayOrderResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/confirm-payOrder")
    BaseResponse confirmPayOrder(@RequestBody @Valid TradeConfirmPayOrderRequest tradeConfirmPayOrderRequest);

    /**
     * 线下确认支付单
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return 支付单集合 {@link TradeConfirmPayOrderResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/confirm-payOrder-offline")
    BaseResponse confirmPayOrderOffline(@RequestBody @Valid TradeConfirmPayOrderOfflineRequest tradeConfirmPayOrderRequest);

    /**
     * 根据支付状态统计订单
     *
     * @param tradeUpdateSettlementStatusRequest 查询参数 {@link TradeUpdateSettlementStatusRequest}
     * @return 支付单集合 {@link TradeCountByPayStateResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update-settlement-status")
    BaseResponse updateSettlementStatus(@RequestBody @Valid TradeUpdateSettlementStatusRequest tradeUpdateSettlementStatusRequest);


    /**
     * 添加订单
     *
     * @param tradeAddRequest 订单信息 {@link TradeAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add")
    BaseResponse add(@RequestBody @Valid TradeAddRequest tradeAddRequest);

    /**
     * 更新订单的业务员
     *
     * @param tradeUpdateEmployeeIdRequest 业务员信息 {@link TradeUpdateEmployeeIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update-employeeId")
    BaseResponse updateEmployeeId(@RequestBody @Valid TradeUpdateEmployeeIdRequest tradeUpdateEmployeeIdRequest);

    /**
     * 更新返利标志
     *
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update-commission-flag")
    BaseResponse updateCommissionFlag(@RequestBody @Valid TradeUpdateCommissionFlagRequest request);

    /**
     * 更新最终时间
     *
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update-final-time")
    BaseResponse updateFinalTime(@RequestBody @Valid TradeFinalTimeUpdateRequest request);

    /**
     * 更新正在进行的退单数量
     *
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update-return-order-num")
    BaseResponse updateReturnOrderNum(@RequestBody @Valid TradeReturnOrderNumUpdateRequest request);

    /**
     * 完善没有业务员的订单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/fill-employeeId")
    BaseResponse fillEmployeeId();

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/update")
    BaseResponse update(@RequestBody @Valid TradeUpdateRequest tradeUpdateRequest);


    /**
     * 更新订单
     *
     * @param tradeGetByIdRequest 订单信息 {@link TradeGetByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/push-wms-Order")
    BaseResponse pushConfirmOrder(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);


    @PostMapping("/order/${application.order.version}/pile/trade/send-pick-up-message")
    BaseResponse sendPickUpMessage(@RequestBody @Valid SendPickUpMessageRequest sendPickUpMessageRequest);

    @PostMapping("/order/${application.order.version}/pile/trade/updateTradeLogisticsCompany")
    BaseResponse updateTradeLogisticsCompany(@RequestBody @Valid updateTradeLogisticCompanyRequest updateTradeLogisticCompanyRequest);


    @PostMapping("/order/${application.order.version}/pile/trade/check-wms")
    BaseResponse checkWms(@RequestBody @Valid TradeCheckWmsRequest tradeCheckWmsRequest);

    /**
     * 订单支付回调处理
     *
     * @param tradePayOnlineCallBackRequest 订单信息 {@link TradePayOnlineCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-online-call-back")
    BaseResponse payOnlineCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    /**
     * 囤货订单支付回调处理
     *
     * @param tradePayOnlineCallBackRequest 订单信息 {@link TradePayOnlineCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/pay-pile-order-call-back")
    BaseResponse payPileOrderCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    @PostMapping("/order/${application.order.version}/pile/trade/pay-online-recharge-call-back")
    BaseResponse payOnlineRechargeCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    /**
     * 订单推送wms
     * @param tradePushRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/push-order-to-wms")
    BaseResponse pushOrderToWms(@RequestBody @Valid TradePushRequest tradePushRequest);

    /**
     * 添加合并支付标识
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/add-merge-pay")
    BaseResponse addMergePay(@RequestBody @Valid TradeAddMergePayRequest request);

    /**
     * 同步囤货订单总金额及商品优惠后单价
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/sync-pile-order-price")
    BaseResponse syncPileOrderPrice();
    /**
     * 清洗囤货历史数据
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/writing-historical-data")
    BaseResponse writingHistoricalData();

    /**
     * 余额支付回调
     */
    @PostMapping("/order/${application.order.version}/pile/trade/wallet-pay")
    BaseResponse walletPay(@RequestBody @Valid WalletPayRequest request);

    /**
     * 根据条件获取囤货明细总数
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/count-pile-purchase-action")
    BaseResponse<Long> countPilePurchaseAction(@RequestBody @Valid PilePurchaseActionRequestVO request);

    /**
     * 分页查询囤货明细数据
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/page-pile-purchase-action")
    BaseResponse<PilePurchaseActionResponse> pilePurchaseActionPage(@RequestBody @Valid PilePurchaseActionRequestVO request);

    /**
     * 批量保存囤货明细信息
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/batch-save-pile-purchase-action")
    BaseResponse batchSavePilePurchaseAction(@RequestBody @Valid PilePurchaseActionRequestVO request);
}
