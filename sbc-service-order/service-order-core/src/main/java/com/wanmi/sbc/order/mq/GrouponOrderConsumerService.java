package com.wanmi.sbc.order.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.rabbitmq.client.Channel;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.node.OrderProcessType;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.vo.GrouponActivityVO;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.request.trade.TradeCancelRequest;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.common.KingdeePushOrder;
import com.wanmi.sbc.order.groupon.service.GrouponOrderService;
import com.wanmi.sbc.order.payorder.response.PayOrderResponse;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.service.GrouponReturnOrderService;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.returnorder.service.ReturnPileOrderService;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.*;
import com.wanmi.sbc.order.trade.service.PileTradeService;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.ChannelItemByIdRequest;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 拼团订单消息处理
 */
@Service
@Slf4j
@EnableBinding(OrderSink.class)
public class GrouponOrderConsumerService {

    @Autowired
    private GrouponOrderService grouponOrderService;

    @Autowired
    private GrouponReturnOrderService grouponReturnOrderService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private PileTradeService pileTradeService;

    @Autowired
    private KingdeePushOrder kingdeePushOrder;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    /**
     * 取消订单
     * @param orderId
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_CONSUMER)
    @LcnTransaction
    public void cancelOrder(String orderId) {
        log.info("订单号：{},取消订单MQ消息，开始运行处理",orderId);
        Operator operator = Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                .PLATFORM).build();
        tradeService.autoCancelOrder(orderId,operator);
    }

    /**
     * 新版本囤货取消订单
     * @param orderId
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_NEW_PILE_CONSUMER)
    @LcnTransaction
    public void cancelNewPilelOrder(String orderId) {
        log.info("新版本囤货订单号：{},取消订单MQ消息，开始运行处理",orderId);
        Operator operator = Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                .PLATFORM).build();

        TradeCancelRequest request = new TradeCancelRequest();
        request.setTid(orderId);
        request.setOperator(operator);
        newPileTradeService.autoCancelOrder(request);
    }

    /**
     * 取消订单
     * @param orderId
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_PILE_ORDER_CONSUMER)
    @LcnTransaction
    public void cancelPileOrder(String orderId) {
        log.info("订单号：{},取消囤货订单MQ消息，开始运行处理",orderId);
        Operator operator = Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                .PLATFORM).build();
        pileTradeService.autoCancelOrder(orderId,operator);
    }

    /**
     * 团长开团MQ处理
     *
     * @param grouponNo
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_OPEN_GROUPON_CONSUMER)
    @LcnTransaction
    public void openGroupon(String grouponNo) {
        log.info("团编号：{},延迟消息开始运行处理",grouponNo);
        //根据团编号查询团长开团信息
        GrouponInstance grouponInstance = grouponOrderService.getGrouponInstanceByActivityIdAndGroupon(grouponNo);
        if (null == grouponInstance || StringUtils.isBlank(grouponInstance.getGrouponActivityId())){
            log.info("团编号:{},未查询到拼团订单数据，请检查团编号是否正确！",grouponNo);
            return;
        }
        GrouponActivityVO grouponActivityVO = grouponOrderService.getGrouponActivityById(grouponInstance.getGrouponActivityId());
        if (Objects.isNull(grouponActivityVO)){
            log.info("团编号:{},活动ID:{},未查询到拼团活动信息，请检查团编号是否正确！",grouponNo,grouponInstance.getGrouponActivityId());
            return;
        }
        log.info("团编号：{},具体的团实例信息如下:{}",grouponNo,grouponInstance);
        //已成团/团已作废不作处理
        if (grouponInstance.getGrouponStatus() == GrouponOrderStatus.COMPLETE || grouponInstance.getGrouponStatus() == GrouponOrderStatus.FAIL ){
            log.info("团编号：{},已成团或者已作废,不作任何处理！",grouponNo);
            return;
        }else{
            //团截止时间
            LocalDateTime endTime =  grouponInstance.getEndTime();
            log.info("团编号:{}，截止时间:{},当前系统时间:{}", grouponNo, endTime, LocalDateTime.now());
            //倒计时结束（已超团结束时长24小时) && 团状态未成团
            if (grouponInstance.getGrouponStatus() == GrouponOrderStatus.WAIT){
                boolean autoGroupon = grouponActivityVO.isAutoGroupon();
                final LocalDateTime currentTime = LocalDateTime.now();
                //自动成团
                if (autoGroupon){
                    //1、更新订单信息-已成团、待发货
                    grouponOrderService.autoGrouponSuccess(grouponNo,Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                            .PLATFORM).build());
                    //3、修改团实例信息
                    grouponInstance.setCompleteTime(currentTime);
                    grouponInstance.setGrouponStatus(GrouponOrderStatus.COMPLETE);
                    grouponOrderService.updateGrouponInstance(grouponInstance);
                    grouponInstance.setGrouponNum(grouponInstance.getJoinNum());
                    //更新拼团活动-已成团、待成团、团失败人数；拼团活动商品-已成团人数
                    grouponOrderService.updateStatisticsNum(grouponInstance);
                }else{
                    //自动退款
                    grouponReturnOrderService.handleGrouponOrderRefund(grouponNo);
                }
            }
        }
    }

    /**
     * 拼团订单-支付成功，订单异常，自动退款
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_PAY_SUCCESS_AUTO_REFUND)
    @LcnTransaction
    public void handleGrouponOrderRefund(String json) {
        try {
            log.info("订单信息：{}，拼团订单-支付成功，订单异常，自动退款,开始运行处理!",json);
            Trade trade = JSONObject.parseObject(json,Trade.class);
            grouponReturnOrderService.handleGrouponOrderRefund(trade);
        } catch (Exception e) {
            log.error("订单信息：{}，拼团订单-支付成功，订单异常，自动退款! param={}", json, e);
        }
    }

    /**
     * 拼团人员不足MQ处理
     * @param grouponNo
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_JOIN_NUM_LIMIT_CONSUMER)
    public void handleGrouponNumLimit(String grouponNo){
        log.info("团编号：{},拼团人员不足开始运行处理",grouponNo);
        //根据团编号查询团长开团信息
        GrouponInstance grouponInstance = grouponOrderService.getGrouponInstanceByActivityIdAndGroupon(grouponNo);
        if (null == grouponInstance || StringUtils.isBlank(grouponInstance.getGrouponActivityId())){
            log.info("团编号:{},未查询到拼团订单数据，请检查团编号是否正确！",grouponNo);
            return;
        }
        GrouponActivityVO grouponActivityVO = grouponOrderService.getGrouponActivityById(grouponInstance.getGrouponActivityId());
        if (Objects.isNull(grouponActivityVO)){
            log.info("团编号:{},活动ID:{},未查询到拼团活动信息，请检查团编号是否正确！",grouponNo,grouponInstance.getGrouponActivityId());
            return;
        }
        log.info("团编号：{},具体的团实例信息如下:{}",grouponNo,grouponInstance);
        //已成团/团已作废不作处理
        if (grouponInstance.getGrouponStatus() == GrouponOrderStatus.COMPLETE || grouponInstance.getGrouponStatus() == GrouponOrderStatus.FAIL ){
            log.info("团编号：{},已成团或者已作废,不作任何处理！",grouponNo);
            return;
        }else{
            //团截止时间
            LocalDateTime endTime =  grouponInstance.getEndTime();
            log.info("团编号:{}，截止时间:{},当前系统时间:{}", grouponNo, endTime, LocalDateTime.now());
            //团状态未成团
            if (grouponInstance.getGrouponStatus() == GrouponOrderStatus.WAIT){
                List<Trade> tradeList = grouponOrderService.getTradeByGrouponNo(grouponNo);
                for (Trade trade : tradeList) {
                    grouponOrderService.sendNoticeMessage(NodeType.ORDER_PROGRESS_RATE, OrderProcessType.GROUP_NUM_LIMIT, trade, grouponNo, grouponActivityVO.getWaitGrouponNum());
                }
            }
        }
    }

    /**
     * 延时向wms推送支付订单
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_DELAY_PUSHING_PAYMENT_CONSUMER)
    public void delayPushingPayment(String json){
        log.info("OrderConsumerService.delayPushingPayment req json:{}",json);
        TradePushPayOrderGroupon pushPayOrderGroupon = JSONObject.parseObject(json,TradePushPayOrderGroupon.class);
        if (Objects.isNull(pushPayOrderGroupon)){
            return;
        }
        try {
            if (StringUtils.isNotEmpty(pushPayOrderGroupon.getOrderCode()) && StringUtils.isNotEmpty(pushPayOrderGroupon.getPayCode())) {

                if(pushPayOrderGroupon.getOrderCode().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)){
                    log.info("======================推送囤货订单数据：{}", pushPayOrderGroupon.getOrderCode());
                    NewPileTrade trade = newPileTradeService.detail(pushPayOrderGroupon.getOrderCode());

                    //查询销售订单对应的支付单数据
                    TradePushKingdeePayOrder payOrder = tradeService.selectPushKingdeePayOrder(pushPayOrderGroupon.getPayCode());
                    //推送金蝶
                    if (Objects.nonNull(trade) && Objects.nonNull(payOrder)) {
                        PayOrderResponse payOrders = new PayOrderResponse();
                        payOrders.setPayOrderId(payOrder.getPayCode());
                        payOrders.setTotalPrice(payOrder.getPracticalPrice());
                        if(null != trade.getTradePrice().getBalancePrice() && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) != 0){
                            //用了鲸币
                            payOrders.setTotalPrice(payOrder.getPracticalPrice().subtract(trade.getTradePrice().getBalancePrice()));
                        }else{
                            payOrders.setTotalPrice(payOrder.getPracticalPrice());
                        }
                        //向金蝶推送成功，才会推wms
                        newPileTradeService.pushPayOrderKingdee(trade, payOrders, PayWay.valueOf(payOrder.getPayType().toUpperCase()));
                        newPileTradeService.updateTrade(trade);
                    }
                }else{
                    Trade trade = tradeService.detail(pushPayOrderGroupon.getOrderCode());

                    //查询销售订单对应的支付单数据
                    TradePushKingdeePayOrder payOrder = tradeService.selectPushKingdeePayOrder(pushPayOrderGroupon.getPayCode());
                    //推送金蝶
                    if (Objects.nonNull(trade) && Objects.nonNull(payOrder)){
                        PayOrderResponse payOrders = new PayOrderResponse();
                        payOrders.setPayOrderId(payOrder.getPayCode());
                        payOrders.setTotalPrice(payOrder.getPracticalPrice());
                        if(null != trade.getTradePrice().getBalancePrice() && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) != 0){
                            //用了鲸币
                            payOrders.setTotalPrice(payOrder.getPracticalPrice().subtract(trade.getTradePrice().getBalancePrice()));
                        }else{
                            payOrders.setTotalPrice(payOrder.getPracticalPrice());
                        }
                        //向金蝶推送成功，才会推wms
                        tradeService.pushPayOrderKingdee(trade, payOrders, PayWay.valueOf(payOrder.getPayType().toUpperCase()));
//                    if (tradeService.pushConfirmOrder(trade)){
//                        trade.setWMSPushFlag(true);
//                    }else {
//                        trade.setWMSPushFlag(false);
//                    }
                        tradeService.updateTrade(trade);
                    }
                }
            }
        }catch (Exception e){
            log.error("OrderConsumerService.delayPushingPayment tradeId:{} error:{}",pushPayOrderGroupon.getOrderCode(),e);
        }
    }

    /**
     * 向金蝶推送订单数据
     * @param tradeJson
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_ORDER_CONSUMER)
    public void kingdeePushOrder(String tradeJson){
        if (StringUtils.isNotEmpty(tradeJson)) {
            log.info("GrouponOrderConsumerService.kingdeePushOrder tradeId:{}", tradeJson);
            try {
                kingdeePushOrder.asyncPushSalesOrderkingdee(tradeJson);
            } catch (Exception e) {
                log.error("GrouponOrderConsumerService.kingdeePushOrder error tradeId:{} err:{}", tradeJson, e);
            }
        }
    }

    /**
     * 购物车向金蝶推送订单数据
     * @param tradeJson
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_SHOPPINGCART_KINGDEE_PUSH_ORDER_CONSUMER)
    public void shoppingCartKingdeePushOrder(String tradeJson){
        if (StringUtils.isNotEmpty(tradeJson)) {
            log.info("GrouponOrderConsumerService.shoppingCartKingdeePushOrder tradeId:{}", tradeJson);
            try {
                kingdeePushOrder.shoppingCartPushSalesOrderkingdee(tradeJson);
            } catch (Exception e) {
                log.error("GrouponOrderConsumerService.shoppingCartKingdeePushOrder error tradeId:{} err:{}", tradeJson, e);
            }
        }
    }

    /**
     * 囤货购物车向金蝶推送订单数据
     * @param tradeJson
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_NEW_PILE_ORDER_CONSUMER)
    public void newPilekingdeePushShopCartOrder(String tradeJson){
        if (StringUtils.isNotEmpty(tradeJson)) {
            log.info("GrouponOrderConsumerService.shoppingCartKingdeePushOrder tradeId:{}", tradeJson);
            try {
                kingdeePushOrder.newPileShoppingCartPushSalesOrderkingdee(tradeJson);
            } catch (Exception e) {
                log.error("GrouponOrderConsumerService.shoppingCartKingdeePushOrder error tradeId:{} err:{}", tradeJson, e);
            }
        }
    }

    /**
     * 取消金蝶订单消费者
     * @param returnOrderJson
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_KINGDEE_ORDER_CONSUMER)
    public void kingdeeCancelOrder(String returnOrderJson){
        log.info("GrouponOrderConsumerService.kingdeeCancelOrder req returnOrderJson:{}",returnOrderJson);
        if (StringUtils.isNotEmpty(returnOrderJson)){
            log.info("GrouponOrderConsumerService.kingdeeCancelOrder returnOrderId:{}",returnOrderJson);
            try {
                ReturnOrder returnOrder = ReturnOrder.builder()
                                                     .tid(returnOrderJson)
                                                     .build();
                returnOrderService.cancelOrder(returnOrder,false);
            }catch (Exception e){
                log.error("GrouponOrderConsumerService.kingdeeCancelOrder error returnOrderId:{}e:{}",returnOrderJson,e);
            }
        }
    }

    /**
     * 推金蝶囤货单
     * @param id
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_KINGDEE_CONSUMER)
    public void pushStockkingdee(String id){
        log.info("GrouponOrderConsumerService.pushStockkingdee id:{}",id);
        if (StringUtils.isNotEmpty(id)){
            PileTrade pileTrade = pileTradeService.detail(id);
            if (pileTrade.getTradeState().getPayState().equals(PayState.PAID)
                    && pileTrade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                    && pileTrade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                    && pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                performPushStockkingdee(pileTrade);
            }else if (pileTrade.getTradeState().getPayState().equals(PayState.PAID)
                    && pileTrade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                    && pileTrade.getTradeState().getFlowState().equals(FlowState.VOID)
                    && pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)){
                performPushStockkingdee(pileTrade);
            }
        }
    }

    private void performPushStockkingdee(PileTrade pileTrade){
        PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                                                                                                           TradeRecordByOrderCodeRequest(pileTrade.getId())).getContext();
        PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                                                                                                 ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                                                                                                                                payTradeRecordResponse.getChannelItemId())).getContext();
        if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
            pileTradeService.pushKingdeeStockOrders(pileTrade,false,PayWay.UNIONPAY);
        }else {
            pileTradeService.pushKingdeeStockOrders(pileTrade,false,PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
        }
    }

    /**
     * 推囤货退货单到金蝶
     * @param rid
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_RETURN_GOODS_KINGDEE_CONSUMER)
    public void pushStockReturnGoodsKingdee(String rid){
        log.info("GrouponOrderConsumerService.pushStockReturnGoodsKingdee rid:{}",rid);
        if (StringUtils.isNotEmpty(rid)) {
            ReturnPileOrder returnPileOrder = returnPileOrderService.findById(rid);
            log.info("GrouponOrderConsumerService.pushStockReturnGoodsKingdee returnPileOrder:{}",JSONObject.toJSONString(returnPileOrder));
            if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.REFUNDED) || returnPileOrder.getReturnFlowState().equals(ReturnFlowState.COMPLETED)) {
                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                                                                                                                   TradeRecordByOrderCodeRequest(returnPileOrder.getTid())).getContext();
                PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                                                                                                         ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                        payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                                                                                                                                        payTradeRecordResponse.getChannelItemId())).getContext();

                if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
                    returnPileOrderService.pushKingdeeReturnGoods(returnPileOrder,true,PayWay.UNIONPAY);
                }else {
                    returnPileOrderService.pushKingdeeReturnGoods(returnPileOrder,true,PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                }
            }
        }
    }

    /**
     * 电商推wms支付单
     * @param tid
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_WMS_PAY_CONSUMER)
    public void pushWmsPay(String tid){
        log.info("GrouponOrderConsumerService.pushWmsPay tid:{}",tid);
        Trade trade = tradeService.detail(tid);
        if (Objects.nonNull(trade)){
            tradeService.pushConfirmOrder(trade);
        }
    }

    /**
     * 订单确定支付延时检查(消费端)
     *
     * @param tidsJson
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_CONFIRM_PAY_CONSUMER)
    public void confirmPayConsumer(String tidsJson,
                                   @Header(AmqpHeaders.CHANNEL) Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws IOException {
        log.info("订单确定支付延时检查(消费端)========》{}", tidsJson);
        List<String> tids = JSONArray.parseArray(tidsJson, String.class);
        tradeService.cancelPay(tids);
        channel.basicAck(deliveryTag, false);
    }
}
