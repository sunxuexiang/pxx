package com.wanmi.sbc.returnorder.trade.fsm.action;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.node.OrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.fsm.TradeAction;
import com.wanmi.sbc.returnorder.trade.fsm.TradeStateContext;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.ShippingItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
@Slf4j
public class DeliverAction extends TradeAction {

    @Override
    protected void evaluateInternal(Trade trade, StateRequest request, TradeStateContext tsc) {
        deliver(trade, tsc.getOperator(), tsc.getRequestData());
    }

    /**
     * 是否全部发货
     *
     * @return
     */
    /*private boolean isAllShipped(Trade trade) {
        List<TradeItem> allCollect = new ArrayList<>();
        //添加赠品
        allCollect.addAll(trade.getTradeItems());
        allCollect.addAll(trade.getGifts());
        List<TradeItem> collect = allCollect
                .stream()
                .filter(tradeItem -> !Objects.equals(tradeItem.getDeliveredNum(), tradeItem.getNum()))
                .collect(Collectors.toList());
        return collect.isEmpty();
    }*/

    /**
     * 是否全部发货
     *
     * @return
     */
    private boolean isAllShipped(Trade trade,TradeDeliver tradeDeliver) {
        List<TradeItem> allCollect = new ArrayList<>();
        //添加赠品
        allCollect.addAll(trade.getTradeItems());
        allCollect.addAll(trade.getGifts());
        List<TradeItem> collect = allCollect
                .stream()
                .filter(tradeItem -> !Objects.equals(tradeItem.getDeliveredNum(), tradeItem.getNum()))
                .collect(Collectors.toList());
        return collect.isEmpty() ||
                (Objects.nonNull(tradeDeliver.getDeliverAll()) && tradeDeliver.getDeliverAll().equals(1));
    }

    /**
     * 发货
     *
     * @param trade
     * @param tradeDeliver
     */
    /*private void deliver(Trade trade, Operator operator, TradeDeliver tradeDeliver) {

        // 如果没有收货信息, 就用之前的存的收货信息
        if (tradeDeliver.getConsignee() == null) {
            tradeDeliver.setConsignee(trade.getConsignee());
        }

        StringBuilder stringBuilder = new StringBuilder(200);

        //处理商品
        handleShippingItems(trade, tradeDeliver.getShippingItems(), stringBuilder, operator, false);

        //处理赠品
        handleShippingItems(trade, tradeDeliver.getGiftItemList(), stringBuilder, operator, true);

        stringBuilder.trimToSize();
        tradeDeliver.setStatus(DeliverStatus.SHIPPED);
        trade.addTradeDeliver(tradeDeliver);
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventType(FlowState.DELIVERED.getDescription())
                .eventDetail(stringBuilder.toString())
                .eventTime(LocalDateTime.now())
                .build());

        // 更改发货状态
        boolean allShipped = isAllShipped(trade);
        if (allShipped) {
            trade.getTradeState().setDeliverTime(LocalDateTime.now());
            trade.getTradeState().setDeliverStatus(DeliverStatus.SHIPPED);
            trade.getTradeState().setFlowState(FlowState.DELIVERED);
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventType(FlowState.DELIVERED.getDescription())
                    .eventDetail(String.format("订单[%s],已全部发货,发货人:%s", trade.getId(), operator.getName()))
                    .eventTime(LocalDateTime.now())
                    .build());
        } else {
            trade.getTradeState().setDeliverStatus(DeliverStatus.PART_SHIPPED);
            trade.getTradeState().setFlowState(FlowState.DELIVERED_PART);
        }

        if (logger.isInfoEnabled()) {
            logger.info(String.format("订单[%s] => 发货状态[ %s ], 流程状态[ %s ]", trade.getId(), trade.getTradeState().getDeliverStatus(), trade.getTradeState().getFlowState()));
        }

        save(trade);
        if (allShipped) {
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(0).getEventDetail());
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(1).getEventDetail());
        } else {
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(0).getEventDetail());
        }
    }*/

    private void deliver(Trade trade, Operator operator, TradeDeliver tradeDeliver) {

        // 如果没有收货信息, 就用之前的存的收货信息
        if (tradeDeliver.getConsignee() == null) {
            tradeDeliver.setConsignee(trade.getConsignee());
        }

        StringBuilder stringBuilder = new StringBuilder(200);

        logger.info("getShippingItems {}", JSONObject.toJSONString(tradeDeliver.getShippingItems()));

        logger.info("getGiftItemList {}", JSONObject.toJSONString(tradeDeliver.getGiftItemList()));

        //处理商品
        handleShippingItems(trade, tradeDeliver.getShippingItems(), stringBuilder, operator, false);

        //处理赠品
        handleShippingItems(trade, tradeDeliver.getGiftItemList(), stringBuilder, operator, true);

        stringBuilder.trimToSize();
        tradeDeliver.setStatus(DeliverStatus.SHIPPED);
        trade.addTradeDeliver(tradeDeliver);
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventType(FlowState.DELIVERED.getDescription())
                .eventDetail(stringBuilder.toString())
                .eventTime(LocalDateTime.now())
                .build());

        // 更改发货状态
        boolean allShipped = isAllShipped(trade, tradeDeliver);
        if (Objects.nonNull(tradeDeliver.getDeliverAll())){
            trade.getTradeState().setDeliverAll(tradeDeliver.getDeliverAll());
        }

        trade.getTradeState().setDeliverTime(LocalDateTime.now());
        trade.getTradeState().setDeliverStatus(DeliverStatus.SHIPPED);
        trade.getTradeState().setFlowState(FlowState.DELIVERED);
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventType(FlowState.DELIVERED.getDescription())
                .eventDetail(String.format("订单[%s],已全部发货,发货人:%s", trade.getId(), operator.getName()))
                .eventTime(LocalDateTime.now())
                .build());
        if (DeliverWay.PICK_SELF.equals(trade.getDeliverWay())){
            trade.getTradeState().setFlowState(FlowState.TOPICKUP);
            trade.getTradeState().setPayState(PayState.PAID);
            String detail = String.format("订单[%s]已付款,待自提,操作人:%s", trade.getId(), operator.getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventTime(LocalDateTime.now())
                    .eventType("订单待自提")
                    .eventDetail(detail)
                    .build());
            //发送短信提心会员拿自提码取货
            sendPickUpMessage(trade);
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("订单[%s] => 发货状态[ %s ], 流程状态[ %s ]", trade.getId(), trade.getTradeState().getDeliverStatus(), trade.getTradeState().getFlowState()));
        }
        TradeItem tradeItem = trade.getTradeItems().stream().findAny().orElse(null);
        if(Objects.isNull(tradeItem.getDevanningId())){
            if (Objects.isNull(trade.getActivityType()) || BigDecimal.ZERO.compareTo(trade.getTradePrice().getTotalPrice()) == 0){
                trade.setActivityType("1");
            }else if (Objects.isNull(trade.getActivityType()) || (trade.getTradePrice().getTotalPrice().compareTo(BigDecimal.ZERO) == 1
                    && trade.getTradePrice().getTotalPrice().compareTo(trade.getTradePrice().getDeliveryPrice()) == 0)){
                trade.setActivityType("1");
            }
        }
        logger.info(" ================== 3. save tradeInfo :{}",trade);
        save(trade);
        if (allShipped) {
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(0).getEventDetail());
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(1).getEventDetail());
        } else {
            super.operationLogMq.convertAndSend(operator, FlowState.DELIVERED.getDescription(), trade.getTradeEventLogs().get(0).getEventDetail());
        }
    }

    private void sendPickUpMessage(Trade trade) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        messageMQRequest.setNodeCode(OrderProcessType.CUSTOMER_PICK_UP_CODE.getType());
        map.put("node", OrderProcessType.CUSTOMER_PICK_UP_CODE.toValue());
        List<String> param=new ArrayList<>(2);
        param.add(trade.getId());
        param.add(trade.getTradeWareHouse().getPickUpCode());
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(param);
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getConsignee().getPhone());
        pickUpCodeMq.sendPickUpCode(messageMQRequest);
    }


    /*private void handleShippingItems(Trade trade, List<ShippingItem> shippingItems, StringBuilder stringBuilder, Operator operator, boolean isGift){
        ConcurrentHashMap<String, List<TradeItem>> skuItemMap ;
        if(isGift){
            skuItemMap = trade.giftSkuItemMap();
        }else{
            skuItemMap = trade.skuItemMap();
        }

        shippingItems.forEach(shippingItem -> {
            TradeItem tradeItem = null;
            List<TradeItem> tradeItems = skuItemMap.get(shippingItem.getSkuId());
            for (TradeItem tradeItem1:tradeItems){
                if (Objects.nonNull(shippingItem.getDevanningId())){
                    if (Objects.nonNull(tradeItem1.getDevanningId()) && Objects.equals(tradeItem1.getDevanningId(), shippingItem.getDevanningId())){
                        tradeItem = tradeItem1;
                        break;
                    }
                }else {
                    tradeItem = tradeItem1;
                    break;
                }
            }

            //1. 增加发货数量
            Long hasNum = tradeItem.getDeliveredNum();
            if (hasNum != null) {
                hasNum += shippingItem.getItemNum();
            } else {
                hasNum = shippingItem.getItemNum();
            }
            tradeItem.setDeliveredNum(hasNum);


            //2. 更新发货状态
            if (hasNum.equals(tradeItem.getNum())) {
                tradeItem.setDeliverStatus(DeliverStatus.SHIPPED);
            } else if (hasNum > tradeItem.getNum()) {  //什么鬼, 发多了？
                throw new SbcRuntimeException("K-050109", new Object[]{tradeItem.getSkuId(), tradeItem.getNum(), hasNum});
            } else {
                tradeItem.setDeliverStatus(DeliverStatus.PART_SHIPPED);
            }
            stringBuilder.append(String.format("订单%s,商品[%s], 发货数：%s, 目前状态:[%s],发货人:%s\r\n",
                    trade.getId(),
                    (isGift ? "【赠品】" : "") + tradeItem.getSkuName(),
                    shippingItem.getItemNum().toString(),
                    tradeItem.getDeliverStatus().getDescription(),
                    operator.getName()));

            //3. 合并数据
            shippingItem.setItemName(tradeItem.getSkuName());
            shippingItem.setSpuId(tradeItem.getSpuId());
            shippingItem.setPic(tradeItem.getPic());
            shippingItem.setSpecDetails(tradeItem.getSpecDetails());
            shippingItem.setUnit(tradeItem.getUnit());
        });
    }*/

    private void handleShippingItems(Trade trade, List<ShippingItem> shippingItems, StringBuilder stringBuilder, Operator operator, boolean isGift){
        ConcurrentHashMap<String, TradeItem> skuItemMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, TradeItem> devanningSkuItemMap = new ConcurrentHashMap<>();
        if(isGift){
            skuItemMap = trade.giftSkuItemMap2();
        }else{
            /*TradeItem firstTradeItem = trade.getTradeItems().stream().findFirst().orElse(null);
            if(Objects.nonNull(firstTradeItem.getDevanningId())){
                //拆箱商品
                devanningSkuItemMap = trade.devanningSkuItemMap();
            }else {
                //正常商品
                skuItemMap = trade.skuItemMap2();
            }*/

            skuItemMap = trade.skuItemMap2();
        }

        log.info("handleShippingItems.skuItemMap:{}", skuItemMap);

        ConcurrentHashMap<String, TradeItem> finalSkuItemMap = skuItemMap;
        ConcurrentHashMap<Long, TradeItem> finalDevanningSkuItemMap = devanningSkuItemMap;
        shippingItems.forEach(shippingItem -> {
            /*TradeItem tradeItem;
            if(Objects.nonNull(shippingItem.getDevanningId())){
                tradeItem = finalDevanningSkuItemMap.get(shippingItem.getDevanningId());
                //赠品也存在拆箱主键
                if(Objects.isNull(tradeItem)){
                    tradeItem = finalSkuItemMap.get(shippingItem.getSkuId());
                }
            }else{
                tradeItem = finalSkuItemMap.get(shippingItem.getSkuId());
            }*/

            TradeItem tradeItem = finalSkuItemMap.get(shippingItem.getSkuId());

            if(Objects.nonNull(tradeItem)){
                //1. 增加发货数量
                Long hasNum = tradeItem.getDeliveredNum();
                if (hasNum != null && hasNum > 0) {
                    hasNum += shippingItem.getItemNum();
                } else {
                    hasNum = shippingItem.getItemNum();
                }
                tradeItem.setDeliveredNum(hasNum);


                //2. 更新发货状态
                if (hasNum.equals(tradeItem.getNum())) {
                    tradeItem.setDeliverStatus(DeliverStatus.SHIPPED);
                } else if (hasNum > tradeItem.getNum()) {  //什么鬼, 发多了？
                    throw new SbcRuntimeException("K-050109", new Object[]{tradeItem.getSkuId(), tradeItem.getNum(), hasNum});
                } else {
                    tradeItem.setDeliverStatus(DeliverStatus.PART_SHIPPED);
                }
                stringBuilder.append(String.format("订单%s,商品[%s], 发货数：%s, 目前状态:[%s],发货人:%s\r\n",
                        trade.getId(),
                        (isGift ? "【赠品】" : "") + tradeItem.getSkuName(),
                        shippingItem.getItemNum().toString(),
                        tradeItem.getDeliverStatus().getDescription(),
                        operator.getName()));

                //3. 合并数据
                shippingItem.setItemName(tradeItem.getSkuName());
                shippingItem.setSpuId(tradeItem.getSpuId());
                shippingItem.setPic(tradeItem.getPic());
                shippingItem.setSpecDetails(tradeItem.getSpecDetails());
                shippingItem.setUnit(tradeItem.getUnit());
            }
        });

        log.info("handleShippingItems.trade{}",trade);
    }
}
