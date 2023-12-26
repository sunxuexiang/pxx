package com.wanmi.sbc.order.ares.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.constants.MQConstant;
import com.wanmi.ares.enums.DataSourceType;
import com.wanmi.ares.request.mq.OrderDataRequest;
import com.wanmi.ares.request.mq.TradeItemRequest;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import com.wanmi.sbc.order.payorder.repository.PayOrderRepository;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单交易相关数据埋点 - 统计系统
 * Author: bail
 * Time: 2017/10/18.15:47
 */
@Service
@Slf4j
@EnableBinding
public class OrderAresService {

    @Autowired
    private BinderAwareChannelResolver resolver;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    /**
     * 初始化订单
     */
    public void initOrderES() {
        tradeRepository.findAll().stream().forEach(trade -> dispatchFunction("addOrder", trade));
    }

    /**
     * 初始化付款单
     */
    public void initPayOrderES() {
        dispatchFunction("payOrderInit",
                payOrderRepository.findAll().stream().filter(payOrder -> PayOrderStatus.PAYED.equals(payOrder.getPayOrderStatus())).collect(Collectors.toList()));//只初始化已支付的
    }

    /**
     * 初始化退单
     */
    public void initReturnOrderES() {
        returnOrderRepository.findAll().stream().filter(returnOrder -> ReturnFlowState.COMPLETED.equals(returnOrder.getReturnFlowState())).forEach(returnOrder -> dispatchFunction("returnOrderInit", returnOrder));//只初始化退单完成的
    }

    /**
     * 埋点处理的分发方法
     *
     * @param funcType 类别,依据此进行分发
     * @param objs     多个入参对象
     */
    @Async
    public void dispatchFunction(String funcType, Object... objs) {
    }

    /**
     * 创建订单(现只用来初始化数据使用)
     *
     * @param objs [订单]
     */
    public void addOrder(Object... objs) {
        Trade trade = (Trade) objs[0];
        OrderDataRequest orderDataRequest = getOrderReq(trade);
        resolver.resolveDestination(MQConstant.Q_ARES_ORDER_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(orderDataRequest)));
    }

    /**
     * 创建订单(用户下单/带客下单,拆单,多笔订单方式)
     *
     * @param objs [订单们]
     */
    public void addOrderList(Object... objs) {
        List<Trade> tradeList = (List<Trade>) objs[0];
        List<OrderDataRequest> collect =
                tradeList.stream().map(trade -> getOrderReq(trade)).collect(Collectors.toList());
        resolver.resolveDestination(MQConstant.Q_ARES_ORDER_LIST_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(collect)));
    }

    /**
     * 获取订单的推送数据
     *
     * @param trade 订单
     * @return 支付单的推送数据
     */
    private OrderDataRequest getOrderReq(Trade trade) {
        OrderDataRequest orderDataRequest = new OrderDataRequest();
        orderDataRequest.setId(trade.getId());
        orderDataRequest.setType(DataSourceType.CREATE);
        orderDataRequest.setOrderAmt(trade.getTradePrice().getTotalPrice());
        orderDataRequest.setRealAmt(null);//无支付金额
        orderDataRequest.setCustomerId(trade.getBuyer().getId());
        orderDataRequest.setItemRequestList(getSkuRequestList(trade.getTradeItems()));
        orderDataRequest.setCompanyId(trade.getSupplier().getSupplierId().toString());
        orderDataRequest.setOperationDate(trade.getTradeState().getCreateTime().toLocalDate());//创建订单时间
        orderDataRequest.setSendTime(LocalDateTime.now());
        return orderDataRequest;
    }

    /**
     * 订单支付-线上支付成功/boss后台添加线下收款单
     *
     * @param objs [订单,付款单]
     */
    public void payOrder(Object... objs) {
        PayOrder payOrder = (PayOrder) objs[0];
        LocalDateTime time = (LocalDateTime) objs[1];
        PayOrder clonedPayOrder = new PayOrder();
        BeanUtils.copyProperties(payOrder, clonedPayOrder);
        clonedPayOrder.setUpdateTime(time);
        Trade trade = tradeRepository.findById(clonedPayOrder.getOrderCode()).orElse(null);//根据订单号查询订单信息
        if (Objects.isNull(trade)){
            log.error("订单ID:{},查询不到订单信息",clonedPayOrder.getOrderCode());
            return;
        }
        long count =
                trade.getTradeEventLogs().stream().filter(tradeEventLog -> TradeEvent.OBSOLETE_PAY.getDescription().equals(tradeEventLog.getEventType())).count();
        if (count <= 0) {//没有过作废支付记录的,才发送给统计系统
            resolver.resolveDestination(MQConstant.Q_ARES_ORDER_PAY).send(new GenericMessage<>(JSONObject.toJSONString(getPayOrderReq(clonedPayOrder, trade))));
        }
    }

    /**
     * 订单支付-线下支付,商家确认(审核)
     *
     * @param objs [付款单List]
     */
    public void offlinePayOrder(Object... objs) {
        List<PayOrder> payOrdeList = (List<PayOrder>) objs[0];
        LocalDateTime time = (LocalDateTime) objs[1];

        payOrdeList.stream().forEach(payOrder -> {
            try {
                PayOrder clonedPayOrder = new PayOrder();
                BeanUtils.copyProperties(payOrder, clonedPayOrder);
                clonedPayOrder.setUpdateTime(time);
                Trade trade = tradeRepository.findById(clonedPayOrder.getOrderCode()).orElse(null);//根据订单号查询订单信息
                if (Objects.isNull(trade)){
                    log.error("订单ID:{},查询不到订单信息",clonedPayOrder.getOrderCode());
                    return;
                }
                long count =
                        trade.getTradeEventLogs().stream().filter(tradeEventLog -> TradeEvent.OBSOLETE_PAY.getDescription().equals(tradeEventLog.getEventType())).count();
                if (count <= 0) {//没有过作废支付记录的,才发送给统计系统
                    resolver.resolveDestination(MQConstant.Q_ARES_ORDER_PAY).send(new GenericMessage<>(JSONObject.toJSONString(getPayOrderReq(clonedPayOrder,trade))));
                }
            } catch (Exception e) {
                log.error("支付单埋点数据错误:tid=" + payOrder.getOrderCode(), e);
            }
        });
    }

    /**
     * ES初始化支付单信息(不需要判断是否有作废记录)
     *
     * @param objs [付款单List]
     */
    public void payOrderInit(Object... objs) {
        List<PayOrder> payOrdeList = (List<PayOrder>) objs[0];
        payOrdeList.stream().forEach(payOrder -> {
            try {
                Trade trade = tradeRepository.findById(payOrder.getOrderCode()).orElse(null);//根据订单号查询订单信息
                if (Objects.isNull(trade)){
                    log.error("订单ID:{},查询不到订单信息",payOrder.getOrderCode());
                    return;
                }
                resolver.resolveDestination(MQConstant.Q_ARES_ORDER_PAY).send(new GenericMessage<>(JSONObject.toJSONString(getPayOrderReq(payOrder,trade))));
            } catch (Exception e) {
                log.error("支付单初始化埋点数据错误:tid=" + payOrder.getOrderCode(), e);
            }
        });
    }

    /**
     * 获取支付单的推送数据
     *
     * @param payOrder 支付单
     * @param trade    订单
     * @return 支付单的推送数据
     */
    private OrderDataRequest getPayOrderReq(PayOrder payOrder, Trade trade) {
        OrderDataRequest orderDataRequest = new OrderDataRequest();
        orderDataRequest.setId(payOrder.getPayOrderId());
        orderDataRequest.setType(DataSourceType.PAY);
        orderDataRequest.setOrderAmt(trade.getTradePrice().getTotalPrice());
        orderDataRequest.setRealAmt(payOrder.getPayOrderPrice());
        orderDataRequest.setCustomerId(trade.getBuyer().getId());
        orderDataRequest.setItemRequestList(getSkuRequestList(trade.getTradeItems()));
        orderDataRequest.setCompanyId(trade.getSupplier().getSupplierId().toString());
        orderDataRequest.setOperationDate(payOrder.getUpdateTime().toLocalDate());//商家确认支付时间
        orderDataRequest.setSendTime(LocalDateTime.now());
        return orderDataRequest;
    }

    /**
     * 埋点退单(以当前时间为准)
     *
     * @param objs [退单]
     */
    public void returnOrder(Object... objs) {
        ReturnOrder returnOrder = (ReturnOrder) objs[0];
        returnOrder.setCreateTime(LocalDateTime.now());//设置当前时间为退单完成时间
//        resolver.resolveDestination(MQConstant.Q_ARES_ORDER_RETURN).send(new GenericMessage<>(JSONObject.toJSONString(getReturnOrderReq(returnOrder))));
    }

    /**
     * ES初始化退单信息(以退单创建时间为准)
     *
     * @param objs [退单]
     */
    public void returnOrderInit(Object... objs) {
        ReturnOrder returnOrder = (ReturnOrder) objs[0];
        resolver.resolveDestination(MQConstant.Q_ARES_ORDER_RETURN).send(new GenericMessage<>(JSONObject.toJSONString(getReturnOrderReq(returnOrder))));
    }

    /**
     * 获取退单信息请求对象
     *
     * @param returnOrder 退单业务数据
     * @return 退单信息请求对象
     */
    private OrderDataRequest getReturnOrderReq(ReturnOrder returnOrder) {
        OrderDataRequest orderDataRequest = new OrderDataRequest();
        orderDataRequest.setId(returnOrder.getId());
        orderDataRequest.setType(DataSourceType.RETURN);
        orderDataRequest.setOrderAmt(returnOrder.getReturnPrice().getTotalPrice());
        orderDataRequest.setRealAmt(returnOrder.getReturnPrice().getActualReturnPrice() == null ?
                returnOrder.getReturnPrice().getTotalPrice() : returnOrder.getReturnPrice().getActualReturnPrice());
        orderDataRequest.setCustomerId(returnOrder.getBuyer().getId());
        orderDataRequest.setItemRequestList(getSkuRequestListForReturn(orderDataRequest.getRealAmt(),
                returnOrder.getReturnItems()));
        orderDataRequest.setCompanyId(returnOrder.getCompany().getCompanyInfoId().toString());
        orderDataRequest.setOperationDate(returnOrder.getCreateTime().toLocalDate());//退单时间
        orderDataRequest.setSendTime(LocalDateTime.now());
        return orderDataRequest;
    }

    /**
     * 由交易项 映射成 商品信息Request
     *
     * @param itemList 交易项
     * @return 商品信息RequestList
     */
    private List<TradeItemRequest> getSkuRequestList(List<TradeItem> itemList) {
        return itemList.stream().map(item -> {
            TradeItemRequest tradeItem = new TradeItemRequest();
            tradeItem.setId(item.getSkuId());
            tradeItem.setPrice(item.getLevelPrice().multiply(new BigDecimal(item.getNum())));//商品的小计价格
            tradeItem.setNum(item.getNum());
            return tradeItem;
        }).collect(Collectors.toList());
    }

    /**
     * 由退货项 映射成 商品信息Request
     *
     * @param itemList 退货项
     * @return 商品信息RequestList
     */
    private List<TradeItemRequest> getSkuRequestListForReturn(BigDecimal reaclAmt, List<ReturnItem> itemList) {
        BigDecimal sumPrices =
                itemList.stream().map(item -> item.getPrice().multiply(item.getNum())).reduce(BigDecimal.ZERO, (sum, single) -> sum.add(single));//所有商品的价格

        Function<ReturnItem, BigDecimal> calculate;//根据不同情况选择不同的函数计算商品价格
        if (reaclAmt.compareTo(sumPrices) == 0) {
            //实际退款金额 == 所有退单商品总价时,商品价格为小计
            calculate = item -> item.getPrice().multiply(item.getNum());
        } else {
            //实际退款金额 != 所有退单商品总价时,商品价格为分摊价格
            calculate =
                    item -> item.getPrice().multiply(item.getNum()).multiply(reaclAmt).divide(sumPrices, 2, BigDecimal.ROUND_HALF_UP);
        }

        ReturnItem item;
        TradeItemRequest tradeItem;
        List<TradeItemRequest> reqList = new ArrayList<>();
        BigDecimal singleTemp;
        BigDecimal beforeSum = BigDecimal.ZERO;//除最后一个的汇总价格
        for (int i = 0; i < itemList.size(); i++) {
            item = itemList.get(i);
            tradeItem = new TradeItemRequest();
            tradeItem.setId(item.getSkuId());
            tradeItem.setNum(item.getNum().longValue());
            if (i == itemList.size() - 1) {
                tradeItem.setPrice(reaclAmt.subtract(beforeSum));//用总额 减去 除最后一个的总额(防止误差0.01元)
            } else {
                singleTemp = calculate.apply(item);
                beforeSum = beforeSum.add(singleTemp);
                tradeItem.setPrice(singleTemp);
            }
            reqList.add(tradeItem);
        }
        return reqList;
    }

}
