package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.returnorder.api.request.purchase.PilePurchaseActionRequestVO;
import com.wanmi.sbc.returnorder.api.response.purchase.PilePurchaseActionResponse;
import com.wanmi.sbc.returnorder.bean.enums.PayCallBackType;
import com.wanmi.sbc.returnorder.bean.vo.PilePurchaseActionVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeCommitResultVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseAction;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRequest;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionService;
import com.wanmi.sbc.returnorder.receivables.request.ReceivableAddRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.PayCallBackOnlineBatchPile;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeCommitResult;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Invoice;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.PileTradeGroup;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.PileStoringHistoricalDataService;
import com.wanmi.sbc.returnorder.trade.service.PileTradeService;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 10:04
 */
@Validated
@RestController
public class PileTradeController implements PileTradeProvider {

    @Autowired
    private PileTradeService tradeService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private PileStoringHistoricalDataService pileStoringHistoricalDataService;

    @Autowired
    private PilePurchaseActionService pilePurchaseActionService;

    /**
     * 新增交易单
     *
     * @param tradeAddWithOpRequest 交易单 操作信息 {@link TradeAddWithOpRequest}
     * @return 交易单 {@link TradeAddWithOpResponse}
     */
    @Override
    public BaseResponse<TradeAddWithOpResponse> addWithOp(@RequestBody @Valid TradeAddWithOpRequest tradeAddWithOpRequest) {
        PileTrade trade = KsBeanUtil.convert(tradeAddWithOpRequest.getTrade(), PileTrade.class);
        trade.setParentId(generatorService.generatePoId());
        trade = tradeService.create(trade,
                tradeAddWithOpRequest.getOperator());
        return BaseResponse.success(TradeAddWithOpResponse.builder().tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
    }

    /**
     * 批量新增交易单
     *
     * @param tradeAddBatchRequest 交易单集合 操作信息 {@link TradeAddBatchRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchResponse}
     */
    @Override
    public BaseResponse<TradeAddBatchResponse> addBatch(@RequestBody @Valid TradeAddBatchRequest tradeAddBatchRequest) {
        List<TradeCommitResult> trades =
                tradeService.createBatch(KsBeanUtil.convert(tradeAddBatchRequest.getTradeDTOList(), PileTrade.class),
                        tradeAddBatchRequest.getOperator());
        return BaseResponse.success(TradeAddBatchResponse.builder().tradeCommitResultVOS(KsBeanUtil.convert(trades,
                TradeCommitResultVO.class)).build());
    }

    @Override
    public BaseResponse<TradeWrapperBackendCommitListResponse> queryDelivery(@RequestBody @Valid TradeQueryDeliveryBatchRequest tradeAddBatchRequest) {
        List<Trade> trades = tradeService.tradeManagerCommit(KsBeanUtil.convert(tradeAddBatchRequest.getTradeDTOList(), Trade.class));
        return BaseResponse.success(TradeWrapperBackendCommitListResponse.builder()
                .tradeVO(KsBeanUtil.convert(trades, TradeVO.class)).build());
    }

    /**
     * 批量分组新增交易单
     *
     * @param tradeAddBatchWithGroupRequest 交易单集合 分组信息 操作信息 {@link TradeAddBatchWithGroupRequest}
     * @return 交易单提交结果集合 {@link TradeAddBatchWithGroupResponse}
     */
    @Override
    public BaseResponse<TradeAddBatchWithGroupResponse> addBatchWithGroup(@RequestBody @Valid TradeAddBatchWithGroupRequest tradeAddBatchWithGroupRequest) {
        List<TradeCommitResult> trades = tradeService.createBatchWithGroup(
                KsBeanUtil.convert(tradeAddBatchWithGroupRequest.getTradeDTOList(), PileTrade.class),
                KsBeanUtil.convert(tradeAddBatchWithGroupRequest.getTradeGroup(), PileTradeGroup.class),
                tradeAddBatchWithGroupRequest.getOperator());
        return BaseResponse.success(TradeAddBatchWithGroupResponse.builder().tradeCommitResultVOS(KsBeanUtil.convert(trades,
                TradeCommitResultVO.class)).build());
    }

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象
     * @return 提交订单结果
     */
    @Override
    public BaseResponse<TradeCommitResponse> commit(@RequestBody @Valid PileTradeCommitRequest tradeCommitRequest) {
        List<TradeCommitResult> results = tradeService.commit(tradeCommitRequest);
        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
    }

    /**
     * 取消订单
     *
     * @param tradeCancelRequest 交易编号 操作信息 {@link TradeCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse cancel(@RequestBody @Valid TradeCancelRequest tradeCancelRequest) {
        tradeService.cancel(tradeCancelRequest.getTid(), tradeCancelRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 订单审核
     *
     * @param tradeAuditRequest 订单审核相关必要信息 {@link TradeAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse audit(@RequestBody @Valid TradeAuditRequest tradeAuditRequest) {
        tradeService.audit(tradeAuditRequest.getTid(), tradeAuditRequest.getAuditState(), tradeAuditRequest.getReason(),
                tradeAuditRequest.getOperator(),tradeAuditRequest.getFinancialFlag());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse newAudit(TradeAuditRequest tradeAuditRequest) {
        tradeService.newAudit(tradeAuditRequest.getTid(), tradeAuditRequest.getAuditState(), tradeAuditRequest.getReason(),
                tradeAuditRequest.getOperator(),tradeAuditRequest.getFinancialFlag());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 订单回审
     *
     * @param tradeRetrialRequest 订单审核相关必要信息 {@link TradeRetrialRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse retrial(@RequestBody @Valid TradeRetrialRequest tradeRetrialRequest) {
        tradeService.retrial(tradeRetrialRequest.getTid(), tradeRetrialRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 订单批量审核
     *
     * @param tradeAuditBatchRequest 订单审核相关必要信息 {@link TradeAuditBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse auditBatch(@RequestBody @Valid TradeAuditBatchRequest tradeAuditBatchRequest) {
        tradeService.batchAudit(tradeAuditBatchRequest.getIds(), tradeAuditBatchRequest.getAuditState(),
                tradeAuditBatchRequest.getReason(),
                tradeAuditBatchRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改卖家备注
     *
     * @param tradeRemedySellerRemarkRequest 订单修改相关必要信息 {@link TradeRemedySellerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse remedySellerRemark(@RequestBody @Valid TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest) {
        tradeService.remedySellerRemark(tradeRemedySellerRemarkRequest.getTid(),
                tradeRemedySellerRemarkRequest.getSellerRemark(),
                tradeRemedySellerRemarkRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @Override
    public BaseResponse<TradeDeliverResponse> deliver(@RequestBody @Valid TradeDeliverRequest tradeDeliverRequest) {
        String deliverId = tradeService.deliver(tradeDeliverRequest.getTid(),
                KsBeanUtil.convert(tradeDeliverRequest.getTradeDeliver(), TradeDeliver.class),
                tradeDeliverRequest.getOperator());
        return BaseResponse.success(TradeDeliverResponse.builder().deliverId(deliverId).build());
    }

    /**
     * 确认收货
     *
     * @param tradeConfirmReceiveRequest 订单编号 操作信息 {@link TradeConfirmReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse confirmReceive(@RequestBody @Valid TradeConfirmReceiveRequest tradeConfirmReceiveRequest) {
        tradeService.confirmReceive(tradeConfirmReceiveRequest.getTid(), tradeConfirmReceiveRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退货 | 退款
     *
     * @param tradeReturnOrderRequest 订单编号 操作信息 {@link TradeReturnOrderRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse returnOrder(@RequestBody @Valid TradeReturnOrderRequest tradeReturnOrderRequest) {
        tradeService.returnOrder(tradeReturnOrderRequest.getTid(), tradeReturnOrderRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 作废订单
     *
     * @param tradeVoidedRequest 订单编号 操作信息 {@link TradeVoidedRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse voided(@RequestBody @Valid TradeVoidedRequest tradeVoidedRequest) {
        tradeService.voidTrade(tradeVoidedRequest.getTid(), tradeVoidedRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单作废后的订单状态扭转
     *
     * @param tradeReverseRequest 订单编号 操作信息 {@link TradeReverseRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse reverse(@RequestBody @Valid TradeReverseRequest tradeReverseRequest) {
        tradeService.reverse(tradeReverseRequest.getTid(), tradeReverseRequest.getOperator(),
                tradeReverseRequest.getReturnType());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发货记录作废
     *
     * @param tradeDeliverRecordObsoleteRequest 订单编号 物流单号 操作信息 {@link TradeDeliverRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse deliverRecordObsolete(@RequestBody @Valid TradeDeliverRecordObsoleteRequest tradeDeliverRecordObsoleteRequest) {
        tradeService.deliverRecordObsolete(tradeDeliverRecordObsoleteRequest.getTid(),
                tradeDeliverRecordObsoleteRequest.getDeliverId(),
                tradeDeliverRecordObsoleteRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存发票信息
     *
     * @param tradeAddInvoiceRequest 订单编号 发票信息 {@link TradeAddInvoiceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse saveInvoice(@RequestBody @Valid TradeAddInvoiceRequest tradeAddInvoiceRequest) {
        tradeService.saveInvoice(tradeAddInvoiceRequest.getTid(),
                KsBeanUtil.convert(tradeAddInvoiceRequest.getInvoiceDTO(), Invoice.class));
        return BaseResponse.SUCCESSFUL();


    }

    /**
     * 支付作废
     *
     * @param tradePayRecordObsoleteRequest 订单编号 操作信息 {@link TradePayRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse payRecordObsolete(@RequestBody @Valid TradePayRecordObsoleteRequest tradePayRecordObsoleteRequest) {
        tradeService.payRecordObsolete(tradePayRecordObsoleteRequest.getTid(),
                tradePayRecordObsoleteRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 线上订单支付回调
     *
     * @param tradePayCallBackOnlineRequest 订单 支付单 操作信息 {@link TradePayCallBackOnlineRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse payCallBackOnline(@RequestBody @Valid TradePayCallBackOnlineRequest tradePayCallBackOnlineRequest) {
        tradeService.payCallBackOnline(
                KsBeanUtil.convert(tradePayCallBackOnlineRequest.getTrade(), PileTrade.class),
                KsBeanUtil.convert(tradePayCallBackOnlineRequest.getPayOrderOld(), PayOrder.class),
                tradePayCallBackOnlineRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse payCallBackOnlineBatch(@RequestBody @Valid TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest) {
        List<PayCallBackOnlineBatchPile> request = tradePayCallBackOnlineBatchRequest.getRequestList().stream().map(i -> {
            PayCallBackOnlineBatchPile data = new PayCallBackOnlineBatchPile();
            data.setPayOrderOld(KsBeanUtil.convert(i.getPayOrderOld(), PayOrder.class));
            data.setTrade(KsBeanUtil.convert(i.getTrade(), PileTrade.class));
            return data;
        }).collect(Collectors.toList());
        tradeService.payCallBackOnlineBatch(request, tradePayCallBackOnlineBatchRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 订单支付回调
     *
     * @param tradePayCallBackRequest 订单号 支付金额 操作信息 支付方式{@link TradePayCallBackRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse payCallBack(@RequestBody @Valid TradePayCallBackRequest tradePayCallBackRequest) {
        tradeService.payCallBack(
                tradePayCallBackRequest.getTid(),
                tradePayCallBackRequest.getPayOrderPrice(),
                tradePayCallBackRequest.getOperator(),
                tradePayCallBackRequest.getPayWay());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 0 元订单默认支付
     *
     * @param tradeDefaultPayRequest 订单号{@link TradeDefaultPayRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @Override
    public BaseResponse<TradeDefaultPayResponse> defaultPay(@RequestBody @Valid TradeDefaultPayRequest tradeDefaultPayRequest) {
        PileTrade trade = tradeService.detail(tradeDefaultPayRequest.getTid());
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException("K-050100", new Object[]{tradeDefaultPayRequest.getTid()});
        }
        return BaseResponse.success(TradeDefaultPayResponse.builder()
                .payResult(tradeService.tradeDefaultPay(trade, tradeDefaultPayRequest.getPayWay())).build());
    }

    @Override
    public BaseResponse defaultPayBatch(@RequestBody @Valid TradeDefaultPayBatchRequest tradeDefaultPayBatchRequest) {
        List<PileTrade> trades = tradeService.details(tradeDefaultPayBatchRequest.getTradeIds());
        //请求包含不存在的订单
        if (trades.size() != tradeDefaultPayBatchRequest.getTradeIds().size()) {
            throw new SbcRuntimeException("K-050100");
        }
        tradeService.tradeDefaultPayBatch(trades, tradeDefaultPayBatchRequest.getPayWay());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param tradeAddReceivableRequest 收款单平台信息{@link TradeAddReceivableRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @Override
    public BaseResponse addReceivable(@RequestBody @Valid TradeAddReceivableRequest tradeAddReceivableRequest) {
        tradeService.addReceivable(KsBeanUtil.convert(tradeAddReceivableRequest.getReceivableAddDTO(),
                ReceivableAddRequest.class), tradeAddReceivableRequest.getPlatform(),
                tradeAddReceivableRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 确认支付单
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return 支付单集合 {@link TradeConfirmPayOrderResponse}
     */
    @Override
    public BaseResponse confirmPayOrder(@RequestBody @Valid TradeConfirmPayOrderRequest tradeConfirmPayOrderRequest) {
        tradeService.confirmPayOrder(tradeConfirmPayOrderRequest.getPayOrderIds(),
                tradeConfirmPayOrderRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 线下确认支付单
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return
     */
    @Override
    public BaseResponse confirmPayOrderOffline(@RequestBody @Valid TradeConfirmPayOrderOfflineRequest tradeConfirmPayOrderRequest){
        tradeService.confirmPayOrderOffline(tradeConfirmPayOrderRequest.getPayOrderIds(),
                                     tradeConfirmPayOrderRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新订单的结算状态
     *
     * @param tradeUpdateSettlementStatusRequest 店铺id 起始时间 {@link TradeUpdateSettlementStatusRequest}
     * @return 支付单集合 {@link TradeCountByPayStateResponse}
     */
    @Override
    public BaseResponse updateSettlementStatus(@RequestBody @Valid TradeUpdateSettlementStatusRequest tradeUpdateSettlementStatusRequest) {
        tradeService.updateSettlementStatus(tradeUpdateSettlementStatusRequest.getStoreId(),
                tradeUpdateSettlementStatusRequest.getStartTime()
                , tradeUpdateSettlementStatusRequest.getEndTime());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 添加订单
     *
     * @param tradeAddRequest 订单信息 {@link TradeAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse add(@RequestBody @Valid TradeAddRequest tradeAddRequest) {
        tradeService.addTrade(KsBeanUtil.convert(tradeAddRequest.getTrade(), PileTrade.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新订单的业务员
     *
     * @param tradeUpdateEmployeeIdRequest 业务员信息 {@link TradeUpdateEmployeeIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse updateEmployeeId(@RequestBody @Valid TradeUpdateEmployeeIdRequest tradeUpdateEmployeeIdRequest) {
        tradeService.updateEmployeeId(tradeUpdateEmployeeIdRequest.getEmployeeId(),
                tradeUpdateEmployeeIdRequest.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新返利标志
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateCommissionFlag(@RequestBody @Valid TradeUpdateCommissionFlagRequest request) {
        tradeService.updateCommissionFlag(request.getTradeId(), request.getCommissionFlag());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新最终时间
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateFinalTime(@RequestBody @Valid TradeFinalTimeUpdateRequest request) {
        tradeService.updateFinalTime(request.getTid(), request.getOrderReturnTime());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新正在进行的退单数量
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateReturnOrderNum(@RequestBody @Valid TradeReturnOrderNumUpdateRequest request) {
        tradeService.updateReturnOrderNum(request.getTid(), request.isAddFlag());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 完善没有业务员的订单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse fillEmployeeId() {
        tradeService.fillEmployeeId();
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 更新订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse update(@RequestBody @Valid TradeUpdateRequest tradeUpdateRequest) {
        tradeService.updateTradeInfo(tradeUpdateRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 功能描述: 手动推送第三方订单(WMS)
     *
     * @param tradeGetByIdRequest 订单信息 {@link TradeGetByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse pushConfirmOrder(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest) {
        PileTrade trade = tradeService.detail(tradeGetByIdRequest.getTid());
//        tradeService.pushConfirmOrder(trade);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse sendPickUpMessage(@RequestBody @Valid SendPickUpMessageRequest request) {
        PileTrade trade = KsBeanUtil.convert(request.getTrade(), PileTrade.class);
        tradeService.sendPickUpMessage(trade);
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse updateTradeLogisticsCompany(@RequestBody @Valid updateTradeLogisticCompanyRequest request) {
        tradeService.updateTradeLogisticsCompany(request.getTid(),request.getAreaInfo(),request.getCompanyId(),request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse checkWms(@Valid TradeCheckWmsRequest tradeCheckWmsRequest) {
        TradeVO tradeVO = tradeCheckWmsRequest.getTradeVO();
        return tradeService.checkWms(KsBeanUtil.convert(tradeVO,Trade.class));
    }

    @Override
    public BaseResponse payOnlineCallBack(@Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.WECAHT){
                tradeService.wxPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.ALI){
                tradeService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse payPileOrderCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.WECAHT){
                tradeService.wxPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.ALI){
                tradeService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 充值回调
     * @param tradePayOnlineCallBackRequest
     * @return
     */
    @Override
    public BaseResponse payOnlineRechargeCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            //微信
            if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.WECAHT){
                tradeService.wxPayRechargeOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if(tradePayOnlineCallBackRequest.getPayCallBackType()== PayCallBackType.ALI){
                //支付宝
                tradeService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushOrderToWms(@RequestBody @Valid TradePushRequest tradePushRequest) {
        tradeService.pushWMSOrder(tradeService.detail(tradePushRequest.getTid()),true);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse addMergePay(@RequestBody @Valid TradeAddMergePayRequest request){
        tradeService.addMergePay(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse syncPileOrderPrice() {
        tradeService.syncPileOrderPrice();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse writingHistoricalData(){
        pileStoringHistoricalDataService.storingHistoricalData();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse walletPay(WalletPayRequest request) {
        tradeService.walletPay(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Long> countPilePurchaseAction(PilePurchaseActionRequestVO request) {
        return BaseResponse.success(pilePurchaseActionService.countPilePurchaseAction(KsBeanUtil.convert(request,PilePurchaseActionRequest.class)));
    }

    @Override
    public BaseResponse<PilePurchaseActionResponse> pilePurchaseActionPage(PilePurchaseActionRequestVO request) {
        Page<PilePurchaseAction> purchaseActionPage = pilePurchaseActionService.page(KsBeanUtil.convert(request,PilePurchaseActionRequest.class));
        PilePurchaseActionResponse response = new PilePurchaseActionResponse();
        response.setPilePurchaseActionVOPage(KsBeanUtil.convertPage(purchaseActionPage, PilePurchaseActionVO.class));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse batchSavePilePurchaseAction(PilePurchaseActionRequestVO request) {
        PilePurchaseActionRequest pilePurchaseActionRequest = new PilePurchaseActionRequest();
        pilePurchaseActionRequest.setPurchaseActionList(KsBeanUtil.convertList(request.getPurchaseActionVOList(),PilePurchaseAction.class));
        pilePurchaseActionService.batchSavePilePurchaseAction(pilePurchaseActionRequest);
        return BaseResponse.SUCCESSFUL();
    }

}
