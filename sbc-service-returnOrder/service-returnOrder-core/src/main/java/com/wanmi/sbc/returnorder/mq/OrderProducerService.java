package com.wanmi.sbc.returnorder.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.goods.api.constant.GoodsJmsDestinationConstants;
import com.wanmi.sbc.goods.api.request.goods.GoodsLackStockRequest;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.GrouponGoodsInfoModifyAlreadyGrouponNumRequest;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.GrouponGoodsInfoModifyStatisticsNumRequest;
import com.wanmi.sbc.marketing.api.provider.constants.MarketingJmsDestinationConstants;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityModifyStatisticsNumByIdRequest;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.returnorder.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.returnorder.api.constant.MessageConstants;
import com.wanmi.sbc.returnorder.api.request.trade.HistoryLogisticCompanyRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeOutRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeRefreshInventoryRequest;
import com.wanmi.sbc.returnorder.bean.vo.GrouponInstanceVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.shopcart.BulkShopCartVO;
import com.wanmi.sbc.returnorder.shopcart.RetailShopCartVo;
import com.wanmi.sbc.returnorder.shopcart.ShopCartVO;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.model.root.TradePushPayOrderGroupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 订单状态变更生产者
 * @Autho qiaokang
 * @Date：2019-03-05 17:47:18
 */
@Service
@EnableBinding
public class OrderProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 订单支付后，发送MQ消息
     * @param tradeVO
     */
    public void sendMQForOrderPayed(TradeVO tradeVO) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_PAYED).send(new GenericMessage<>(JSONObject.toJSONString(tradeVO)));

    }

    /**
     * 加购成功后，发生MQ消息
     * @param bulkShopCart
     */
    public void sendMQForOrderBulkShopCar(BulkShopCartVO bulkShopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_BULK_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(bulkShopCart)));
    }

    /**
     * 加购成功后，发生MQ消息
     * @param shopCart
     */
    public void sendMQForOrderShopCar(ShopCartVO shopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(shopCart)));
    }

    /**
     * 加购成功后，发生MQ消息
     * @param retailShopCartVo
     */
    public void sendMQForOrderRetailShopCar(RetailShopCartVo retailShopCartVo){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_RETAIL_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(retailShopCartVo)));
    }


    /**
     * 加购成功后，发生MQ消息
     * @param shopCart
     */
    public void sendMQForOrderStoreShopCar(ShopCartVO shopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(shopCart)));
    }




    /**
     * 发送订单支付、订单完成MQ消息
     * @param tradeVO
     */
    public void sendMQForOrderPayedAndComplete(TradeVO tradeVO) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_PAYED_AND_COMPLETE).send(new GenericMessage<>(JSONObject.toJSONString(tradeVO)));

    }

    /**
     * 订单完成，发送MQ消息
     * @param tradeId
     */
    public void sendMQForOrderComplete(String tradeId) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_COMPLETE).send(new GenericMessage<>(tradeId));

    }

    /**
     * 积分订单完成，发送MQ消息
     * @param tradeId
     */
    public void sendMQForPointsOrderComplete(String tradeId) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_POINTS_ORDER_COMPLETE).send(new GenericMessage<>(tradeId));

    }

    /**
     * 分销订单退款作废后，发送MQ消息
     * @param tradeVO
     */
    public void sendMQForOrderRefundVoid(TradeVO tradeVO) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_REFUND_VOID).send(new GenericMessage<>(JSONObject.toJSONString(tradeVO)));

    }

    /**
     * 超过一定时间未支付订单，自动取消订单
     * @param orderId
     * @param millis
     * @return
     */
    public Boolean cancelOrder(String orderId,Long millis){
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_PRODUCER).send
                (MessageBuilder.withPayload(orderId).setHeader("x-delay", millis ).build());
        return send;
    }


    /**
     * 超过一定时间未支付订单，自动取消订单
     * @param orderId
     * @param millis
     * @return
     */
    public Boolean cancelOrderPileNew(String orderId,Long millis){
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_NEW_PILE_PRODUCER).send
                (MessageBuilder.withPayload(orderId).setHeader("x-delay", millis ).build());
        return send;
    }

    /**
     * 超过一定时间未支付订单，自动取消订单
     * @param orderId
     * @param millis
     * @return
     */
    public Boolean cancelPileOrder(String orderId,Long millis){
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_PILE_ORDER_PRODUCER).send
                (MessageBuilder.withPayload(orderId).setHeader("x-delay", millis ).build());
        return send;
    }

    /**
     * 团长开团-开启延迟消息
     * @param grouponNo
     * @param millis
     * @return
     */
    public Boolean sendOpenGroupon(String grouponNo,Long millis){
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_OPEN_GROUPON_PRODUCER).send
                (MessageBuilder.withPayload(grouponNo).setHeader("x-delay", millis ).build());
        return send;
    }

    /**
     * 团长开团- C端消息推送
     * @param grouponNo
     * @param grouponActivityId
     * @return
     */
    public Boolean sendOpenGrouponMsgToC(String grouponNo,String grouponActivityId){
        GrouponInstanceVO grouponInstanceVO = new GrouponInstanceVO();
        grouponInstanceVO.setGrouponActivityId(grouponActivityId);
        grouponInstanceVO.setGrouponNo(grouponNo);
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_OPEN_GROUPON_MESSAGE_PUSH_TO_C).send
                (new GenericMessage<>(JSONObject.toJSONString(grouponInstanceVO)));
        return send;
    }


    /**
     * 根据不同拼团状态更新不同的统计数据（已成团、待成团、团失败人数）
     * @param grouponActivityId
     * @param grouponNum
     * @param grouponOrderStatus
     * @return
     */
    public Boolean updateStatisticsNumByGrouponActivityId(String grouponActivityId, Integer grouponNum, GrouponOrderStatus grouponOrderStatus){
        GrouponActivityModifyStatisticsNumByIdRequest request = new GrouponActivityModifyStatisticsNumByIdRequest(grouponActivityId,grouponNum,grouponOrderStatus);
        return resolver.resolveDestination(MarketingJmsDestinationConstants.Q_MARKET_GROUPON_MODIFY_STATISTICS_NUM).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

    /**
     * 根据活动ID、SKU编号集合批量更新已成团人数
     * @param grouponActivityId
     * @param goodsInfoIds
     * @param alreadyGrouponNum
     * @return
     */
    public Boolean updateAlreadyGrouponNumByGrouponActivityIdAndGoodsInfoId(String grouponActivityId, List<String> goodsInfoIds, Integer alreadyGrouponNum){
        GrouponGoodsInfoModifyAlreadyGrouponNumRequest request = new GrouponGoodsInfoModifyAlreadyGrouponNumRequest(grouponActivityId,goodsInfoIds,alreadyGrouponNum);
        return resolver.resolveDestination(GoodsJmsDestinationConstants.Q_GROUPON_GOODS_INFO_MODIFY_ALREADY_GROUPON_NUM).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

    /**
     * 根据活动ID、SKU编号更新商品销售量、订单量、交易额
     * @param grouponActivityId
     * @param goodsInfoId
     * @param goodsSalesNum
     * @param orderSalesNum
     * @param tradeAmount
     * @return
     */
    public Boolean updateOrderPayStatisticNumByGrouponActivityIdAndGoodsInfoId(String grouponActivityId, String goodsInfoId, Integer goodsSalesNum,
                                                                       Integer orderSalesNum, BigDecimal tradeAmount){
        GrouponGoodsInfoModifyStatisticsNumRequest request = new GrouponGoodsInfoModifyStatisticsNumRequest(grouponActivityId,goodsInfoId,goodsSalesNum,orderSalesNum,tradeAmount);
        return resolver.resolveDestination(GoodsJmsDestinationConstants.Q_GROUPON_GOODS_INFO_MODIFY_ORDER_PAY_STATISTICS).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

    /**
     * 拼团订单-支付成功，订单异常，自动退款
     * @param trade
     */
    public void sendGrouponOrderAutoRefund(Trade trade) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_PAY_SUCCESS_AUTO_REFUND).send(new GenericMessage<>(JSONObject.toJSONString(trade)));
    }

    /**
     * 发送push、站内信、短信
     * @param request
     */
    public void sendMessage(MessageMQRequest request){
        resolver.resolveDestination(MessageConstants.Q_SMS_SERVICE_MESSAGE_SEND).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

    /**
     * 拼团结束3小时前校验拼团人数
     * @param grouponNo
     * @param millis
     * @return
     */
    public boolean sendGrouponNumLimit(String grouponNo, Long millis){
        Boolean send = resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_JOIN_NUM_LIMIT_PRODUCER).send
                (MessageBuilder.withPayload(grouponNo).setHeader("x-delay", millis ).build());
        return send;
    }

    /**
     * wms库存校验 —— 同步库存和到货通知入记录
     * @return
     */
    public boolean sendMessageToDelLackStock(GoodsLackStockRequest lackStockRequest){
        Boolean send = resolver.resolveDestination(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_LACK_GOODS_STOCK)
                .send(new GenericMessage<>(JSONObject.toJSONString(lackStockRequest)));
        return send;
    }

    /**
     * 业务员交接数据
     * @param company
     */
    public void insertCompany(HistoryLogisticCompanyRequest company){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_INSERT_HISTORY_COMPANY_DATA).send(new GenericMessage<>(JSONObject.toJSONString(company)));
    }

    /**
     * 库存刷新es
     * @param
     */
    public void stockRefreshEs(TradeRefreshInventoryRequest request){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_STOCK_REFRESH_ES).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

    /***
     * @desc  之前是延迟 3*60*1000L，现在改为15秒
     * @author shiy  2023/8/4 22:28
    */
    public boolean delayPushingPaymentOrders(TradePushPayOrderGroupon pushPayOrderGroupon){
        return delayPushingPaymentOrders(pushPayOrderGroupon,2*60*1000L);
    }

    /**
     * 延时3分钟，向wms推送支付订单状态
     * @param pushPayOrderGroupon
     * @param millis
     * @return
     */
    public boolean delayPushingPaymentOrders(TradePushPayOrderGroupon pushPayOrderGroupon, Long millis){
        return resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_DELAY_PUSHING_PAYMENT_PRODUCER)
                       .send(MessageBuilder.withPayload(pushPayOrderGroupon).setHeader("x-delay", millis ).build());
    }

    /**
     * 向金蝶推送囤货订单数据
     * @param tid
     */
    public void newPilekingdeePushShopCartOrder(String tid,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_NEW_PILE_ORDER_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay", millis).build());
    }

    /**
     * 购物车向金蝶推送订单数据
     * @param tid
     */
    public void shoppingCartKingdeePushOrder(String tid,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_SHOPPINGCART_KINGDEE_PUSH_ORDER_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay", millis).build());
    }

    /**
     * 向金蝶推送订单数据
     * @param tid
     */
    public void kingdeePushOrder(String tid,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_ORDER_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay", millis).build());
    }

    /**
     * 取消金蝶订单
     * @param returnOrder
     * @param millis
     */
    public void kingdeeCancelOrder(ReturnOrder returnOrder,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_KINGDEE_ORDER_PRODUCER).send(MessageBuilder.withPayload(returnOrder.getTid()).setHeader("x-delay", millis).build());
    }

    /**
     * 推囤货到金蝶
     * @param id
     * @param millis
     */
    public void pushStockkingdee(String id, Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_KINGDEE_PRODUCER).send(MessageBuilder.withPayload(id).setHeader("x-delay", millis).build());
    }

    /**
     * 推囤货退货到金蝶
     * @param rid
     * @param millis
     */
    public void pushStockReturnGoodsKingdee(String rid,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_RETURN_GOODS_KINGDEE_PRODUCER).send(MessageBuilder.withPayload(rid).setHeader("x-delay", millis).build());
    }
    public void pushOrderPayWMS(String tid){
        pushOrderPayWMS(tid,10*1000L);
    }

    /**
     * 电商推wms支付单
     * @param tid
     * @param millis
     */
    public void pushOrderPayWMS(String tid,Long millis){
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_WMS_PAY_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay",millis).build());
    }

    /**
     * 购买指定商品赠券
     * @param tid
     */
    public void buyGoodsSendCoupon(String tid) {
        resolver.resolveDestination(JmsDestinationConstants.Q_BUY_GOODS_SEND_COUPON).send(new GenericMessage<>(tid));
    }

    /**
     * 热销排行榜埋点数据
     * @param tid
     */
    public void hotSaleArea(String tid) {
        resolver.resolveDestination(JmsDestinationConstants.Q_HOT_SALE_AREA).send(new GenericMessage<>(tid));
    }

    public void wmsAutoChargeback(TradeOutRequest request) {
        resolver.resolveDestination(MessageConstants.Q_ORDER_SERVICE_MESSAGE_WMS_AUTO_CHARGEBACK).send(new GenericMessage<Object>(JSONObject.toJSONString(request)));
    }

    public void pushCommisionToKingdee(String tid, long millis) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_PAYMENT_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay", millis ).build());
    }

    public void pushRefundCommisionToKingdee(String tid, long millis) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_PRODUCER).send(MessageBuilder.withPayload(tid).setHeader("x-delay", millis ).build());
    }
}
