package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.ChannelItemByIdRequest;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradePushKingdeeOrderRequest;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.common.KingdeePushOrder;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.payorder.service.PayOrderService;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.model.root.*;
import com.wanmi.sbc.returnorder.returnorder.repository.TradePushKingdeeRefundRepository;
import com.wanmi.sbc.returnorder.returnorder.repository.TradePushKingdeeReturnGoodsRepository;
import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnPileOrderService;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecordAttachment;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.*;
import com.wanmi.sbc.returnorder.trade.repository.*;
import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.wms.api.provider.erp.PushOrderKingdeeProvider;
import com.wanmi.sbc.wms.api.request.erp.DescriptionFailedQueryStockPushRequest;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushResponse;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushReturnGoodsResponse;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushReturnGoodsVO;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 补偿推送金蝶失败单子
 *
 * @author yitang
 * @version 1.0
 */
@Service
@Slf4j
public class KingdeeAbnormalSalesOrderService {

    @Autowired
    private TradePushKingdeeOrderRepository tradePushKingdeeOrderRepository;

    @Autowired
    private TradePushKingdeePayRepository tradePushKingdeePayRepository;

    @Autowired
    private TradePushKingdeeRefundRepository tradePushKingdeeRefundRepository;

    @Autowired
    private TradePushKingdeeReturnGoodsRepository tradePushKingdeeReturnGoodsRepository;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private KingdeePushOrder kingdeePushOrder;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private ReturnOrderService returnOrderService;

    /**
     * 注入消费记录生产者service
     */
    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private TradeCachePushKingdeeOrderRepository tradeCachePushKingdeeOrderRepository;

    @Autowired
    private PushOrderKingdeeProvider pushOrderKingdeeProvider;

    @Autowired
    private PileTradeService pileTradeService;

    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    @Autowired
    private PileStockRecordAttachmentRepostory pileStockRecordAttachmentRepostory;

    @Autowired
    private CachePushKingdeeOrderExceptionRepository cachePushKingdeeOrderExceptionRepository;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;


    public void compensationKingdeeAbnormalSalesOrder(TradePushKingdeeOrderRequest request){
        log.info("KingdeeAbnormalSalesOrderService.compensationKingdeeAbnormalSalesOrder req orderType:{} dateTime:{}",request.getOrderType(),
                request.getDateTime());
        if (!Objects.nonNull(request)){
            return;
        }

//        LocalDateTime byTime = LocalDateTime.now().plusDays(request.getDay());
        LocalDateTime byTime = request.getDateTime();
        LocalDateTime endTime = request.getEndTime();
        switch (request.getOrderType().toTypeInt()){
            //销售订单
            case 0:
                salesOrder(byTime);
                break;
            //支付单
            case 1:
//                payOrder(byTime);
                payCompensationOrder(request.getOrderIds());
                break;
            //退货
            case 2:
                salesReturnOrder(request.getOrderIds());
                break;
            //退款单
            case 3:
                compensationRefundOrder(request.getOrderIds());
                break;
            case 4:
                salesOrderId(request.getOrderIds());
                break;
            //洗囤货
            case 5:
                historicalDataStorageDataPushKingdee(byTime,endTime);
                break;
            //洗囤货退货
            case 6:
                historicalDataStorageDataPushReturnGoodsKingdee(byTime,endTime);
                break;
            //重推囤货
            case 7:
                pushStockOrder(request.getOrderIds());
                break;
            //重推囤货退货
            case 8:
                pushStockReturns(request.getOrderIds());
                break;
            //洗囤货(只处理退货情况)
            case 9:
                historicalDataStorageDataReturnGoodsPushKingdee(byTime,endTime);
                break;
            //重推新囤货订单
            case 10:
                newPilePushStockOrder(request.getOrderIds());
                break;
            //重推新囤货收款单
            case 11:
                newPilePayCompensationOrder(request.getOrderIds());
                break;
            //重推新囤货退款单
            case 12:
                newPiLeCompensationRefundOrder(request.getOrderIds());
                break;
            //重推佣金收款单
            case 13:
                commissionPayed(request.getOrderIds());
                break;
            //重推佣金退款单
            case 14:
                commissionRefund(request.getOrderIds());
                break;
            //重推囤货佣金退款单
            case 15:
                newPileCommissionRefund(request.getOrderIds());
                break;
            default:
                break;
        }
    }

    private void newPileCommissionRefund(String rid) {
        NewPileReturnOrder returnOrder = newPileReturnOrderService.findById(rid);
        if (Objects.nonNull(returnOrder)) {
            String tid = returnOrder.getTid();
            NewPileTrade trade = newPileTradeService.detail(tid);
            if (Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                String returnOrderId = returnOrder.getId();
                String tradeId = trade.getId();
                String sendStr = tradeId + "#" + returnOrderId;
                log.info("佣金部分推送ERP退款单：{}", sendStr);
                orderProducerService.pushRefundCommisionToKingdee(sendStr, 10 * 1000L);
            }
        }else {
            log.error("补推佣金退款单到金蝶失败：退单不存在：{}", rid);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退单不存在");
        }
    }

    private void commissionRefund(String rid) {
        ReturnOrder returnOrder = returnOrderService.findById(rid);
        if (Objects.nonNull(returnOrder)) {
            String tid = returnOrder.getTid();
            Trade trade = tradeService.detail(tid);
            if (Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                String returnOrderId = returnOrder.getId();
                String tradeId = trade.getId();
                String sendStr = tradeId + "#" + returnOrderId;
                log.info("佣金部分推送ERP退款单：{}", sendStr);
                orderProducerService.pushRefundCommisionToKingdee(sendStr, 10 * 1000L);
            }
        }else {
            log.error("补推佣金退款单到金蝶失败：退单不存在：{}", rid);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退单不存在");
        }
    }

    private void commissionPayed(String orderIds) {
        if (StringUtils.isNotBlank(orderIds)) {
            String[] orderIdList = orderIds.split(",");
            List<String> orderList = Arrays.asList(orderIdList);
            List<Trade> trades = tradeService.details(orderList);
            if (CollectionUtils.isEmpty(trades)) {
                log.error("补推佣金收款单到金蝶失败：订单不存在：{}", orderIds);
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单不存在");
            }
            for (Trade trade : trades) {
                if (Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                    String tradeId = trade.getId();
                    orderProducerService.pushCommisionToKingdee(tradeId, 5 * 1000L);
                }
            }
        }
    }

    /**
     * 销售订单补偿
     * @param byTime
     */
    private void salesOrder(LocalDateTime byTime){
        log.info("KingdeeAbnormalSalesOrderService.salesOrder byTime:{}",byTime);
        new Thread(){
            @Override
            public void run(){
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrder start:{}",start);
                Long pushKingdeeId = 0L;
                LocalDateTime byTimes = byTime;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime time = LocalDateTime.now();
                String beginTime = df.format(byTimes);
                String endTime = df.format(time);
//                do {
//                    log.info("KingdeeAbnormalSalesOrderService.salesOrder pushKingdeeId:{} byTimes:{}",pushKingdeeId,byTimes);
//                    List<TradePushKingdeeOrder> kingdeeOrderList = tradePushKingdeeOrderRepository.selcetPushKingdeeOrder(pushKingdeeId,byTimes);
//                    log.info("KingdeeAbnormalSalesOrderService.salesOrder kingdeeOrderList:{}",kingdeeOrderList.size());
//                    if (kingdeeOrderList.size() > 0){
//                        //查询销售订单
//                        List<String> tradeId = kingdeeOrderList.stream().map(TradePushKingdeeOrder::getOrderCode).collect(Collectors.toList());
//                        List<Trade> trades = tradeService.details(tradeId);
//                        TradeState tradeState = TradeState.builder()
//                                                          .payState(PayState.PAID)
//                                                          .auditState(AuditState.CHECKED)
//                                                          .flowState(FlowState.AUDIT)
//                                                          .build();
//                        TradeQueryRequest queryRequest = new TradeQueryRequest();
//                        queryRequest.setBeginTime(beginTime);
//                        queryRequest.setEndTime(endTime);
//                        queryRequest.setTradeState(tradeState);
//                        queryRequest.setPageNum(pageNum);
//                        queryRequest.setPageSize(10);
//                        Criteria criteria = queryRequest.getWhereCriteria();
//                        tradeService.page(criteria,queryRequest);
//                        trades.stream().forEach(trade -> {
//                            if (Objects.nonNull(trade)) {
//                                log.info("KingdeeAbnormalSalesOrderService.salesOrder trade.tid:{}",trade.getId());
//                                kingdeePushOrder.pushSalesOrderkingdee(trade);
//                            }
//                        });
//                        queryMarked = true;
//                        TradePushKingdeeOrder result = kingdeeOrderList.stream().reduce((first, second) -> second).orElse(new TradePushKingdeeOrder());
//                        pushKingdeeId=result.getPushKingdeeId();
//                    }else {
//                        queryMarked = false;
//                    }
//                }while (queryMarked);
                Integer pageNum = 0;
                do {
                    TradeState tradeState = TradeState.builder()
                            .payState(PayState.PAID)
                            .auditState(AuditState.CHECKED)
                            .flowState(FlowState.AUDIT)
                            .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                            .build();
                    TradeQueryRequest queryRequest = new TradeQueryRequest();
                    queryRequest.setBeginTime(beginTime);
                    queryRequest.setEndTime(endTime);
                    queryRequest.setTradeState(tradeState);
                    queryRequest.setPageNum(pageNum);
                    queryRequest.setPageSize(10);
                    Criteria criteria = queryRequest.getWhereCriteria();
                    List<Trade> trades = tradeService.page(criteria,queryRequest).getContent();
                    trades.stream().forEach(trade -> {
                        if (Objects.nonNull(trade)) {
                            log.info("KingdeeAbnormalSalesOrderService.salesOrder trade.tid:{}",trade.getId());
                            kingdeePushOrder.asyncPushSalesOrderkingdee(trade.getId());
                            //延时队列
                            PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                            TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                            pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                            pushPayOrderGroupon.setOrderCode(trade.getId());
                            orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                        }
                    });
                    if (trades.size() > 0){
                        pageNum = pageNum + 1;
                    }else {
                        pageNum = 0;
                    }
                }while (pageNum>0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrder end:{}",end-start);
            }
        }.start();

    }

    /**
     * 根据订单推
     * @param orderIds
     */
    private void salesOrderId(String orderIds){
        if (StringUtils.isNotEmpty(orderIds)){
            String[] orderId =orderIds.split(",");
            if (orderId.length > 0){
                List<String> orderList = Arrays.asList(orderId);
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId orderList：{}",orderList);
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId start:{}",start);
                List<Trade> trades = tradeService.details(orderList);
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId :{}", JSONObject.toJSONString(trades));
                trades.stream().forEach(trade -> {
                    Integer num = tradePushKingdeeOrderRepository.queryPushKingdeeOrderPushStatus(trade.getId());
                    log.info("KingdeeAbnormalSalesOrderService.salesOrderId OrderId:{} num :{}",trade.getId(),num);
                    if (num == 0){
                        if (Objects.nonNull(trade)) {
                            log.info("KingdeeAbnormalSalesOrderService.salesOrderId trade.tid:{}",trade.getId());
                            //拼团在订单那提交的时候不推送，在成团时推送
                            if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                                try {
                                    List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordAttachment(trade.getId());
                                    if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                                        Long stockNum = trade.getTradeItems().stream().mapToLong(TradeItem::getNum).sum();
                                        Long deliveryNum = stockRecordAttachmentList.stream().mapToLong(PileStockRecordAttachment::getNum).sum();
                                        if (!stockNum.equals(deliveryNum)) {
                                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "提货数量" + stockNum + "件，不等于发货数量" + deliveryNum + "件，请联系客服处理！");
                                        }
                                        Trade pushWmsTrade = KsBeanUtil.convert(trade, Trade.class);
                                        //新订单商品items
                                        List<TradeItem> newTradeItems = new ArrayList<>();
                                        //需要删除的商品items
                                        List<TradeItem> removeTradeItems = new ArrayList<>();
                                        pushWmsTrade.getTradeItems().forEach(tradeItem -> {
                                            //过滤出此商品的提货明细记录
                                            List<PileStockRecordAttachment> filterList = stockRecordAttachmentList.stream().filter(s -> s.getSkuId().equals(tradeItem.getSkuId())).collect(Collectors.toList());
                                            //囤货明细记录大于1
                                            if (CollectionUtils.isNotEmpty(filterList) && filterList.size() > 1) {
                                                filterList.forEach(f -> {
                                                    //新订单商品item
                                                    TradeItem newTradeItem = KsBeanUtil.convert(tradeItem, TradeItem.class);
                                                    newTradeItem.setPrice(f.getPrice());
                                                    newTradeItem.setVipPrice(f.getPrice());
                                                    newTradeItem.setSplitPrice(f.getPrice().multiply(BigDecimal.valueOf(f.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
                                                    newTradeItem.setOriginalPrice(f.getPrice());
                                                    newTradeItem.setPileOrderCode(f.getOrderCode());
                                                    newTradeItem.setNum(f.getNum());
                                                    newTradeItems.add(newTradeItem);
                                                });
                                                removeTradeItems.add(tradeItem);
                                            } else {
                                                if (CollectionUtils.isNotEmpty(filterList)) {
                                                    PileStockRecordAttachment filter = filterList.get(0);
                                                    tradeItem.setPileOrderCode(filter.getOrderCode());
                                                } else {
                                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货明细副表记录异常，请联系客服！");
                                                }
                                            }
                                        });
                                        //删除原有订单商品明细记录
                                        if (CollectionUtils.isNotEmpty(removeTradeItems)) {
                                            pushWmsTrade.getTradeItems().removeAll(removeTradeItems);
                                        }
                                        //添加拆分后的订单商品明细记录
                                        if (CollectionUtils.isNotEmpty(newTradeItems)) {
                                            pushWmsTrade.getTradeItems().addAll(newTradeItems);
                                        }
                                        newPileTradeService.pushWMSOrder(pushWmsTrade, false, trade.getNewVilageFlag());
                                    }else {
                                        newPileTradeService.pushWMSOrder(trade, false, trade.getNewVilageFlag());
                                    }
                                } catch (Exception e) {
                                    log.info("=====订单推送报错日志：" + e + ";  订单编号:" + trade.getId());
                                }
                            }
                            if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
                                if (StringUtils.isEmpty(trade.getLogistics()) || trade.getLogistics().equals("01")) {
                                    log.info("KingdeeAbnormalSalesOrderService.salesOrderId logistics:{}", trade.getId());
                                    orderProducerService.kingdeePushOrder(trade.getId(), 10 * 1000L);
                                }
                            }else {
                                log.info("KingdeeAbnormalSalesOrderService.salesOrderId shoppingCart:{}", trade.getId());
                                orderProducerService.shoppingCartKingdeePushOrder(trade.getId(),10 * 1000L);
                            }
                            if (trade.getTradeState().getPayState().equals(PayState.PAID)
                                    && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                                    && trade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                                    && trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                                //延时队列
                                PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                                        TradeRecordByOrderCodeRequest(trade.getId())).getContext();
                                PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                                        ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                                        payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                                        payTradeRecordResponse.getChannelItemId())).getContext();

                                if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
                                    newPileTradeService.savePayOrder(trade, payOrder, PayWay.UNIONPAY);
                                } else {
                                    newPileTradeService.savePayOrder(trade, payOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                                }
                                //老逻辑，推送wms
                                orderProducerService.pushOrderPayWMS(trade.getId());
                                //推金蝶
                                TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                                pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                                pushPayOrderGroupon.setOrderCode(trade.getId());
                                orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                            }
                        }
                    }
                });
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId end:{}",end-start);
            }
        }

    }

    /**
     * 根据订单推
     * @param orderIds
     */
    private void pushOrderId(String orderIds){
        if (StringUtils.isNotEmpty(orderIds)){
            String[] orderId =orderIds.split(",");
            if (orderId.length > 0){
                List<String> orderList = Arrays.asList(orderId);
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId orderList：{}",orderList);
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId start:{}",start);
                List<Trade> trades = tradeService.details(orderList);
                trades.stream().forEach(trade -> {
                    Integer num = tradePushKingdeeOrderRepository.queryPushKingdeeOrderPushStatus(trade.getId());
                    if (num == 0){
                        if (Objects.nonNull(trade)) {
                            log.info("KingdeeAbnormalSalesOrderService.salesOrderId trade.tid:{}",trade.getId());
                            kingdeePushOrder.asyncPushSalesOrderkingdee(trade.getId());
                            //延时队列
                            PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                            PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                                    TradeRecordByOrderCodeRequest(trade.getId())).getContext();
                            PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                                    ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                                    payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                                    payTradeRecordResponse.getChannelItemId())).getContext();

                            if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
                                newPileTradeService.savePayOrder(trade,payOrder,PayWay.UNIONPAY);
                            }else {
                                newPileTradeService.savePayOrder(trade, payOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                            }
                            TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                            pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                            pushPayOrderGroupon.setOrderCode(trade.getId());
                            orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                        }
                    }
                });
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.salesOrderId end:{}",end-start);
            }
        }
    }

    /**
     * 退货单补偿
     */
    private void salesReturnOrder(String rid){
        if (StringUtils.isNotEmpty(rid)){
            ReturnOrder returnOrder = returnOrderService.findById(rid);
            log.info("KingdeeAbnormalSalesOrderService.salesReturnOrder returnGoodsCode:{}",returnOrder.getId());
            returnOrderService.pushAuditKingdee(returnOrder);
        }
    }

    private void payCompensationOrder(String orderIds){
        if (StringUtils.isNotEmpty(orderIds)) {
            String[] orderId = orderIds.split(",");
            if (orderId.length > 0) {
                List<String> orderList = Arrays.asList(orderId);
                log.info("KingdeeAbnormalSalesOrderService.payCompensationOrder orderList：{}", orderList);
                long start, end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.payCompensationOrder start:{}", start);
                List<Trade> trades = tradeService.details(orderList);
                trades.stream().forEach(trade -> {
                    //延时队列
                    PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                    if (payOrder.getPayOrderStatus().equals(PayOrderStatus.PAYED)) {
                        Integer num = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumberPushStatus(payOrder.getPayOrderId());
                        if (num == 0) {
                            log.info("KingdeeAbnormalSalesOrderService.payCompensationOrder payOrderId:{}",trade.getId());
                            TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                            pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                            pushPayOrderGroupon.setOrderCode(trade.getId());
                            orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                        }
                    }
                });
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.payCompensationOrder end:{}",end-start);
            }
        }
    }

    /**
     * 补偿支付单
     * @param byTime
     */
    private void payOrder(LocalDateTime byTime){
        new Thread(){
            @Override
            public void run(){
                Long pushKingdeePayId = 0L;
                Boolean queryMarked = false;
                LocalDateTime byTimes = byTime;
                do {
                    log.info("KingdeeAbnormalSalesOrderService.payOrder pushKingdeePayId:{} byTimes:{}",pushKingdeePayId,byTimes);
                    List<TradePushKingdeePayOrder> payOrders = tradePushKingdeePayRepository.selectPushKingdeePayOrderList(pushKingdeePayId,byTimes);
                    log.info("KingdeeAbnormalSalesOrderService.payOrder payOrders:{}",payOrders.size());
                    if (payOrders.size() > 0){
                        //查询销售订单
                        List<String> tradeId = payOrders.stream().map(TradePushKingdeePayOrder::getOrderCode).collect(Collectors.toList());
                        List<Trade> trades = tradeService.details(tradeId);
//                        //获取支付单
//                        List<String> payId = payOrders.stream().map(TradePushKingdeePayOrder::getPayCode).collect(Collectors.toList());
//                        List<PayOrder> payIds = payOrderService.findPayOrderByPayOrderIds(payId);
                        trades.stream().forEach(trade -> {
                            if (Objects.nonNull(trade)){
                                payOrders.stream().forEach(payOrder ->{
                                    if (trade.getPayOrderId().equals(payOrder.getPayCode())){
                                        PayOrderResponse payOrderResponse = new PayOrderResponse();
                                        Optional<PayOrder> payOrderById = payOrderService.findPayOrderByOrderCode(payOrder.getOrderCode());
                                        if (Objects.nonNull(payOrderById.get())) {
                                            log.info("KingdeeAbnormalSalesOrderService.payOrder payOrderId:{}",payOrderById.get().getPayOrderId());
                                            payOrderResponse.setPayOrderId(payOrderById.get().getPayOrderId());
                                            payOrderResponse.setTotalPrice(payOrderById.get().getPayOrderPrice());
                                            tradeService.pushPayOrderKingdee(trade, payOrderResponse, PayWay.valueOf
                                                    (payOrder.getPayType().toUpperCase()));
                                        }
                                    }
                                });

                            }
                        });
                        queryMarked = true;
                        TradePushKingdeePayOrder result = payOrders.stream().reduce((first, second) -> second).orElse(new TradePushKingdeePayOrder());
                        pushKingdeePayId = result.getPushKingdeePayId().longValue();
                    }else {
                        queryMarked = false;
                    }
                }while (queryMarked);
            }
        }.start();
    }


    /**
     * 补偿退款单
     * @param byTime
     */
    private void refundOrder(LocalDateTime byTime){
        new Thread(){
            @Override
            public void run(){
                Long pushKingdeeRefundId = 0L;
                Boolean queryMarked = false;
                LocalDateTime byTimes = byTime;
                do {
                    log.info("KingdeeAbnormalSalesOrderService.refundOrder pushKingdeeRefundId:{} byTimes:{}",pushKingdeeRefundId,byTimes);
                    List<TradePushKingdeeRefund> refundList = tradePushKingdeeRefundRepository.selectPushKingdeeRefundOrderList(pushKingdeeRefundId, byTimes);
                    log.info("KingdeeAbnormalSalesOrderService.refundOrder refundList:{}",refundList.size());
                    if (refundList.size() > 0){
                        List<String> ridList = refundList.stream().map(TradePushKingdeeRefund::getRefundCode).collect(Collectors.toList());
                        // 查询退款单
                        RefundOrderRequest refundOrderRequest = new RefundOrderRequest();
                        refundOrderRequest.setReturnOrderCodes(ridList);
                        List<RefundOrder> refundOrders = refundOrderService.findAll(refundOrderRequest);
                        if (refundOrders.size() > 0) {
                            //推送记录表
                            refundList.stream().forEach(refund -> {
                                //退款表
                                refundOrders.stream().forEach(refundOrder -> {
                                    if (Objects.nonNull(refund) && Objects.nonNull(refundOrder) && refund.getRefundCode().equals(refundOrder.getRefundCode())){
                                        log.info("KingdeeAbnormalSalesOrderService.refundOrder refundCode:{}",refundOrder.getRefundCode());
                                        Buyer buyer = new Buyer();
                                        buyer.setAccount(refund.getCustomerAccount());
                                        ReturnOrder returnOrder = new ReturnOrder();
                                        returnOrder.setBuyer(buyer);
                                        returnOrderService.pushRefundOrderKingdee(returnOrder,refundOrder,PayWay.valueOf(refund.getPayType().toUpperCase()));
                                    }
                                });
                            });
                            queryMarked = true;
                            TradePushKingdeeRefund tradePushKingdeeRefund = refundList.stream().reduce((first, second) -> second).orElse(new TradePushKingdeeRefund());
                            pushKingdeeRefundId = tradePushKingdeeRefund.getPushKingdeeRefundId();
                        }else {
                            queryMarked = false;
                        }
                    }else {
                        queryMarked = false;
                    }
                }while (queryMarked);
            }
        }.start();
    }

    /**
     * 补偿退款数据
     * @param rid
     */
    private void compensationRefundOrder(String rid){
        if (StringUtils.isNotEmpty(rid)){
            TradePushKingdeeRefund pushKingdeeRefund = tradePushKingdeeRefundRepository.selectPushKingdeeRefundOrder(rid);
            RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
            if (Objects.nonNull(refundOrder) && Objects.isNull(pushKingdeeRefund)) {
                log.info("KingdeeAbnormalSalesOrderService.compensationRefundOrder rid:{}",rid);
                ReturnOrder returnOrder = returnOrderService.findById(rid);
                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
                if (Objects.isNull(payTradeRecordResponse)) {
                    Trade trade = tradeService.getById(returnOrder.getId());
                    if (Objects.isNull(trade)) {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "关联订单不存在");
                    }
                    payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(trade.getParentId())).getContext();
                }
                PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) ||
                        Objects.isNull(payTradeRecordResponse.getChannelItemId()) ?
                        Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                        payTradeRecordResponse.getChannelItemId())).getContext();
                if (StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                    returnOrderService.pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                }else {
                    returnOrderService.pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                }
            }
        }
    }

    /**
     * 补偿退货
     * @param byTime
     */
    private void returnOrder(LocalDateTime byTime){
        new Thread(){
            @Override
            public void run(){
                Long pushKingdeeReturnGoodsId = 0L;
                Boolean queryMarked = false;
                LocalDateTime byTimes = byTime;
                do {
                    log.info("KingdeeAbnormalSalesOrderService.returnOrder pushKingdeeReturnGoodsId:{} byTimes:{}",pushKingdeeReturnGoodsId,byTimes);
                    List<TradePushKingdeeReturnGoods> returnGoodsList = tradePushKingdeeReturnGoodsRepository.selectPushKingdeeRefundOrderList(pushKingdeeReturnGoodsId, byTimes);
                    log.info("KingdeeAbnormalSalesOrderService.returnOrder returnGoodsList:{}",returnGoodsList.size());
                    if (returnGoodsList.size() > 0){
                        List<String> returnIdList = returnGoodsList.stream().map(TradePushKingdeeReturnGoods::getReturnGoodsCode).collect(Collectors.toList());
                        List<ReturnOrder> returnOrderList = returnOrderService.findAllById(returnIdList);
                        if (returnOrderList.size() > 0) {
                            //退货记录表
                            returnGoodsList.stream().forEach(returnGoodsOrder -> {
                                //退货表
                                returnOrderList.stream().forEach(returnOrder -> {
                                    if (Objects.nonNull(returnGoodsOrder) && Objects.nonNull(returnOrder) && returnGoodsOrder.getReturnGoodsCode().equals(returnOrder.getId())){
                                        log.info("KingdeeAbnormalSalesOrderService.returnOrder returnGoodsCode:{}",returnOrder.getId());
                                        returnOrderService.pushAuditKingdee(returnOrder);
                                    }
                                });
                            });
                            queryMarked = true;
                            TradePushKingdeeReturnGoods tradePushKingdeeRefund = returnGoodsList.stream().reduce((first, second) -> second).orElse(new TradePushKingdeeReturnGoods());
                            pushKingdeeReturnGoodsId = tradePushKingdeeRefund.getPushKingdeeReturnGoodsId();
                        }else {
                            queryMarked = false;
                        }
                    }else {
                        queryMarked = false;
                    }
                }while (queryMarked);
            }
        }.start();
    }

    /**
     * 定时推送金蝶销售单
     */
    public void pushCachePushKingdeeOrder(){
        log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder req");
//        //当前时间前2个小时
//        Calendar beforeTime = Calendar.getInstance();
//        beforeTime.add(Calendar.HOUR, -2);// 2小时之前的时间
//        Date beforeD = beforeTime.getTime();
//        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeD);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        //将字符串转成 LocalDateTime
//        LocalDateTime dateTime = LocalDateTime.parse(before5, dateTimeFormatter);
        //当前时间
        LocalDateTime dateTime = LocalDateTime.now();

        //创建新线程，推送缓存下来的订单() 暂时屏蔽，没有乡村件
      /*  new Thread(){
            @Override
            public void run() {
                Long orderID = 0L;
                long start, end;
                start = System.currentTimeMillis();
                //每一次的标识id
                String flag_id = NanoIdUtils.randomNanoId();
                log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder start:{}",start);
                do {
                    log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder dateTime:{} orderID:{}",dateTime,orderID);
                    List<TradeCachePushKingdeeOrder> cachePushKingdeeOrderList = tradeCachePushKingdeeOrderRepository.getTimerCachePushKingdeeOrder(dateTime,flag_id);
//                    cachePushKingdeeOrderList = cachePushKingdeeOrderList.stream().filter(tradeCachePushKingdeeOrder -> {
//                        if (tradeCachePushKingdeeOrder.getPushStatus() == -1 && tradeCachePushKingdeeOrder.getErroReson() == 0 && (!tradeCachePushKingdeeOrder.getFlagId().equalsIgnoreCase(flag_id) || Objects.isNull(tradeCachePushKingdeeOrder.getFlagId()))) {
//                            return true;
//                        } else if (tradeCachePushKingdeeOrder.getPushStatus() == 0) {
//                            return true;
//                        }
//                        return false;
//                    }).collect(Collectors.toList());
                    log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder cachePushKingdeeOrderList:{}", JSONObject.toJSONString(cachePushKingdeeOrderList));
                    AtomicInteger pushStatus = new AtomicInteger(1);//推送状态
                    AtomicInteger erroReson = new AtomicInteger(2);//异常原因
                    if (cachePushKingdeeOrderList.size() > 0){
                        List<String> orderList = cachePushKingdeeOrderList.stream().map(TradeCachePushKingdeeOrder::getOrderCode).collect(Collectors.toList());
                        List<Trade> trades = tradeService.details(orderList);
                        if (CollectionUtils.isNotEmpty(trades)&&trades.size()>0){
                            trades.stream().forEach(trade -> {
                                pushStatus.set(1);
                                erroReson.set(2);
                                try {
                                    log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder tradeID:{}", trade.getId());
                                    //查询订单的状态,为支付且未发货等推金蝶
                                    if (trade.getTradeState().getPayState().equals(PayState.PAID)
                                            && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                                            && trade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                                            && trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                                        //拼团在订单那提交的时候不推送，在成团时推送
                                        if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                                            try {
                                                List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordAttachment(trade.getId());
                                                if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                                                    Long stockNum = trade.getTradeItems().stream().mapToLong(TradeItem::getNum).sum();
                                                    Long deliveryNum = stockRecordAttachmentList.stream().mapToLong(PileStockRecordAttachment::getNum).sum();
                                                    if (!stockNum.equals(deliveryNum)) {
                                                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "提货数量" + stockNum + "件，不等于发货数量" + deliveryNum + "件，请联系客服处理！");
                                                    }
                                                    Trade pushWmsTrade = KsBeanUtil.convert(trade, Trade.class);
                                                    //新订单商品items
                                                    List<TradeItem> newTradeItems = new ArrayList<>();
                                                    //需要删除的商品items
                                                    List<TradeItem> removeTradeItems = new ArrayList<>();
                                                    pushWmsTrade.getTradeItems().forEach(tradeItem -> {
                                                        //过滤出此商品的提货明细记录
                                                        List<PileStockRecordAttachment> filterList = stockRecordAttachmentList.stream().filter(s -> s.getSkuId().equals(tradeItem.getSkuId())).collect(Collectors.toList());
                                                        //囤货明细记录大于1
                                                        if (CollectionUtils.isNotEmpty(filterList) && filterList.size() > 1) {
                                                            filterList.forEach(f -> {
                                                                //新订单商品item
                                                                TradeItem newTradeItem = KsBeanUtil.convert(tradeItem, TradeItem.class);
                                                                newTradeItem.setPrice(f.getPrice());
                                                                newTradeItem.setVipPrice(f.getPrice());
                                                                newTradeItem.setSplitPrice(f.getPrice().multiply(BigDecimal.valueOf(f.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
                                                                newTradeItem.setOriginalPrice(f.getPrice());
                                                                newTradeItem.setPileOrderCode(f.getOrderCode());
                                                                newTradeItem.setNum(f.getNum());
                                                                newTradeItems.add(newTradeItem);
                                                            });
                                                            removeTradeItems.add(tradeItem);
                                                        } else {
                                                            if (CollectionUtils.isNotEmpty(filterList)) {
                                                                PileStockRecordAttachment filter = filterList.get(0);
                                                                tradeItem.setPileOrderCode(filter.getOrderCode());
                                                            } else {
                                                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货明细副表记录异常，请联系客服！");
                                                            }
                                                        }
                                                    });
                                                    //删除原有订单商品明细记录
                                                    if (CollectionUtils.isNotEmpty(removeTradeItems)) {
                                                        pushWmsTrade.getTradeItems().removeAll(removeTradeItems);
                                                    }
                                                    //添加拆分后的订单商品明细记录
                                                    if (CollectionUtils.isNotEmpty(newTradeItems)) {
                                                        pushWmsTrade.getTradeItems().addAll(newTradeItems);
                                                    }
                                                    orderUtilService.pushWMSOrder(pushWmsTrade, false, trade.getNewVilageFlag());
                                                }else {
                                                    orderUtilService.pushWMSOrder(trade, false, trade.getNewVilageFlag());
                                                }
                                            } catch (Exception e) {
                                                erroReson.set(1);
                                                pushStatus.set(0);
                                                log.info("抛异常================pushStatus"+pushStatus.get());
                                                log.info("抛异常================tradeId"+trade.getId());

                                                log.info("=====订单推送报错日志：" + e + ";  订单编号:" + trade.getId());
                                                cachePushKingdeeOrderExceptionRepository.save(CachePushKingdeeOrderException.builder().exceptionInfo(e.toString()).orderCode(trade.getId()).build());
                                            }
                                        }
//                                    orderProducerService.kingdeePushOrder(trade, 1 * 10L);
//                                    //延时队列
//                                    PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
//                                    PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
//                                                                                                                                       TradeRecordByOrderCodeRequest(trade.getId())).getContext();
//                                    PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
//                                                                                                                             ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
//                                            payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
//                                                                                                                                                            payTradeRecordResponse.getChannelItemId())).getContext();
//
//                                    if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
//                                        tradeService.savePayOrder(trade, payOrder, PayWay.UNIONPAY);
//                                    } else {
//                                        tradeService.savePayOrder(trade, payOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
//                                    }
                                        //老逻辑，推送wms
                                        orderProducerService.pushOrderPayWMS(trade.getId(),15*1000L);
//                                    //推金蝶
//                                    TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
//                                    pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
//                                    pushPayOrderGroupon.setOrderCode(trade.getId());
//                                    orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon, 3 * 60 * 1000L);
                                        try {
                                            Thread.sleep(20000);
                                        }catch (Exception e){
                                            erroReson.set(1);
                                            pushStatus.set(0);
                                            log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder error orderid:{}",trade.getId());
                                        }
                                    }
                                    else {
                                        erroReson.set(0);
                                        pushStatus.set(0);
                                        log.info("无条件================pushStatus"+pushStatus.get());
                                        log.info("无条件================tradeId"+trade.getId());
//                                    tradeCachePushKingdeeOrderRepository.updateTimingCachePushKingdeeWhetherStatus(LocalDateTime.now(),trade.getId());
                                    }
//
//                            tradeCachePushKingdeeOrderRepository.updateTimingCachePushKingdeeOrderStatus(LocalDateTime.now(),trade.getId());
                                }finally {
                                    log.info("================pushStatus"+pushStatus.get());
                                    log.info("================tradeId"+trade.getId());
                                    if (pushStatus.get()== 0){
                                        //如果为0那么错误次数加一
                                        tradeCachePushKingdeeOrderRepository.updateCachePushKingdeNum(trade.getId());
                                    }
                                    tradeCachePushKingdeeOrderRepository.updateTimingCachePushKingde(LocalDateTime.now(),trade.getId(),pushStatus.get(),flag_id,erroReson.get());
                                    tradeCachePushKingdeeOrderRepository.updateCachePushKingdePushStatus();
                                }
                            });
                            TradeCachePushKingdeeOrder cachePushKingdeeOrder = cachePushKingdeeOrderList.stream().reduce((first, second) -> second).orElse(new TradeCachePushKingdeeOrder());
                            orderID = cachePushKingdeeOrder.getPushKingdeeId();
                        }
                        else {
                            orderList.forEach(v->{
                                erroReson.set(0);
                                pushStatus.set(0);
                                log.info("未支付================pushStatus"+pushStatus.get());
                                log.info("未支付================tradeId"+v);
                                if (pushStatus.get()== 0){
                                    //如果为0那么错误次数加一
                                    tradeCachePushKingdeeOrderRepository.updateCachePushKingdeNum(v);
                                }
                                tradeCachePushKingdeeOrderRepository.updateTimingCachePushKingde(LocalDateTime.now(),v,pushStatus.get(),flag_id,erroReson.get());
                                tradeCachePushKingdeeOrderRepository.updateCachePushKingdePushStatus();
                            });
                            log.info("乡镇件推送wms时mongo无数据");
                            orderID = 1L;
                        }
                    }else {
                        orderID = 0L;
                    }




                }while (orderID > 0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.pushCachePushKingdeeOrder end:{}",end-start);
            }
        }.start();*/
    }

    //补偿失败销售订单
    public void compensateFailedSalesOrders() {
        //当前时间前10分钟
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.HOUR, -1);// 1小时之前的时间
        Date beforeD = beforeTime.getTime();
        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeD);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //将字符串转成 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(before5, dateTimeFormatter);

        new Thread() {
            @Override
            public void run() {
                Long orderID = 0L;
                long start, end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders start:{}", start);
                do {
                    List<TradePushKingdeeOrder> tradePushKingdeeOrderList = tradePushKingdeeOrderRepository.selcetPushKingdeeOrderError(orderID, dateTime);
                    if (tradePushKingdeeOrderList.size() > 0) {
                        List<String> orderList = tradePushKingdeeOrderList.stream().map(TradePushKingdeeOrder::getOrderCode).collect(Collectors.toList());
                        List<Trade> trades = tradeService.details(orderList);
                        trades.stream().forEach(trade -> {
                            log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders tradeID:{}", trade.getId());
                            //查询订单的状态,为支付且未发货等推金蝶
                            if (trade.getTradeState().getPayState().equals(PayState.PAID)
                                    && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                                    && trade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                                    && trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                                if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
                                    if (StringUtils.isEmpty(trade.getLogistics()) || trade.getLogistics().equals("01")) {
                                        log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders logistics:{}", trade.getId());
                                        orderProducerService.kingdeePushOrder(trade.getId(), 10 * 1000L);
                                    }
                                }else {
                                    log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders shoppingCart:{}", trade.getId());
                                    orderProducerService.shoppingCartKingdeePushOrder(trade.getId(), 10 * 1000L);
                                }

                                //延时队列
                                PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                                        TradeRecordByOrderCodeRequest(trade.getId())).getContext();
                                PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                                        ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                                        payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                                        payTradeRecordResponse.getChannelItemId())).getContext();

                                TradePushKingdeePayOrder tradePushKingdeePayOrder = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumberPushStatusError(payOrder.getPayOrderId());
                                if (Objects.nonNull(tradePushKingdeePayOrder)) {
                                    if (StringUtils.isEmpty(channelItemResponse.getChannel())) {
                                        newPileTradeService.savePayOrder(trade, payOrder, PayWay.UNIONPAY);
                                    } else {
                                        newPileTradeService.savePayOrder(trade, payOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                                    }
                                    TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                                    pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                                    pushPayOrderGroupon.setOrderCode(trade.getId());
                                    orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                                }
                                try {
                                    Thread.sleep(20000);
                                } catch (Exception e) {
                                    log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders error orderid:{}", trade.getId());
                                }
                            }
                        });
                        TradePushKingdeeOrder tradePushKingdeeOrder = tradePushKingdeeOrderList.stream().reduce((first, second) -> second).orElse(new TradePushKingdeeOrder());
                        orderID = tradePushKingdeeOrder.getPushKingdeeId();
                    } else {
                        orderID = 0L;
                    }

                } while (orderID > 0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateFailedSalesOrders end:{}", end - start);
            }
        }.start();
    }

    /**
     * 补偿囤货订单
     */
    public void compensateStockpilingOrders(){
        //当前时间前10分钟
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.HOUR, -1);// 1小时之前的时间
        Date beforeD = beforeTime.getTime();
        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeD);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //将字符串转成 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(before5, dateTimeFormatter);
        new Thread() {
            @Override
            public void run() {
                Long orderID = 0L;
                long start, end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingOrders start:{}", start);
                do {
                    DescriptionFailedQueryStockPushRequest stockPushRequest = new DescriptionFailedQueryStockPushRequest();
                    stockPushRequest.setPushKingdeeId(orderID);
                    stockPushRequest.setCreateTime(dateTime);
                    BaseResponse<DescriptionFailedQueryStockPushResponse> stockPushKingdeeOrderOrders = pushOrderKingdeeProvider.findStockPushKingdeeOrderOrders(stockPushRequest);
                    if (stockPushKingdeeOrderOrders.getContext() != null){
                        List<String> orderList = stockPushKingdeeOrderOrders.getContext().getStockPushVO().stream().map(DescriptionFailedQueryStockPushVO::getStockOrderCode).collect(Collectors.toList());
                        List<PileTrade> pileTradeList = pileTradeService.details(orderList);
                        pileTradeList.stream().forEach(pileTrade -> {
                            if (pileTrade.getTradeState().getPayState().equals(PayState.PAID)
                                    && pileTrade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                                    && pileTrade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                                    && pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                                //囤货推erp
                                orderProducerService.pushStockkingdee(pileTrade.getId(),6 * 10L);
                                try {
                                    Thread.sleep(20000);
                                } catch (Exception e) {
                                    log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingOrders error orderid:{}", pileTrade.getId());
                                }
                            }
                        });
                        DescriptionFailedQueryStockPushVO stockPushVO = stockPushKingdeeOrderOrders.getContext().getStockPushVO().stream().reduce((first, second) -> second).orElse(new DescriptionFailedQueryStockPushVO());
                        orderID = stockPushVO.getPushKingdeeId();
                        log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingOrders orderID:{}",orderID);
                    }else {
                        orderID = 0L;
                    }
                }while (orderID > 0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingOrders end:{}", end - start);
            }
        }.start();
    }

    /**
     * 补偿囤货退货订单
     */
    public void compensateStockpilingReturnGoodsOrders(){
        //当前时间前10分钟
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.HOUR, -1);// 1小时之前的时间
        Date beforeD = beforeTime.getTime();
        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeD);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //将字符串转成 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(before5, dateTimeFormatter);
        new Thread() {
            @Override
            public void run() {
                Long orderID = 0L;
                long start, end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingReturnGoodsOrders start:{}", start);
                do {
                    DescriptionFailedQueryStockPushRequest stockPushRequest = new DescriptionFailedQueryStockPushRequest();
                    stockPushRequest.setPushKingdeeId(orderID);
                    stockPushRequest.setCreateTime(dateTime);
                    BaseResponse<DescriptionFailedQueryStockPushReturnGoodsResponse> stockPushKingdeeOrderOrders = pushOrderKingdeeProvider.findStockPushKingdeeReturnGoodsOrders(stockPushRequest);
                    if (stockPushKingdeeOrderOrders.getContext() != null){
                        List<String> orderList = stockPushKingdeeOrderOrders.getContext().getReturnGoodsVOList().stream().map(DescriptionFailedQueryStockPushReturnGoodsVO::getStockOrderCode).collect(Collectors.toList());
                        List<ReturnPileOrder> pileTradeList = returnPileOrderService.findAllById(orderList);
                        pileTradeList.stream().forEach(pileTrade -> {
                            //退款状态为已退款或完成
                            if (pileTrade.getReturnFlowState().equals(ReturnFlowState.REFUNDED) || pileTrade.getReturnFlowState().equals(ReturnFlowState.COMPLETED)) {
                                //推erp退货单
                                orderProducerService.pushStockReturnGoodsKingdee(pileTrade.getId(),6 * 10L);
                                try {
                                    Thread.sleep(20000);
                                } catch (Exception e) {
                                    log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingReturnGoodsOrders error orderid:{}", pileTrade.getId());
                                }
                            }
                        });
                    }else {
                        orderID = 0L;
                    }
                }while (orderID > 0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.compensateStockpilingReturnGoodsOrders end:{}", end - start);
            }
        }.start();
    }


    //将历史数据囤货数据推金蝶(只处理已支付未退货情况)
    public void historicalDataStorageDataPushKingdee(LocalDateTime byTime,LocalDateTime rqEndTime){
        log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee byTime:{} rqEndTime:{}",byTime,rqEndTime);
        new Thread(){
            @Override
            public void run() {
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee start:{}",start);
                LocalDateTime byTimes = byTime;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime times = null;
                if(Objects.nonNull(rqEndTime)){
                    times = rqEndTime;
                }else {
                    times = LocalDateTime.now();
                }
                String beginTime = df.format(byTimes);
                String endTime = df.format(times);
                Integer pageNum = 0;
                do {
                    TradeState tradeState = TradeState.builder()
                            .payState(PayState.PAID)
                            .auditState(AuditState.CHECKED)
                            .flowState(FlowState.AUDIT)
                            .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                            .build();
                    TradeQueryRequest queryRequest = new TradeQueryRequest();
                    queryRequest.setBeginTime(beginTime);
                    queryRequest.setEndTime(endTime);
                    queryRequest.setTradeState(tradeState);
                    queryRequest.setPageNum(pageNum);
                    queryRequest.setPageSize(10);
                    Criteria criteria = queryRequest.getWhereCriteria();
                    log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee criteria:{} queryRequest:{}",
                            JSONObject.toJSONString(criteria),JSONObject.toJSONString(queryRequest));
                    List<PileTrade> pileTrades = pileTradeService.page(criteria, queryRequest).getContent();
                    log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee pileTrades.size:{}",pileTrades.size());
                    pileTrades.stream().forEach(pileTrade -> {
                        //囤货推erp
                        orderProducerService.pushStockkingdee(pileTrade.getId(),6 * 10L);
                        try {
                            Thread.sleep(20000);
                        } catch (Exception e) {
                            log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee error orderid:{}", pileTrade.getId());
                        }
                    });

                    if(pileTrades.size()>0){
                        pageNum = pageNum + 1;
                    }else {
                        pageNum = 0;
                    }
                }while (pageNum>0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee end:{}", end - start);
            }
        }.start();
    }

    //将历史数据囤货数据推金蝶(只处理退货情况)
    public void historicalDataStorageDataReturnGoodsPushKingdee(LocalDateTime byTime,LocalDateTime rqEndTime){
        log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee byTime:{}",byTime);
        new Thread(){
            @Override
            public void run() {
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee start:{}",start);
                LocalDateTime byTimes = byTime;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime times = null;
                if(Objects.nonNull(rqEndTime)){
                    times = rqEndTime;
                }else {
                    times = LocalDateTime.now();
                }
                String beginTime = df.format(byTimes);
                String endTime = df.format(times);
                Integer pageNum = 0;
                do {
                    TradeState tradeState = TradeState.builder()
                            .payState(PayState.PAID)
                            .auditState(AuditState.CHECKED)
                            .flowState(FlowState.VOID)
                            .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                            .build();
                    TradeQueryRequest queryRequest = new TradeQueryRequest();
                    queryRequest.setBeginTime(beginTime);
                    queryRequest.setEndTime(endTime);
                    queryRequest.setTradeState(tradeState);
                    queryRequest.setPageNum(pageNum);
                    queryRequest.setPageSize(10);
                    Criteria criteria = queryRequest.getWhereCriteria();
                    log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee criteria:{} queryRequest:{}",
                            JSONObject.toJSONString(criteria),JSONObject.toJSONString(queryRequest));
                    List<PileTrade> pileTrades = pileTradeService.page(criteria, queryRequest).getContent();
                    log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee pileTrades.size:{}",pileTrades.size());
                    pileTrades.stream().forEach(pileTrade -> {
                        //囤货推erp
                        orderProducerService.pushStockkingdee(pileTrade.getId(),6 * 10L);
                        try {
                            Thread.sleep(20000);
                        } catch (Exception e) {
                            log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee error orderid:{}", pileTrade.getId());
                        }
                    });

                    if(pileTrades.size()>0){
                        pageNum = pageNum + 1;
                    }else {
                        pageNum = 0;
                    }
                }while (pageNum>0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataReturnGoodsPushKingdee end:{}", end - start);
            }
        }.start();
    }

    //将历史数据囤货退货数据推金蝶
    public void historicalDataStorageDataPushReturnGoodsKingdee(LocalDateTime byTime,LocalDateTime rqEndTime){
        log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee byTime:{}",byTime);
        new Thread(){
            @Override
            public void run() {
                long start,end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee start:{}",start);
                LocalDateTime byTimes = byTime;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime times = null;
                if(Objects.nonNull(rqEndTime)){
                    times = rqEndTime;
                }else {
                    times = LocalDateTime.now();
                }
                String beginTime = df.format(byTimes);
                String endTime = df.format(times);
                Integer pageNum = 0;
                do {
                    ReturnQueryRequest queryRequest = new ReturnQueryRequest();
                    queryRequest.setBeginTime(beginTime);
                    queryRequest.setEndTime(endTime);
                    queryRequest.setReturnFlowState(ReturnFlowState.COMPLETED);
                    queryRequest.setPageNum(pageNum);
                    queryRequest.setPageSize(10);
                    List<ReturnPileOrder> pileTrades = returnPileOrderService.page(queryRequest).getContent();
                    pileTrades.stream().forEach(pileTrade -> {
                        //推erp退货单
                        orderProducerService.pushStockReturnGoodsKingdee(pileTrade.getId(),6 * 10L);
                        try {
                            Thread.sleep(20000);
                        } catch (Exception e) {
                            log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee error orderid:{}", pileTrade.getId());
                        }
                    });

                    if(pileTrades.size()>0){
                        pageNum = pageNum + 1;
                    }else {
                        pageNum = 0;
                    }
                }while (pageNum>0);
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.historicalDataStorageDataPushKingdee end:{}", end - start);
            }
        }.start();
    }

    //重推囤货
    public void pushStockOrder(String orderIds){
        log.info("KingdeeAbnormalSalesOrderService.pushStockOrder orderIds:{}",orderIds);
        if (StringUtils.isNotEmpty(orderIds)){
            PileTrade pileTrade = pileTradeService.detail(orderIds);
            if (pileTrade.getTradeState().getPayState().equals(PayState.PAID)
                    && pileTrade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                    && pileTrade.getTradeState().getFlowState().equals(FlowState.AUDIT)
                    && pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                log.info("KingdeeAbnormalSalesOrderService.pushStockOrder push orderIds:{}",orderIds);
                //囤货推erp
                orderProducerService.pushStockkingdee(pileTrade.getId(),6 * 10L);
            }
        }
    }

    //重推囤货退货
    public void pushStockReturns(String orderIds){
        log.info("KingdeeAbnormalSalesOrderService.pushStockReturns orderIds:{}",orderIds);
        if (StringUtils.isNotEmpty(orderIds)){
            ReturnPileOrder returnPileOrder = returnPileOrderService.findById(orderIds);
            if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.REFUNDED) || returnPileOrder.getReturnFlowState().equals(ReturnFlowState.COMPLETED)){
                log.info("KingdeeAbnormalSalesOrderService.pushStockReturns push orderIds:{}",orderIds);
                //推erp退货单
                orderProducerService.pushStockReturnGoodsKingdee(returnPileOrder.getId(),6 * 10L);
            }
        }
    }


    //重推新囤货单
    public void newPilePushStockOrder(String orderIds){
        log.info("KingdeeAbnormalSalesOrderService.newPilePushStockOrder orderIds:{}",orderIds);
        if (StringUtils.isNotEmpty(orderIds)){
            NewPileTrade newPileTrade = newPileTradeService.detail(orderIds);
            if (newPileTrade.getTradeState().getPayState().equals(PayState.PAID)
                    && newPileTrade.getTradeState().getAuditState().equals(AuditState.CHECKED)
                    && newPileTrade.getTradeState().getFlowState().equals(NewPileFlowState.PILE)
                    && newPileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                log.info("KingdeeAbnormalSalesOrderService.newPilePushStockOrder push orderIds:{}",orderIds);
                //囤货推erp
                orderProducerService.newPilekingdeePushShopCartOrder(newPileTrade.getId(),6 * 10L);
            }
        }
    }

    //重推新囤货单收款单
    private void newPilePayCompensationOrder(String orderIds){
        if (StringUtils.isNotEmpty(orderIds)) {
            String[] orderId = orderIds.split(",");
            if (orderId.length > 0) {
                List<String> orderList = Arrays.asList(orderId);
                log.info("KingdeeAbnormalSalesOrderService.newPilePayCompensationOrder orderList：{}", orderList);
                long start, end;
                start = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.newPilePayCompensationOrder start:{}", start);
                List<NewPileTrade> trades = newPileTradeService.details(orderList);
                trades.stream().forEach(trade -> {
                    //延时队列
                    PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
                    if (payOrder.getPayOrderStatus().equals(PayOrderStatus.PAYED)) {
                        Integer num = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumberPushStatus(payOrder.getPayOrderId());
                        if (num == 0) {
                            log.info("KingdeeAbnormalSalesOrderService.newPilePayCompensationOrder payOrderId:{}",trade.getId());
                            TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                            pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                            pushPayOrderGroupon.setOrderCode(trade.getId());
                            orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                        }
                    }
                });
                end = System.currentTimeMillis();
                log.info("KingdeeAbnormalSalesOrderService.newPilePayCompensationOrder end:{}",end-start);
            }
        }
    }


    /**
     * 补偿退款数据
     * @param rid
     */
    private void newPiLeCompensationRefundOrder(String rid){
        if (StringUtils.isNotEmpty(rid)){
            TradePushKingdeeRefund pushKingdeeRefund = tradePushKingdeeRefundRepository.selectPushKingdeeRefundOrder(rid);
            RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
            if (Objects.nonNull(refundOrder) && Objects.isNull(pushKingdeeRefund)) {
                log.info("KingdeeAbnormalSalesOrderService.newPiLeCompensationRefundOrder rid:{}",rid);
                NewPileReturnOrder newPileReturnOrder = newPileReturnOrderService.findById(rid);
                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(newPileReturnOrder.getId())).getContext();
                if(payTradeRecordResponse != null){
                    PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) ||
                            Objects.isNull(payTradeRecordResponse.getChannelItemId()) ?
                            Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                            payTradeRecordResponse.getChannelItemId())).getContext();
                    if (StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                        returnOrderService.newPilePushRefundOrderKingdee(newPileReturnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                    }else {
                        returnOrderService.newPilePushRefundOrderKingdee(newPileReturnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                    }
                } else {
                    returnOrderService.newPilePushRefundOrderKingdee(newPileReturnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                }
            }
        }
    }

}
