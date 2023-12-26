package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchPlusStockRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.job.service.DistributionTaskTempService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.distribution.ReturnOrderSendMQRequest;
import com.wanmi.sbc.order.api.request.refund.RefundForNewPickNotAudit;
import com.wanmi.sbc.order.api.request.refund.RefundOrderNotAuditProducerRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradePushRequest;
import com.wanmi.sbc.order.api.request.trade.TradeReturnOrderNumUpdateRequest;
import com.wanmi.sbc.order.api.request.trade.TradeVoidedRequest;
import com.wanmi.sbc.order.api.response.returnorder.NewPileReturnOrderByIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 退单处理消费者
 * @Autho qiaokang
 * @Date：2019-03-08 16:14:12
 */
@Service
@Slf4j
@EnableBinding(BossSink.class)
public class ReturnOrderConsumerService {

    /**
     * 注入分销临时任务service
     */
    @Autowired
    private DistributionTaskTempService distributionTaskTempService;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private NewPileReturnOrderProvider newPileReturnOrderProvider;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    CouponCodeProvider couponCodeProvider;

    @Autowired
    InventoryDetailSamountProvider inventoryDetailSamountProvider;

    /**
     * 退单状态变更MQ处理：分销任务临时表退单数量加减
     *
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_RETURN_ORDER_FLOW)
    public void doReturnOrderInit(String json) {
        try {
            log.info("=============== 退单状态变更MQ处理start ===============");
            ReturnOrderSendMQRequest request = JSONObject.parseObject(json, ReturnOrderSendMQRequest.class);

            if (request.isAddFlag()) {
                // 分销任务临时表退单数量加1
                distributionTaskTempService.addReturnOrderNum(request.getOrderId());
            } else {
                // 分销任务临时表退单数量减1
                distributionTaskTempService.minusReturnOrderNum(request.getOrderId());
            }

            // 更新订单正在进行的退单数量
            tradeProvider.updateReturnOrderNum(
                    TradeReturnOrderNumUpdateRequest.builder().tid(request.getOrderId()).addFlag(request.isAddFlag()).build());

            log.info("=============== 退单状态变更MQ处理end ===============");
        } catch (Exception e) {
            log.error("退单状态变更MQ处理异常! param={}", json, e);
        }
    }

    /**
     * 囤货退单状态变更MQ处理：分销任务临时表退单数量加减
     *
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_RETURN_PILE_ORDER_FLOW)
    public void doReturnPileOrderInit(String json) {
        try {
            log.info("=============== 退单状态变更MQ处理start ===============");
            ReturnOrderSendMQRequest request = JSONObject.parseObject(json, ReturnOrderSendMQRequest.class);

            // 更新订单正在进行的退单数量
            pileTradeProvider.updateReturnOrderNum(
                    TradeReturnOrderNumUpdateRequest.builder().tid(request.getOrderId()).addFlag(request.isAddFlag()).build());

            log.info("=============== 退单状态变更MQ处理end ===============");
        } catch (Exception e) {
            log.error("退单状态变更MQ处理异常! param={}", json, e);
        }
    }

    /**
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_REFUND_ORDER_NOT_AUDIT_CONSUMER)
    public void refundOrderNotAuditProducer(String json) {
        try {
            log.info("=============== Q_REFUND_ORDER_NOT_AUDIT_PRODUCER退单无审核直接退款start ===============");
            RefundOrderNotAuditProducerRequest message = JSONObject.parseObject(json, RefundOrderNotAuditProducerRequest.class);
            // log.info("*------------------------------->" + json);
            // log.info("*------------------------------->" + JSONObject.toJSONString(message));
            if (message.getNewPickOrderFlag()) {
                processReturnOrderFromNewPile(message);
                return;
            }

            //审核
            ReturnOrderOnlineModifyPriceRequest request = new ReturnOrderOnlineModifyPriceRequest();

            BigDecimal refundPrice = returnOrderQueryProvider.queryRefundPrice(ReturnOrderQueryRefundPriceRequest.builder()
                    .rid(message.getRId()).build()).getContext().getRefundPrice();
            request.setActualReturnPrice(refundPrice);

            ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(message.getRId())
                    .build()).getContext();

            BaseResponse baseResponse = returnOrderProvider.onlineModifyPrice(ReturnOrderOnlineModifyPriceRequest.builder()
                    .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                    .refundComment(request.getRefundComment())
                    .actualReturnPrice(request.getActualReturnPrice())
                    .actualReturnPoints(request.getActualReturnPoints())
                    .operator(message.getOperator()).build());

            if (baseResponse.getCode().equals("K-000000")) {
                //打款
                Operator operator = message.getOperator();
                BaseResponse<Object> res = returnOrderProvider.refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(message.getRId())
                        .operator(operator).build());
                tradeProvider.cancelOrderToTms(TradePushRequest.builder().tid(message.getTId()).build());
                Object data = res.getContext();
                if (Objects.isNull(data)) {
                    //无返回信息，追踪退单退款状态
                    ReturnFlowState state = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(message.getRId()).build()).getContext().getReturnFlowState();
                    if (state.equals(ReturnFlowState.REFUND_FAILED)) {
                        log.info("打款失败-------->" + json);
                    }
                }
            }
            log.info("=============== Q_REFUND_ORDER_NOT_AUDIT_PRODUCER退单无审核直接退款end ===============");
        } catch (Exception e) {
            log.error("退单无审核直接退款! param={}", json, e);
        }
    }


    @Autowired
    PayOrderQueryProvider payOrderQueryProvider;
    @Autowired
    RefundOrderProvider refundOrderProvider;

    @LcnTransaction
    public void processReturnOrderFromNewPile(RefundOrderNotAuditProducerRequest refundRequest) {
        //TODO: 手动ack, 处理失败时，需再次消费
        if (Objects.isNull(refundRequest) || Objects.isNull(refundRequest.getRId())) {
            log.info("=============== refundRequest is null or rId is null ===============");
            return;
        }
        log.info("=============== 处理来源于新囤货订单的提货退款 start =============== 单号={}", refundRequest.getRId());

        //查找退单
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(refundRequest.getRId())
                .build()).getContext();

        //1 根据提货单找到关联的囤货单及商品
        TradeGetByIdRequest tradeGetByIdRequest = TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build();
        BaseResponse<TradeGetByIdResponse> tradeResp = tradeQueryProvider.getById(tradeGetByIdRequest);
        TradeVO tradeVO = tradeResp.getContext().getTradeVO();

        if (!checkReturnOrder(returnOrder, tradeVO)) {
            return;
        }
        //申请分布式锁，以免重复处理 TODO: DEFINE CONST IN COMMON JAR
        RLock rLock = redissonClient.getFairLock("refund_new_pile_pick_return_order_" + returnOrder.getBuyer().getId());
        rLock.lock();
        try {
            returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(refundRequest.getRId())
                    .build()).getContext();

            if (!checkReturnOrder(returnOrder, tradeVO)) {
                return;
            }
            //推送取消订单
//            tradeProvider.checkWms(TradeCheckWmsRequest.builder().tradeVO(tradeVO).build());
//            returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
            //更新退单状态
            updateReturnOrderFlowState(returnOrder);

            //更新订单状态
            Operator operator = refundRequest.getOperator();
            if (Objects.isNull(operator) || StringUtils.isBlank(operator.getUserId())) {
                operator = Operator.builder().ip("127.0.0.1").adminId("1").name("system").platform(Platform.BOSS).build();
                refundRequest.setOperator(operator);
            }
            tradeProvider.voided(TradeVoidedRequest.builder().tid(returnOrder.getTid()).operator(refundRequest.getOperator()).build());

            //DONE: 退款开关，囤货提货退款-无需审核
            //回退余额：增加明细(退单金额+运费)，增加账户余额
            //未发货时，整单退，退运费
            RefundForNewPickNotAudit request = new RefundForNewPickNotAudit();
            request.setTradeVO(tradeVO);
            request.setReturnOrder(returnOrder);
            refundOrderProvider.refundForNewPickNotAudit(request);

            //退商品库存
            returnSkuStock(returnOrder);

            //如果囤货单全部已退，返还优惠券
            returnOrderProvider.returnCoupon(returnOrder.getId());

            //推erp
//            TradePushKingdeeOrderRequest request = new TradePushKingdeeOrderRequest();
//            request.setOrderIds(returnOrder.getId());
//            request.setOrderType(KingdeeAbnormalOrderEnum.REFUNDORDER);
//            tradeProvider.pushKingdeeCompensationOrder(request);

            log.info("=============== 处理来源于新囤货订单的提货退款 end =============== 单号={}", refundRequest.getRId());
        } finally {
            rLock.unlock();
        }
    }

    private void updateReturnOrderFlowState(ReturnOrderVO returnOrder) {
        UpdateReturnFlowStateRequest request = new UpdateReturnFlowStateRequest();
        request.setRid(returnOrder.getId());
        request.setReturnFlowState(ReturnFlowState.COMPLETED);
        request.setWmsPushState(returnOrder.getWmsPushState());
        returnOrderProvider.updateReturnFlowState(request);

        //TODO: 重构为使用状态机统一处理
        inventoryDetailSamountProvider.returnAmountByRid(returnOrder.getId());
    }

    /**
     * 检查退单是否可退款
     *
     * @param returnOrder
     * @return
     */
    private boolean checkReturnOrder(ReturnOrderVO returnOrder, TradeVO trade) {
        Assert.notNull(trade, "trade 不能为null");
        if (Objects.isNull(returnOrder)) {
            log.info("退单不存在，退单id={}", returnOrder.getId());
            return false;
        }
        boolean isRefund = trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || trade
                .getTradeState().getDeliverStatus()
                == DeliverStatus.VOID;
        if (isRefund) {
            if (!Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.AUDIT)) {
                log.info("退单不在【已审核】状态，不再处理。。。退单id={}", returnOrder.getId());
                return false;
            }
        } else {
            //是否为已退款状态？不再重复处理
            if (!Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.INIT)) {
                log.info("退单不在【创建退单】状态，不再处理。。。退单id={}", returnOrder.getId());
                return false;
            }
        }
        return true;
    }


    @Autowired
    GoodsInfoProvider goodsInfoProvider;

    /**
     * 回退goods库存 【{skuId,stock,newPileOrderNo,newPileReturnOrderNo}】
     *
     * @param returnOrder
     */
    private void returnSkuStock(ReturnOrderVO returnOrder) {
        Map<String, Long> skuReturnNumMap = returnOrder.getReturnItems().stream()
                .flatMap((returnItemVO -> returnItemVO.getReturnGoodsList().stream()))
                .collect(Collectors.groupingBy(PickGoodsDTO::getGoodsInfoId, Collectors.summingLong(PickGoodsDTO::getNum))
                );

        Long wareId = returnOrder.getWareId();
        List<GoodsInfoPlusStockDTO> stockList = skuReturnNumMap.entrySet().stream().map(item -> {
            GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
            dto.setStock(BigDecimal.valueOf(item.getValue()));
            dto.setGoodsInfoId(item.getKey());
            dto.setWareId(wareId);
            return dto;
        }).collect(Collectors.toList());

        GoodsInfoBatchPlusStockRequest request = new GoodsInfoBatchPlusStockRequest();
        request.setStockList(stockList);
        request.setWareId(wareId);
        goodsInfoProvider.batchPlusStock(request);
    }

    /**
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_CONSUMER)
    public void refundOrderNotAuditNewPileProducer(String json) {
        try {
            log.info("=============== Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_PRODUCER退单无审核直接退款start ===============");
            RefundOrderNotAuditProducerRequest message = JSONObject.parseObject(json, RefundOrderNotAuditProducerRequest.class);
            //审核
            ReturnOrderOnlineModifyPriceRequest request = new ReturnOrderOnlineModifyPriceRequest();

            BigDecimal refundPrice = returnOrderQueryProvider.queryRefundPriceNewPile(ReturnOrderQueryRefundPriceRequest.builder()
                    .rid(message.getRId()).build()).getContext().getRefundPrice();
            request.setActualReturnPrice(refundPrice);

            NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(message.getRId())
                    .build()).getContext();

            BaseResponse baseResponse = returnOrderProvider.onlineEditPriceNewPile(ReturnOrderOnlineModifyPriceRequest.builder()
                    .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                    .refundComment(request.getRefundComment())
                    .actualReturnPrice(request.getActualReturnPrice())
                    .actualReturnPoints(request.getActualReturnPoints())
                    .operator(message.getOperator()).build());


            if (baseResponse.getCode().equals("K-000000")) {
                //打款
                Operator operator = message.getOperator();
                BaseResponse<Object> res = returnOrderProvider.refundOnlineByTidNewPile(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(message.getRId())
                        .operator(operator).build());
                Object data = res.getContext();
                if (Objects.isNull(data)) {
                    //无返回信息，追踪退单退款状态
                    NewPileReturnFlowState returnFlowState = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(message.getRId()).build()).getContext().getReturnFlowState();
                    if (!returnFlowState.equals(NewPileReturnFlowState.COMPLETED)) {
                        log.info("退款失败-------->" + json);
                    }
                }
            }
            log.info("=============== Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_PRODUCER退单无审核直接退款end ===============");
        } catch (Exception e) {
            log.error("退单无审核直接退款! param={}", json, e);
        }
    }
}
