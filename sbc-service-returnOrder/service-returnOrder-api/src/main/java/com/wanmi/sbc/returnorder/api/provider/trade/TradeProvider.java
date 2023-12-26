package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.orderpicking.OrderPickingRequest;
import com.wanmi.sbc.returnorder.api.response.orderpicking.OrderPickingListResponse;
import com.wanmi.sbc.returnorder.api.response.orderpicking.OrderPickingResponse;
import com.wanmi.sbc.returnorder.bean.dto.MarketTradeInfoDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeDeliverUpdateDTO;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-03 17:24
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnTradeProvider")
public interface TradeProvider {

    /**
     * 新增交易单
     *
     * @param tradeAddWithOpRequest 交易单 操作信息 {@link TradeAddWithOpRequest}
     * @return 交易单 {@link TradeAddWithOpResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add-withOp")
    BaseResponse<TradeAddWithOpResponse> addWithOp(@RequestBody @Valid TradeAddWithOpRequest tradeAddWithOpRequest);

    /**
     * 批量新增交易单
     *
     * @param tradeAddBatchRequest 交易单集合 操作信息 {@link TradeAddBatchRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add-batch")
    BaseResponse<TradeAddBatchResponse> addBatch(@RequestBody @Valid TradeAddBatchRequest tradeAddBatchRequest);

    /**
     * 计算运费
     *
     * @param tradeAddBatchRequest 计算运费 {@link TradeAddBatchRequest}
     * @return 计算运费 {@link TradeAddBatchResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-delivery")
    BaseResponse<TradeWrapperBackendCommitListResponse> queryDelivery(@RequestBody @Valid TradeQueryDeliveryBatchRequest tradeAddBatchRequest);


    /**
     * 批量分组新增交易单
     *
     * @param tradeAddBatchWithGroupRequest 交易单集合 分组信息 操作信息 {@link TradeAddBatchWithGroupRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchWithGroupResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add-batch-with-group")
    BaseResponse<TradeAddBatchWithGroupResponse> addBatchWithGroup(@RequestBody @Valid TradeAddBatchWithGroupRequest tradeAddBatchWithGroupRequest);

    /**
     * 订单改价
     *
     * @param tradeModifyPriceRequest 价格信息 交易单号 操作信息 {@link TradeModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/modify-price")
    BaseResponse modifyPrice(@RequestBody @Valid TradeModifyPriceRequest tradeModifyPriceRequest);

    /**
     * 订单明细价格修改
     *
     * @param tradeModifyPriceRequest 价格信息 交易单号 操作信息 {@link TradeModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/change-item-price")
    BaseResponse changeItemPrice(@RequestBody @Valid TradeModifyPriceRequest tradeModifyPriceRequest);

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/commit")
    BaseResponse<TradeCommitResponse> commit(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/devanningcommit")
    BaseResponse<TradeCommitResponse> devanningCommit(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);

    /**
     * C端提交订单（批发+零售）
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/commit-all")
    BaseResponse<TradeCommitResponse> commitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);


    /**
     * C端提交订单（批发+零售+拆箱）
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade1/commit-devanning-all")
    BaseResponse<TradeCommitResponse> devanningcommitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/takeGoods")
    BaseResponse<TradeCommitResponse> takeGoods(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);
    /**
     * 移动端提交积分商品订单
     *
     * @param pointsTradeCommitRequest 提交订单请求对象  {@link PointsTradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/points-commit")
    BaseResponse<PointsTradeCommitResponse> pointsCommit(@RequestBody @Valid PointsTradeCommitRequest pointsTradeCommitRequest);

    /**
     * 移动端提交积分优惠券订单
     *
     * @param pointsTradeCommitRequest 提交订单请求对象  {@link PointsTradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/points-coupon-commit")
    BaseResponse<PointsTradeCommitResponse> pointsCouponCommit(@RequestBody @Valid PointsCouponTradeCommitRequest pointsTradeCommitRequest);

    /**
     * 修改订单
     *
     * @param tradeRemedyRequest 店铺信息 交易单信息 操作信息 {@link TradeModifyRemedyRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/remedy")
    BaseResponse remedy(@RequestBody @Valid TradeModifyRemedyRequest tradeRemedyRequest);

    /**
     * 修改订单-部分修改
     *
     * @param tradeRemedyPartRequest 店铺信息 交易单信息 操作信息 {@link TradeRemedyPartRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/remedy-part")
    BaseResponse remedyPart(@RequestBody @Valid TradeRemedyPartRequest tradeRemedyPartRequest);

    /**
     * 取消订单
     *
     * @param tradeCancelRequest 店铺信息 交易单信息 操作信息 {@link TradeCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/cancel")
    BaseResponse cancel(@RequestBody @Valid TradeCancelRequest tradeCancelRequest);

    /**
     * 订单审核
     *
     * @param tradeAuditRequest 订单审核相关必要信息 {@link TradeAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/audit")
    BaseResponse audit(@RequestBody @Valid TradeAuditRequest tradeAuditRequest);

    /**
     * 订单回审
     *
     * @param tradeRetrialRequest 订单审核相关必要信息 {@link TradeRetrialRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/retrial")
    BaseResponse retrial(@RequestBody @Valid TradeRetrialRequest tradeRetrialRequest);

    /**
     * 订单批量审核
     *
     * @param tradeAuditBatchRequest 订单审核相关必要信息 {@link TradeAuditBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/audit-batch")
    BaseResponse auditBatch(@RequestBody @Valid TradeAuditBatchRequest tradeAuditBatchRequest);

    /**
     * 修改卖家备注
     *
     * @param tradeRemedySellerRemarkRequest 订单修改相关必要信息 {@link TradeRemedySellerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/remedy-seller-remark")
    BaseResponse remedySellerRemark(@RequestBody @Valid TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest);


    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/deliver")
    BaseResponse<TradeDeliverResponse> deliver(@RequestBody @Valid TradeDeliverRequest tradeDeliverRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/thirdPartyDeliver")
    BaseResponse tmsDeliver(@RequestBody @Valid TmsDeliverRequest tmsDeliverRequest);

    /**
     * @desc  修改发货
     * @author shiy  2023/6/19 20:14
    */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-deliver")
    BaseResponse<TradeDeliverResponse> updateLogistics(@RequestBody @Valid TradeDeliverUpdateDTO tradeDeliverUpdateDTO);

    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/deliver2")
    BaseResponse<TradeDeliverResponse> deliver2(@RequestBody @Valid TradeDeliverRequest tradeDeliverRequest);

    /**
     * 确认收货
     *
     * @param tradeConfirmReceiveRequest 订单编号 操作信息 {@link TradeConfirmReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/confirm-receive")
    BaseResponse confirmReceive(@RequestBody @Valid TradeConfirmReceiveRequest tradeConfirmReceiveRequest);

    /**
     * 退货 | 退款
     *
     * @param tradeReturnOrderRequest 订单编号 操作信息 {@link TradeReturnOrderRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/return-order")
    BaseResponse returnOrder(@RequestBody @Valid TradeReturnOrderRequest tradeReturnOrderRequest);

    /**
     * 作废订单
     *
     * @param tradeVoidedRequest 订单编号 操作信息 {@link TradeVoidedRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/voided")
    BaseResponse voided(@RequestBody @Valid TradeVoidedRequest tradeVoidedRequest);

    /**
     * 退单作废后的订单状态扭转
     *
     * @param tradeReverseRequest 订单编号 操作信息 {@link TradeReverseRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/reverse")
    BaseResponse reverse(@RequestBody @Valid TradeReverseRequest tradeReverseRequest);

    /**
     * 发货记录作废
     *
     * @param tradeDeliverRecordObsoleteRequest 订单编号 物流单号 操作信息 {@link TradeDeliverRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/deliver-record-obsolete")
    BaseResponse deliverRecordObsolete(@RequestBody @Valid TradeDeliverRecordObsoleteRequest tradeDeliverRecordObsoleteRequest);

    /**
     * 保存发票信息
     *
     * @param tradeAddInvoiceRequest 订单编号 发票信息 {@link TradeAddInvoiceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/save-invoice")
    BaseResponse saveInvoice(@RequestBody @Valid TradeAddInvoiceRequest tradeAddInvoiceRequest);

    /**
     * 支付作废
     *
     * @param tradePayRecordObsoleteRequest 订单编号 操作信息 {@link TradePayRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-pecord-obsolete")
    BaseResponse payRecordObsolete(@RequestBody @Valid TradePayRecordObsoleteRequest tradePayRecordObsoleteRequest);

    /**
     * 线上订单支付回调
     *
     * @param tradePayCallBackOnlineRequest 订单 支付单 操作信息 {@link TradePayCallBackOnlineRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-callBack-online")
    BaseResponse payCallBackOnline(@RequestBody @Valid TradePayCallBackOnlineRequest tradePayCallBackOnlineRequest);

    /**
     * 线上订单支付批量回调
     *
     * @param tradePayCallBackOnlineBatchRequest 订单 支付单 操作批量信息 {@link TradePayCallBackOnlineBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-callBack-online-batch")
    BaseResponse payCallBackOnlineBatch(@RequestBody @Valid TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/pay-callBack-take-goods-online-batch")
    BaseResponse payTakeGoodCallBackOnlineBatch(@RequestBody @Valid TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest);
    /**
     * 订单支付回调
     *
     * @param tradePayCallBackRequest 订单号 支付金额 操作信息 支付方式{@link TradePayCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-call-back")
    BaseResponse payCallBack(@RequestBody @Valid TradePayCallBackRequest tradePayCallBackRequest);

    /**
     * 0 元订单默认支付
     *
     * @param tradeDefaultPayRequest 订单号{@link TradeDefaultPayRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/default-pay")
    BaseResponse<TradeDefaultPayResponse> defaultPay(@RequestBody @Valid TradeDefaultPayRequest tradeDefaultPayRequest);

    /**
     * 提货 0元订单默认支付
     *
     * @param tradeDefaultPayRequest 订单号{@link TradeDefaultPayRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pile-default-pay")
    BaseResponse<TradeDefaultPayResponse> pileDefaultPay(@RequestBody @Valid TradeDefaultPayRequest tradeDefaultPayRequest);

    /**
     * 0 元订单默认批量支付
     *
     * @param tradeDefaultPayBatchRequest 订单号{@link TradeDefaultPayBatchRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/default-pay-batch")
    BaseResponse defaultPayBatch(@RequestBody @Valid TradeDefaultPayBatchRequest tradeDefaultPayBatchRequest);

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param tradeAddReceivableRequest 收款单平台信息{@link TradeAddReceivableRequest}
     * @return 支付单 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add-receivable")
    BaseResponse addReceivable(@RequestBody @Valid TradeAddReceivableRequest tradeAddReceivableRequest);

    /**
     * 确认支付单
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return 支付单集合 {@link TradeConfirmPayOrderResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/confirm-payOrder")
    BaseResponse confirmPayOrder(@RequestBody @Valid TradeConfirmPayOrderRequest tradeConfirmPayOrderRequest);

    /**
     * 线下确认支付单
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return 支付单集合 {@link TradeConfirmPayOrderResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/confirm-payOrder-offline")
    BaseResponse confirmPayOrderOffline(@RequestBody @Valid TradeConfirmPayOrderOfflineRequest tradeConfirmPayOrderRequest);

    /**
     * 根据支付状态统计订单
     *
     * @param tradeUpdateSettlementStatusRequest 查询参数 {@link TradeUpdateSettlementStatusRequest}
     * @return 支付单集合 {@link TradeCountByPayStateResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-settlement-status")
    BaseResponse updateSettlementStatus(@RequestBody @Valid TradeUpdateSettlementStatusRequest tradeUpdateSettlementStatusRequest);


    /**
     * 添加订单
     *
     * @param tradeAddRequest 订单信息 {@link TradeAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add")
    BaseResponse add(@RequestBody @Valid TradeAddRequest tradeAddRequest);

    /**
     * 更新订单的业务员
     *
     * @param tradeUpdateEmployeeIdRequest 业务员信息 {@link TradeUpdateEmployeeIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-employeeId")
    BaseResponse updateEmployeeId(@RequestBody @Valid TradeUpdateEmployeeIdRequest tradeUpdateEmployeeIdRequest);

    /**
     * 更新返利标志
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-commission-flag")
    BaseResponse updateCommissionFlag(@RequestBody @Valid TradeUpdateCommissionFlagRequest request);

    /**
     * 更新最终时间
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-final-time")
    BaseResponse updateFinalTime(@RequestBody @Valid TradeFinalTimeUpdateRequest request);

    /**
     * 更新正在进行的退单数量
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update-return-order-num")
    BaseResponse updateReturnOrderNum(@RequestBody @Valid TradeReturnOrderNumUpdateRequest request);

    /**
     * 完善没有业务员的订单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/fill-employeeId")
    BaseResponse fillEmployeeId();

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/update")
    BaseResponse update(@RequestBody @Valid TradeUpdateRequest tradeUpdateRequest);


    /**
     * 更新订单
     *
     * @param tradeGetByIdRequest 订单信息 {@link TradeGetByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/push-wms-Order")
    BaseResponse pushConfirmOrder(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);


    @PostMapping("/returnOrder/${application.order.version}/trade/send-pick-up-message")
    BaseResponse sendPickUpMessage(@RequestBody @Valid SendPickUpMessageRequest sendPickUpMessageRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/updateTradeLogisticsCompany")
    BaseResponse updateTradeLogisticsCompany(@RequestBody @Valid updateTradeLogisticCompanyRequest updateTradeLogisticCompanyRequest);


    @PostMapping("/returnOrder/${application.order.version}/trade/check-wms")
    BaseResponse checkWms(@RequestBody @Valid TradeCheckWmsRequest tradeCheckWmsRequest);

    /**
     * 订单支付回调处理
     *
     * @param tradePayOnlineCallBackRequest 订单信息 {@link TradePayOnlineCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-online-call-back")
    BaseResponse payOnlineCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/pay-online-recharge-call-back")
    BaseResponse payOnlineRechargeCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    /**
     * 囤货的提货
     * @param tradePayOnlineCallBackRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pay-online-take-good-call-back")
    BaseResponse payOnlineTakeGoodCallBack(@RequestBody @Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest);

    /**
     * 订单推送wms
     * @param tradePushRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/push-order-to-wms")
    BaseResponse pushOrderToWms(@RequestBody @Valid TradePushRequest tradePushRequest);

    /**
     * @desc  订单推送tms
     * @author shiy  2023/9/20 9:05
    */
    @PostMapping("/returnOrder/${application.order.version}/trade/push-order-to-tms")
    BaseResponse pushOrderToTms(@RequestBody @Valid TradePushRequest tradePushRequest);

    /**
     * @desc  订单取消推送tms
     * @author shiy  2023/9/20 9:05
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/cancel-order-to-tms")
    BaseResponse cancelOrderToTms(@RequestBody @Valid TradePushRequest tradePushRequest);

    /**
     * 补偿push金蝶失败的单子
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/push-kingdee-compensation-order")
    BaseResponse pushKingdeeCompensationOrder(@RequestBody @Valid TradePushKingdeeOrderRequest request);

    /**
     * 添加合并支付标识
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/add-merge-pay")
    BaseResponse addMergePay(@RequestBody @Valid TradeAddMergePayRequest request);


    @PostMapping("/returnOrder/${application.order.version}/trade/push-cache-push-kingdee-order")
    BaseResponse pushCachePushKingdeeOrder();

    @PostMapping("/returnOrder/${application.order.version}/trade/push-order-to-wms2")
    BaseResponse pushOrderToWms2(@RequestBody @Valid TradePushRequest tradePushRequest);

    /**
     * 定时补偿失败销售单
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/compensate-failed-sales-orders")
    BaseResponse compensateFailedSalesOrders();

    /**
     * 删除提货明细记录
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/stockup/delete-by-create-time")
    BaseResponse deleteStockupAction(@RequestBody @Valid StockupActionDeleteRequest request);

    /**
     * 批量新增提货明细记录
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/stockup/batch-add")
    BaseResponse batchAddStockupAction(@RequestBody @Valid BatchAddStockupActionRequest request);

    /**
     * 新线下订单确认收款
     * @param payOrderOperateRequest
     */
    @PostMapping("/returnOrder/${application.order.version}/stockup/newConfirmPayOrderOffline")
    BaseResponse newConfirmPayOrderOffline(@RequestBody @Valid TradeConfirmPayOrderOfflineRequest payOrderOperateRequest);


    /**
     * 提现申请推送金蝶收款退款单
     * @param ticketsFormPushPayOrderKingdee
     */
    @PostMapping("/returnOrder/${application.order.version}/ticketsForm/pushPayOrderKingdee")
    BaseResponse ticketsFormPushPayOrderKingdee(@RequestBody @Valid TicketsFormPushPayOrderKingdeeRequest ticketsFormPushPayOrderKingdee);


    /**
     * 满多少件返2*x推送收款单
     * @param addWalletRecordRequest
     */
    @PostMapping("/returnOrder/${application.order.version}/balanceForm/pushPayOrderKingdee")
    BaseResponse balanceFormPushPayOrderKingdee(@RequestBody @Valid AddWalletRecordRequest addWalletRecordRequest);


    /**
     * 查询订单电商拣货状态
     * @param request
     */
    @PostMapping("/returnOrder/${application.order.version}/orderPicking/add")
    BaseResponse orderPicking(@RequestBody OrderPickingRequest request);

    /**
     * 查询订单电商拣货状态
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/orderPicking/list")
    BaseResponse<OrderPickingListResponse> queryOrderPicking(@RequestBody OrderPickingRequest request);

    /**
     * 根据tid查询电商拣货状态
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/orderPicking/tid")
    BaseResponse<OrderPickingResponse> getOrderPickingByTid(@RequestBody OrderPickingRequest request);

    /**
     * 赠送金币推送金蝶
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pushRefundOrderKingdeeForCoin")
    BaseResponse pushRefundOrderKingdeeForCoin(@RequestBody CoinActivityPushKingdeeRequest request);

    /**
     * 退还金币推送金蝶
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/pushOrderKingdeeForCoin")
    BaseResponse pushOrderKingdeeForCoin(@RequestBody CoinActivityPushKingdeeRequest request);

    /**
     * 商城提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/submit_mall_trades")
    BaseResponse<TradeCommitResponse> submitMallTrades(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);

    /**
     * 退还金币推送金蝶
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/trade-dilvery-area-template")
    BaseResponse<TradeDeliveryWayResponse> getTradeDiliveryAreaVo(@RequestBody TradeDeliveryWayRequest request);
    
    /**
     * @desc  初始化历史数据
     * @author shiy  2023/8/24 11:41
    */
    @GetMapping("/returnOrder/${application.order.version}/trade/initHistoryTradeInfo")
    BaseResponse initHistoryTradeInfo(@RequestParam(value="flag")Integer flag);

    @PostMapping("/returnOrder/${application.order.version}/trade/checkSubmitTradeDeliveryToStore")
    BaseResponse checkSubmitTradeDeliveryToStore(@RequestBody MarketTradeInfoDTO marketTradeInfoDTO);
}
