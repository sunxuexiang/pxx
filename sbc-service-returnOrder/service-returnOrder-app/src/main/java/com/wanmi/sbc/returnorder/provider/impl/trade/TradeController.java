package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeProvider;
import com.wanmi.sbc.returnorder.api.request.orderpicking.OrderPickingRequest;
import com.wanmi.sbc.returnorder.api.response.orderpicking.OrderPickingListResponse;
import com.wanmi.sbc.returnorder.api.response.orderpicking.OrderPickingResponse;
import com.wanmi.sbc.returnorder.bean.dto.MarketTradeInfoDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeDeliverUpdateDTO;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.PayCallBackType;
import com.wanmi.sbc.returnorder.bean.vo.PointsTradeCommitResultVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeCommitResultVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeDeliverVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.orderpicking.model.root.OrderPicking;
import com.wanmi.sbc.returnorder.orderpicking.service.OrderPickingService;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.receivables.request.ReceivableAddRequest;
import com.wanmi.sbc.returnorder.returnorder.service.PushKingdeeService;
import com.wanmi.sbc.returnorder.stockupaction.service.StockupActionService;
import com.wanmi.sbc.returnorder.trade.model.entity.PayCallBackOnlineBatch;
import com.wanmi.sbc.returnorder.trade.model.entity.PointsTradeCommitResult;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeCommitResult;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Invoice;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Logistics;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.model.root.TradeGroup;
import com.wanmi.sbc.returnorder.trade.request.TradePriceChangeRequest;
import com.wanmi.sbc.returnorder.trade.request.TradeRemedyRequest;
import com.wanmi.sbc.returnorder.trade.service.KingdeeAbnormalSalesOrderService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 10:04
 */
@Validated
@Slf4j
@RestController
public class TradeController implements TradeProvider {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private KingdeeAbnormalSalesOrderService kingdeeAbnormalSalesOrderService;

    @Autowired
    private StockupActionService stockupActionService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderPickingService orderPickingService;

    @Autowired
    PushKingdeeService pushKingdeeService;

    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    /**
     * 新增交易单
     *
     * @param tradeAddWithOpRequest 交易单 操作信息 {@link TradeAddWithOpRequest}
     * @return 交易单 {@link TradeAddWithOpResponse}
     */
    @Override

    public BaseResponse<TradeAddWithOpResponse> addWithOp(@RequestBody @Valid TradeAddWithOpRequest tradeAddWithOpRequest) {
        Trade trade = KsBeanUtil.convert(tradeAddWithOpRequest.getTrade(), Trade.class);
        trade.setParentId(generatorService.generatePoId());
        trade = tradeService.create(trade,
                tradeAddWithOpRequest.getOperator(), false, true);

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
                tradeService.createBatch(KsBeanUtil.convert(tradeAddBatchRequest.getTradeDTOList(), Trade.class),
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
                KsBeanUtil.convert(tradeAddBatchWithGroupRequest.getTradeDTOList(), Trade.class),
                KsBeanUtil.convert(tradeAddBatchWithGroupRequest.getTradeGroup(), TradeGroup.class),
                tradeAddBatchWithGroupRequest.getOperator());
        return BaseResponse.success(TradeAddBatchWithGroupResponse.builder().tradeCommitResultVOS(KsBeanUtil.convert(trades,
                TradeCommitResultVO.class)).build());
    }

    /**
     * 订单改价
     *
     * @param tradeModifyPriceRequest 价格信息 交易单号 操作信息 {@link TradeModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse modifyPrice(@RequestBody @Valid TradeModifyPriceRequest tradeModifyPriceRequest) {
        tradeService.changePrice(KsBeanUtil.convert(tradeModifyPriceRequest.getTradePriceChangeDTO(),
                        TradePriceChangeRequest.class),
                tradeModifyPriceRequest.getTid(),
                tradeModifyPriceRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 订单明细价格修改
     *
     * @param tradeModifyPriceRequest 价格信息 交易单号 操作信息 {@link TradeModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse changeItemPrice(TradeModifyPriceRequest tradeModifyPriceRequest) {
        BaseResponse response = tradeService.changeItemPrice(tradeModifyPriceRequest);
        return response;
    }

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象
     * @return 提交订单结果
     */
    @Override
    public BaseResponse<TradeCommitResponse> commit(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        List<TradeCommitResult> results = tradeService.commit(tradeCommitRequest);
        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
    }

    /**
     * C端提交订单
     *
     * @param tradeCommitRequest 提交订单请求对象
     * @return 提交订单结果
     */
    @Override
    public BaseResponse<TradeCommitResponse> devanningCommit(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        List<TradeCommitResult> results = tradeService.devanningCommit(tradeCommitRequest);
        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
    }


    @Override
    public BaseResponse<TradeCommitResponse> commitAll(TradeCommitRequest tradeCommitRequest) {
        List<TradeCommitResult> results = tradeService.commitAll(tradeCommitRequest);
        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
    }

    @Override
    public BaseResponse<TradeCommitResponse> devanningcommitAll(@Valid TradeCommitRequest tradeCommitRequest) {

//        List<TradeCommitResult> results = tradeService.devanningcommitAll(tradeCommitRequest);
//        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));

        RLock rLock = redissonClient.getFairLock(Constants.OVER_ORDER_BOOKING);
        rLock.lock();
        try {
            List<TradeCommitResult> results = tradeService.devanningcommitAll(tradeCommitRequest);
            return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }


    }

    @Override
    public BaseResponse<TradeCommitResponse> takeGoods(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        List<TradeCommitResult> results = tradeService.takeGood(tradeCommitRequest);
        return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
    }

    /**
     * 移动端提交积分商品订单
     *
     * @param pointsTradeCommitRequest 提交订单请求对象  {@link PointsTradeCommitRequest}
     * @return
     */
    @Override
    public BaseResponse<PointsTradeCommitResponse> pointsCommit(@RequestBody @Valid PointsTradeCommitRequest pointsTradeCommitRequest) {
        PointsTradeCommitResult result = tradeService.pointsCommit(pointsTradeCommitRequest);
        return BaseResponse.success(new PointsTradeCommitResponse(KsBeanUtil.convert(result,
                PointsTradeCommitResultVO.class)));
    }

    /**
     * 移动端提交积分优惠券订单
     *
     * @param pointsTradeCommitRequest 提交订单请求对象  {@link PointsTradeCommitRequest}
     * @return
     */
    @Override
    public BaseResponse<PointsTradeCommitResponse> pointsCouponCommit(@RequestBody @Valid PointsCouponTradeCommitRequest pointsTradeCommitRequest) {
        PointsTradeCommitResult result = tradeService.pointsCouponCommit(pointsTradeCommitRequest);
        return BaseResponse.success(new PointsTradeCommitResponse(KsBeanUtil.convert(result,
                PointsTradeCommitResultVO.class)));
    }

    /**
     * 修改订单
     *
     * @param tradeModifyRemedyRequest 店铺信息 交易单信息 操作信息 {@link TradeModifyRemedyRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse remedy(@RequestBody @Valid TradeModifyRemedyRequest tradeModifyRemedyRequest) {
        tradeService.remedy(KsBeanUtil.convert(tradeModifyRemedyRequest.getTradeRemedyDTO(), TradeRemedyRequest.class),
                tradeModifyRemedyRequest.getOperator(),
                KsBeanUtil.convert(tradeModifyRemedyRequest.getStoreInfoDTO(), StoreInfoResponse.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改订单-部分修改
     *
     * @param tradeRemedyPartRequest 店铺信息 交易单信息 操作信息 {@link TradeRemedyPartRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse remedyPart(@RequestBody @Valid TradeRemedyPartRequest tradeRemedyPartRequest) {
        tradeService.remedyPart(KsBeanUtil.convert(tradeRemedyPartRequest.getTradeRemedyDTO(),
                        TradeRemedyRequest.class),
                tradeRemedyPartRequest.getOperator(),
                KsBeanUtil.convert(tradeRemedyPartRequest.getStoreInfoDTO(), StoreInfoResponse.class));
        return BaseResponse.SUCCESSFUL();
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
        if (Objects.nonNull(tradeAuditRequest.getTid())) {
            tradeService.audit(tradeAuditRequest.getTid(), tradeAuditRequest.getAuditState(), tradeAuditRequest.getReason(),
                    tradeAuditRequest.getOperator());
        } else if (CollectionUtils.isNotEmpty(tradeAuditRequest.getTids())) {
            tradeAuditRequest.getTids().forEach(tid -> {
                tradeService.audit(tid, tradeAuditRequest.getAuditState(), tradeAuditRequest.getReason(),
                        tradeAuditRequest.getOperator());
            });
        }

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
     * @desc  修改运单号
     * @author shiy  2023/6/19 20:19
    */
    @Override
    public BaseResponse<TradeDeliverResponse> updateLogistics(@RequestBody @Valid TradeDeliverUpdateDTO tradeDeliverUpdateDTO) {
        TradeDeliverVO deliverVO = tradeDeliverUpdateDTO.getTradeDeliverVO();
        Trade trade = tradeService.detail(deliverVO.getTradeId());
        int i = 0;
        if (CollectionUtils.isNotEmpty(trade.getTradeDelivers())) {
            for (TradeDeliver tradeDeliver : trade.getTradeDelivers()) {
                if (tradeDeliverUpdateDTO.getOldData().getDeliverNo().equals(tradeDeliver.getLogistics().getLogisticNo())) {
                    Logistics logistics = tradeDeliver.getLogistics();
                    logistics.setLogisticNo(tradeDeliverUpdateDTO.getNewData().getDeliverNo());
                    logistics.setLogisticCompanyId(deliverVO.getLogistics().getLogisticCompanyId());
                    logistics.setLogisticCompanyName(deliverVO.getLogistics().getLogisticCompanyName());
                    logistics.setLogisticStandardCode(deliverVO.getLogistics().getLogisticStandardCode());
                    logistics.setLogisticPhone(deliverVO.getLogistics().getLogisticPhone());
                    logistics.setEncloses(deliverVO.getLogistics().getEncloses());
                    trade.getTradeState().setDeliverTime(deliverVO.getDeliverTime());
                }
            }
        }
        tradeService.updateTrade(trade);
        return BaseResponse.success(TradeDeliverResponse.builder().deliverId(deliverVO.getDeliverId()).build());
    }

    @Override
    public BaseResponse<TradeDeliverResponse> deliver2(TradeDeliverRequest tradeDeliverRequest) {
        String deliverId = tradeService.deliver2(tradeDeliverRequest.getTid(),
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
                KsBeanUtil.convert(tradePayCallBackOnlineRequest.getTrade(), Trade.class),
                KsBeanUtil.convert(tradePayCallBackOnlineRequest.getPayOrderOld(), PayOrder.class),
                tradePayCallBackOnlineRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse payCallBackOnlineBatch(@RequestBody @Valid TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest) {
        List<PayCallBackOnlineBatch> request = tradePayCallBackOnlineBatchRequest.getRequestList().stream().map(i -> {
            PayCallBackOnlineBatch data = new PayCallBackOnlineBatch();
            data.setPayOrderOld(KsBeanUtil.convert(i.getPayOrderOld(), PayOrder.class));
            data.setTrade(KsBeanUtil.convert(i.getTrade(), Trade.class));
            return data;
        }).collect(Collectors.toList());
        tradeService.payCallBackOnlineBatch(request, tradePayCallBackOnlineBatchRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse payTakeGoodCallBackOnlineBatch(TradePayCallBackOnlineBatchRequest tradePayCallBackOnlineBatchRequest) {
        List<PayCallBackOnlineBatch> request = tradePayCallBackOnlineBatchRequest.getRequestList().stream().map(i -> {
            PayCallBackOnlineBatch data = new PayCallBackOnlineBatch();
            data.setPayOrderOld(KsBeanUtil.convert(i.getPayOrderOld(), PayOrder.class));
            data.setTrade(KsBeanUtil.convert(i.getTrade(), Trade.class));
            return data;
        }).collect(Collectors.toList());
        tradeService.payTakeGoodCallBackOnlineBatch(request, tradePayCallBackOnlineBatchRequest.getOperator());
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
        Trade trade = tradeService.detail(tradeDefaultPayRequest.getTid());
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException("K-050100", new Object[]{tradeDefaultPayRequest.getTid()});
        }
        return BaseResponse.success(TradeDefaultPayResponse.builder()
                .payResult(tradeService.tradeDefaultPay(trade, tradeDefaultPayRequest.getPayWay())).build());
    }

    /**
     * 提货 0元订单默认支付
     *
     * @param tradeDefaultPayRequest 订单号{@link TradeDefaultPayRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @Override
    public BaseResponse<TradeDefaultPayResponse> pileDefaultPay(@RequestBody @Valid TradeDefaultPayRequest tradeDefaultPayRequest) {
        Trade trade = tradeService.detail(tradeDefaultPayRequest.getTid());
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException("K-050100", new Object[]{tradeDefaultPayRequest.getTid()});
        }
        return BaseResponse.success(TradeDefaultPayResponse.builder()
                .payResult(tradeService.pileTradeDefaultPay(trade, tradeDefaultPayRequest.getPayWay())).build());
    }

    @Override
    public BaseResponse defaultPayBatch(@RequestBody @Valid TradeDefaultPayBatchRequest tradeDefaultPayBatchRequest) {
        List<Trade> trades = tradeService.details(tradeDefaultPayBatchRequest.getTradeIds());
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
     *
     * @param tradeConfirmPayOrderRequest 支付单id集合{@link TradeConfirmPayOrderRequest}
     * @return
     */
    @Override
    public BaseResponse confirmPayOrderOffline(@RequestBody @Valid TradeConfirmPayOrderOfflineRequest tradeConfirmPayOrderRequest) {
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
        tradeService.addTrade(KsBeanUtil.convert(tradeAddRequest.getTrade(), Trade.class));
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
        Trade trade = tradeService.detail(tradeGetByIdRequest.getTid());
        if (tradeService.pushConfirmOrder(trade)) {
            return BaseResponse.SUCCESSFUL();
        } else {
            return BaseResponse.FAILED();
        }
    }

    @Override
    public BaseResponse sendPickUpMessage(@RequestBody @Valid SendPickUpMessageRequest request) {
        Trade trade = KsBeanUtil.convert(request.getTrade(), Trade.class);
        tradeService.sendPickUpMessage(trade);
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse updateTradeLogisticsCompany(@RequestBody @Valid updateTradeLogisticCompanyRequest request) {
        tradeService.updateTradeLogisticsCompany(request.getTid(), request.getAreaInfo(), request.getCompanyId(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse checkWms(@Valid TradeCheckWmsRequest tradeCheckWmsRequest) {
        log.info("线程号" + Thread.currentThread().getId());
        TradeVO tradeVO = tradeCheckWmsRequest.getTradeVO();
        return tradeService.checkWms(KsBeanUtil.convert(tradeVO, Trade.class));
    }

    @Override
    public BaseResponse payOnlineCallBack(@Valid TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.WECAHT) {
                tradeService.wxPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.ALI) {
                tradeService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.CMB) {
                tradeService.cmbPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.CUPSWECHAT || tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.CUPSALI) {
                tradeService.cupsPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.CCB ) {
                tradeService.ccbPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 充值回调
     *
     * @param tradePayOnlineCallBackRequest
     * @return
     */
    @Override
    public BaseResponse payOnlineRechargeCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            //微信
            if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.WECAHT) {
                tradeService.wxPayRechargeOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.ALI) {
                //支付宝
                tradeService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 囤货的提货
     *
     * @param tradePayOnlineCallBackRequest
     * @return
     */
    @Override
    public BaseResponse payOnlineTakeGoodCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            //微信
            if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.WECAHT) {
                tradeService.wxPayTakeGoodOnlineCallBack(tradePayOnlineCallBackRequest);
            } else if (tradePayOnlineCallBackRequest.getPayCallBackType() == PayCallBackType.ALI) {
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
        tradeService.pushWMSOrder(tradePushRequest.getTid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushOrderToTms(TradePushRequest tradePushRequest) {
        log.info("开始pushOrderToTms[{}]", JSONObject.toJSONString(tradePushRequest));
        tradeService.pushOrderToTms(tradePushRequest.getTid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse cancelOrderToTms(TradePushRequest tradePushRequest) {
       Trade trade = tradeService.detail(tradePushRequest.getTid());
       tradeService.updateStatusForTradeCancel(trade);
       return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushOrderToWms2(@RequestBody @Valid TradePushRequest tradePushRequest) {
        tradeService.pushWMSOrder2(tradePushRequest.getTid());
        return BaseResponse.SUCCESSFUL();
    }

//    @Override
//    public BaseResponse walletPay(WalletPayRequest request) {
//        tradeService.walletPay(request);
//        return BaseResponse.SUCCESSFUL();
//    }

    @Override
    public BaseResponse pushKingdeeCompensationOrder(@RequestBody @Valid TradePushKingdeeOrderRequest request) {
        kingdeeAbnormalSalesOrderService.compensationKingdeeAbnormalSalesOrder(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse addMergePay(@RequestBody @Valid TradeAddMergePayRequest request) {
        tradeService.addMergePay(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushCachePushKingdeeOrder() {
        kingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse compensateFailedSalesOrders() {
        kingdeeAbnormalSalesOrderService.compensateFailedSalesOrders();
        kingdeeAbnormalSalesOrderService.compensateStockpilingOrders();
        kingdeeAbnormalSalesOrderService.compensateStockpilingReturnGoodsOrders();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteStockupAction(StockupActionDeleteRequest request) {
        stockupActionService.delStockupAction(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchAddStockupAction(BatchAddStockupActionRequest request) {
        stockupActionService.batchAddStockupAction(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse newConfirmPayOrderOffline(TradeConfirmPayOrderOfflineRequest request) {
        tradeService.newConfirmPayOrderOffline(request.getPayOrderIds(),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse ticketsFormPushPayOrderKingdee(TicketsFormPushPayOrderKingdeeRequest ticketsFormPushPayOrderKingdee) {
        tradeService.ticketsFormPushPayOrderKingdee(ticketsFormPushPayOrderKingdee);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse balanceFormPushPayOrderKingdee(AddWalletRecordRequest addWalletRecordRequest) {
        tradeService.balanceFormPushPayOrderKingdee(addWalletRecordRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse orderPicking(OrderPickingRequest request) {
        orderPickingService.sava(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<OrderPickingListResponse> queryOrderPicking(OrderPickingRequest request) {
        List<OrderPicking> list = orderPickingService.getByTidList(request.getTidList());
        return BaseResponse.success(OrderPickingListResponse.builder().orderPickingResponseList(KsBeanUtil.convert(list, OrderPickingResponse.class)).build());
    }

    @Override
    public BaseResponse<OrderPickingResponse> getOrderPickingByTid(OrderPickingRequest request) {
        OrderPicking orderPicking = orderPickingService.getByTid(request.getTid());

        return BaseResponse.success(KsBeanUtil.convert(orderPicking, OrderPickingResponse.class));
    }

    @Override
    public BaseResponse pushRefundOrderKingdeeForCoin(CoinActivityPushKingdeeRequest request) {
        if (kingdeeOpenState) {
            pushKingdeeService.pushRefundOrderKingdeeForCoin(request);

        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushOrderKingdeeForCoin(CoinActivityPushKingdeeRequest request) {
        if (kingdeeOpenState) {
            pushKingdeeService.pushOrderKingdeeForCoin(request);
        }
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * @desc  商城提交订单
     * @author shiy  2023/7/4 10:54
     */
    @Override
    public BaseResponse<TradeCommitResponse> submitMallTrades(TradeCommitRequest tradeCommitRequest) {
        RLock rLock = redissonClient.getFairLock(Constants.OVER_ORDER_BOOKING+tradeCommitRequest.getOperator().getUserId());
        rLock.lock();
        try {
            List<TradeCommitResult> results = tradeService.submitMallTrades(tradeCommitRequest);
            return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public BaseResponse<TradeDeliveryWayResponse> getTradeDiliveryAreaVo(TradeDeliveryWayRequest request) {
        TradeDeliveryWayResponse responseList = tradeService.calcTradeDeliveryWay(request);
        return BaseResponse.success(responseList);
    }

    @Override
    public BaseResponse tmsDeliver(TmsDeliverRequest tmsDeliverRequest) {
        if(tmsDeliverRequest ==null || tmsDeliverRequest.getTid()==null || tmsDeliverRequest.getLogistics()==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Trade trade = tradeService.detail(tmsDeliverRequest.getTid());
        if(trade==null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单错误");
        }
        if(!DeliverStatus.isShipped(trade.getTradeState().getDeliverStatus())||CollectionUtils.isEmpty(trade.getTradeDelivers())){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"当前订单状态未发货，不支持本次操作");
        }
        trade.getTradeDelivers().get(0).getLogistics().setLogisticNo(tmsDeliverRequest.getLogistics().getLogisticNo());
        String detail = String.format("订单[%s]物流商[%s]已发货。操作人:%s", trade.getId(), tmsDeliverRequest.getOperatorName(),tmsDeliverRequest.getOperator().getName());
        trade.appendTradeEventLog(new TradeEventLog(tmsDeliverRequest.getOperator(), "物流发货", detail, LocalDateTime.now()));
        tradeService.updateTrade(trade);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse initHistoryTradeInfo(Integer flag) {
        tradeService.initHistoryTradeInfo(flag);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse checkSubmitTradeDeliveryToStore(MarketTradeInfoDTO marketTradeInfoDTO) {
        tradeService.checkSubmitTradeDeliveryToStore(marketTradeInfoDTO);
        return BaseResponse.SUCCESSFUL();
    }
}
