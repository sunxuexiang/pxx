package com.wanmi.sbc.order.refund.service;

import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.mq.OrderProducerService;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.PayInfo;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.pay.api.provider.PayProvider;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import com.wanmi.sbc.pay.bean.enums.RefundSourceType;
import com.wanmi.sbc.pay.bean.enums.RefundType;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.WalletInfoRequest;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public abstract class AbstractRefund<T, K> {
    @Autowired
    PayProvider payProvider;
    @Autowired
    NewPileTradeService newPileTradeService;
    @Autowired
    ReturnOrderService returnOrderService;
    @Autowired
    NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    RefundOrderService refundOrderService;
    @Autowired
    CustomerWalletProvider customerWalletProvider;

    @Autowired
    private OrderProducerService orderProducerService;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    /**
     * 计算单号对应各渠道应退金额
     *
     * @param trade       订单
     * @param returnOrder 退单
     * @return
     */
    abstract Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(T trade, K returnOrder);

    /**
     * 退款
     *
     * @param trade       订单
     * @param returnOrder 退单
     */
    public abstract void refund(T trade, K returnOrder);

    protected void doRefund(T tradeT, K returnOrderT) {
        Map<String, List<RefundByChannelRequest.RefundItem>> channelRefundMap = calcChannelRefundAmount(tradeT, returnOrderT);
        //检查是否有退款金额，没有则不调用退款接口
        if (MapUtils.isEmpty(channelRefundMap)) {
            log.info("退款结果集为空，不再处理");
            return;
        }

        List<RefundByChannelRequest.RefundItem> amountGtZeroRefundItems = channelRefundMap.values().stream().flatMap(Collection::stream)
                .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(amountGtZeroRefundItems)) {
            log.info("退款结果集无大于0的退款项，不再处理");
            return;
        }

        payProvider.refundByChannel(
                RefundByChannelRequest
                        .builder()
                        .payTypeRefundItemsByOrderMap(channelRefundMap)
                        .build()
        );


        if (Objects.isNull(returnOrderT)) {
            //取消订单，无退单，不推送
            return;
        }

        //汇总在线退款总金额，余额退款总金额，保存到退单
        K currentReturnOrder = saveTotalChannelRefundAmount(returnOrderT, channelRefundMap);

        if (tradeT instanceof Trade) {
            log.info("tradeT.getClass().getSimpleName()111:{}",tradeT.getClass().getSimpleName());
            ReturnOrder returnOrder = (ReturnOrder)currentReturnOrder;
            RefundOrder refundOrdeP = refundOrderService.findRefundOrderByReturnOrderNo(returnOrder.getId());
            RefundOrder refundOrder = KsBeanUtil.convert(refundOrdeP,RefundOrder.class);
            Trade trade = (Trade) tradeT;
            returnOrderService.pushKingdeeEntry(trade,returnOrder,refundOrder);
            // 非自营订单 退款 佣金部分 推送金蝶
            String returnOrderId = returnOrder.getId();
            String tradeId = trade.getId();
            String sendStr = tradeId + "#" + returnOrderId;
            if (wmsAPIFlag && Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                log.info("非自营商家，佣金部分推送ERP退款单：{}", sendStr);
                orderProducerService.pushRefundCommisionToKingdee(sendStr, 60 * 1000L);
            }

            if (Objects.equals(trade.getPayWay(), PayWay.CCB) && DeliverWay.isTmsDelivery(trade.getDeliverWay())) {
                log.info("订单退款,处理运费加收，发送消息：{}", sendStr);
                orderProducerService.pushRefundExtra(sendStr, 5 * 1000L);
            }

        } else if (tradeT instanceof NewPileTrade) {
            log.info("tradeT.getClass().getSimpleName()222:{}",tradeT.getClass().getSimpleName());
            NewPileReturnOrder returnOrder = (NewPileReturnOrder)currentReturnOrder;
            NewPileTrade trade = (NewPileTrade) tradeT;
            RefundOrder refundOrdeP = refundOrderService.findRefundOrderByReturnOrderNo(returnOrder.getId());
            RefundOrder refundOrder = KsBeanUtil.convert(refundOrdeP,RefundOrder.class);
            returnOrderService.pushKingdeeEntryPile(trade,returnOrder,refundOrder);
            // 订单 退款 佣金部分 推送金蝶
            if (wmsAPIFlag && Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                String returnOrderId = returnOrder.getId();
                String tradeId = trade.getId();
                String sendStr = tradeId + "#" + returnOrderId;
                log.info("非自营商家，佣金部分推送ERP退款单：{}", sendStr);
                orderProducerService.pushRefundCommisionToKingdee(sendStr,  60 * 1000L);
            }
        } else {
            throw new SbcRuntimeException("不支持的订单类型：" + tradeT.getClass().getSimpleName());
        }

    }

    /**
     * 汇总在线退款总金额，余额退款总金额，保存到退单
     *
     * @param returnOrderT
     * @param channelRefundMap
     */
    protected K saveTotalChannelRefundAmount(K returnOrderT, Map<String, List<RefundByChannelRequest.RefundItem>> channelRefundMap) {
        if (Objects.isNull(returnOrderT)) {
            //取消订单，无退单
            return null;
        }

        BigDecimal totalOnlineRefundAmount = channelRefundMap.values().stream().flatMap(Collection::stream)
                .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(it -> Objects.equals(it.getRefundType(), RefundType.ONLINE))
                .map(RefundByChannelRequest.RefundItem::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBalanceRefundAmount = channelRefundMap.values().stream().flatMap(Collection::stream)
                .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(it -> Objects.equals(it.getRefundType(), RefundType.BALANCE))
                .map(RefundByChannelRequest.RefundItem::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (returnOrderT instanceof ReturnOrder) {
            ReturnOrder returnOrderPass = (ReturnOrder) returnOrderT;
            ReturnOrder returnOrder = returnOrderService.findById(returnOrderPass.getId());
            returnOrder.getReturnPrice().setTotalOnlineRefundAmount(totalOnlineRefundAmount);
            returnOrder.getReturnPrice().setTotalBalanceRefundAmount(totalBalanceRefundAmount);
            returnOrderService.updateReturnOrder(returnOrder);
            return (K) returnOrder;
        } else if (returnOrderT instanceof NewPileReturnOrder) {
            NewPileReturnOrder returnOrderPass = (NewPileReturnOrder) returnOrderT;
            NewPileReturnOrder returnOrder = newPileReturnOrderService.dealEmptyFindById(returnOrderPass.getId());
            returnOrder.getReturnPrice().setTotalOnlineRefundAmount(totalOnlineRefundAmount);
            returnOrder.getReturnPrice().setTotalBalanceRefundAmount(totalBalanceRefundAmount);
            newPileReturnOrderService.updateReturnOrder(returnOrder);
            return (K) returnOrder;
        } else {
            throw new SbcRuntimeException("不支持的退单类型：" + returnOrderT.getClass().getSimpleName());
        }
    }

    /**
     * 所有订单取消时，还未付款，但抵扣了余额，需退余额
     *
     * @param tradeT
     * @return
     */
    protected Map<String, List<RefundByChannelRequest.RefundItem>> getRefundMapForCancel(T tradeT) {
        Assert.notNull(tradeT, "tradeT 不能为null");

        TradePrice tradePrice;
        String bizId;
        Long storeId;
        Buyer buyer;
        String activityType;

        if (tradeT instanceof Trade) {
            Trade trade = (Trade) tradeT;
            tradePrice = trade.getTradePrice();
            bizId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            storeId = trade.getSupplier().getStoreId();
            buyer = trade.getBuyer();
            activityType = trade.getActivityType();
        } else if (tradeT instanceof NewPileTrade) {
            NewPileTrade trade = (NewPileTrade) tradeT;
            tradePrice = trade.getTradePrice();
            bizId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            storeId = trade.getSupplier().getStoreId();
            buyer = trade.getBuyer();
            activityType = trade.getActivityType();
        } else {
            throw new SbcRuntimeException("不支持的订单类型：" + tradeT.getClass().getSimpleName());
        }

        String account = buyer.getAccount();
        Map<String, String> extraInfos = new LinkedHashMap<>();
        extraInfos.put("buyerId", buyer.getId());
        extraInfos.put("account", account);
        extraInfos.put("activityType", activityType);
        extraInfos.put("storeId",String.valueOf(storeId));

        BigDecimal balancePrice = tradePrice.getBalancePrice();
        //历史订单取消：未开放鲸币使用，不会产生鲸币抵扣，故不存在订单余额
        Map<String, List<RefundByChannelRequest.RefundItem>> resultMap = new LinkedHashMap<>();
        List<RefundByChannelRequest.RefundItem> refundItemList = new ArrayList<>();
        refundItemList.add(RefundByChannelRequest.RefundItem.builder()
                .sourceType(RefundSourceType.CANCEL_ORDER)
                .refundType(RefundType.BALANCE)
                .storeId(storeId)
                .bizId(bizId)
                .refundAmount(balancePrice)
                .extraInfos(extraInfos)
                .build());
        resultMap.put(bizId, refundItemList);
        return resultMap;
    }

    /**
     * 囤货退，普通订单退，不涉及分摊计算，统一处理
     *
     * @param tradeT
     * @param returnOrderT
     * @return
     */
    protected Map<String, List<RefundByChannelRequest.RefundItem>> getRefundMapForReturn(T tradeT, K returnOrderT) {
        Assert.notNull(tradeT, "tradeT 不能为null");
        Assert.notNull(returnOrderT, "returnOrderT 不能为null");

        TradePrice tradePrice;
        ReturnPrice returnPrice;
        PayInfo payInfo;
        String description;
        List<TradeItem> tradeItems;
        String bizId;
        String refundBizId;
        BigDecimal totalOnlineTradePrice = BigDecimal.ZERO;
        Long storeId;
        Buyer buyer;
        String activityType;
        BigDecimal totalReturnBalance;
        BigDecimal totalReturnOnline;
        String tid;
        boolean refundFreight = false;
        BigDecimal freightPrice = BigDecimal.ZERO;

        boolean isTrade = tradeT instanceof Trade;
        if (isTrade) {
            Trade trade = (Trade) tradeT;
            ReturnOrder returnOrderPass = (ReturnOrder) returnOrderT;
            ReturnOrder returnOrder = returnOrderService.findById(returnOrderPass.getId());
            returnPrice = returnOrder.getReturnPrice();
            bizId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            refundBizId = returnOrder.getId();
            payInfo = trade.getPayInfo();
            tradeItems = trade.getTradeItems();
            tradePrice = trade.getTradePrice();
            storeId = trade.getSupplier().getStoreId();
            buyer = trade.getBuyer();
            activityType = trade.getActivityType();
            tid = trade.getId();

            // 拼接描述信息
            description = tradeItems.get(0).getSkuName() + " " + (tradeItems.get(0).getSpecDetails
                    () == null ? "" : tradeItems.get(0).getSpecDetails());
            if (tradeItems.size() > 1) {
                description = description + " 等多件商品";
            }


            BigDecimal deliveryPrice = BigDecimal.ZERO;
            BigDecimal packingPrice = BigDecimal.ZERO;
            //未发货，需退运费
            if (DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())) {
                BigDecimal villageAddliveryPrice = trade.getTradePrice().getVillageAddliveryPrice();
                if (Objects.nonNull(villageAddliveryPrice) && villageAddliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                    deliveryPrice = trade.getTradePrice().getDeliveryPrice().subtract(villageAddliveryPrice);
                }else {
                    deliveryPrice = trade.getTradePrice().getDeliveryPrice();
                }
            } else if (Objects.nonNull(returnPrice.getDeliveryPrice())) {
                deliveryPrice = returnPrice.getDeliveryPrice();
            }

            // 配送到店的运费单独退
            if (DeliverWay.isCcbSubBill(trade.getDeliverWay()) && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                refundFreight = true;
                freightPrice = deliveryPrice;
            }

            //零售订单，微信支付和鲸币支付、线下支付，未发货退款，少退纸箱费
            if (DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())
                    && SaleType.RETAIL.equals(trade.getSaleType())) {
                packingPrice = trade.getTradePrice().getPackingPrice();
            }

            if (isHistoryReturnOrder(returnOrder.getCreateTime())) {
                log.info("历史普通退单，原路返还：单号={},创建时间={}", returnOrder.getId(), returnOrder.getCreateTime());
                BigDecimal totalReturnPrice = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
                if (PayType.ONLINE == PayType.fromValue(Integer.parseInt(payInfo.getPayTypeId()))) {
                    totalReturnOnline = totalReturnPrice;
                    totalReturnBalance = BigDecimal.ZERO;
                    totalOnlineTradePrice = totalReturnPrice;
                } else {
                    totalReturnOnline = BigDecimal.ZERO;
                    totalReturnBalance = totalReturnPrice;
                    totalOnlineTradePrice = BigDecimal.ZERO;
                }
            } else {
                if (returnPrice.getApplyStatus()) {
                    returnOrderService.refillReturnBalancePriceAndCashPrice(returnOrder, returnPrice.getApplyPrice());
                    returnOrderService.updateReturnOrder(returnOrder);
                }

                BigDecimal returnCash = returnPrice.getActualReturnCash();
                BigDecimal totalReturnCash = returnCash.add(deliveryPrice).add(packingPrice);
                BigDecimal returnBalance = returnPrice.getActualBalanceReturnPrice();
                if (PayType.ONLINE == PayType.fromValue(Integer.parseInt(payInfo.getPayTypeId()))) {
                    totalReturnOnline = totalReturnCash;
                    totalReturnBalance = returnBalance;
                    totalOnlineTradePrice = tradePrice.getTotalPayCash();
                } else {
                    totalReturnOnline = BigDecimal.ZERO;
                    totalReturnBalance = totalReturnCash.add(returnBalance);
                }
            }

        } else if (tradeT instanceof NewPileTrade) {
            NewPileTrade trade = (NewPileTrade) tradeT;
            NewPileReturnOrder returnOrderPass = (NewPileReturnOrder) returnOrderT;
            NewPileReturnOrder returnOrder = newPileReturnOrderService.dealEmptyFindById(returnOrderPass.getId());
            returnPrice = returnOrder.getReturnPrice();
            bizId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            refundBizId = returnOrder.getId();
            payInfo = trade.getPayInfo();
            tradePrice = trade.getTradePrice();
            storeId = trade.getSupplier().getStoreId();
            buyer = trade.getBuyer();
            activityType = trade.getActivityType();
            tid = trade.getId();

            BigDecimal reduceSplitPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            description = "商品费用:" + "(" + reduceSplitPrice + ")" + "-鲸币抵扣:" + "(" + tradePrice.getBalancePrice() + ")"
                    + "-运费:" + "(" + tradePrice.getDeliveryPrice() + ")" + "-包装费用:" + "(" + tradePrice.getPackingPrice() + ")";

            //历史囤货退单 退余额
            if (isHistoryReturnOrder(returnOrder.getCreateTime())) {
                log.info("历史囤货退单，退余额：单号={},创建时间={}", returnOrder.getId(), returnOrder.getCreateTime());
                totalReturnOnline = BigDecimal.ZERO;
                totalReturnBalance = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
            } else {
                if (returnPrice.getApplyStatus()) {
                    newPileReturnOrderService.refillReturnBalancePriceAndCashPrice(returnOrder, returnPrice.getApplyPrice());
                    newPileReturnOrderService.updateReturnOrder(returnOrder);
                }

                BigDecimal returnCash = returnPrice.getActualReturnCash();
                BigDecimal returnBalance = returnPrice.getActualBalanceReturnPrice();
                if (PayType.ONLINE == PayType.fromValue(Integer.parseInt(payInfo.getPayTypeId()))) {
                    totalReturnOnline = returnCash;
                    totalReturnBalance = returnBalance;
                    totalOnlineTradePrice = tradePrice.getTotalPayCash();
                } else {
                    totalReturnOnline = BigDecimal.ZERO;
                    //线下支付全部退余额：检查是否改价
                    totalReturnBalance = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
                }
            }

        } else {
            throw new SbcRuntimeException("不支持的订单类型：" + tradeT.getClass().getSimpleName());
        }

        List<RefundByChannelRequest.RefundItem> refundItemList = new ArrayList<>();
        if (totalReturnOnline.compareTo(BigDecimal.ZERO) > 0) {
            refundItemList.add(RefundByChannelRequest.RefundItem.builder()
                    .sourceType(RefundSourceType.RETURN_ORDER)
                    .refundType(RefundType.ONLINE)
                    .storeId(storeId)
                    .bizId(bizId)
                    .refundBizId(refundBizId)
                    .totalOnlineTradePrice(totalOnlineTradePrice)
                    .refundAmount(totalReturnOnline)
                    .description(description)
                    .tid(tid)
                    .refundFreight(refundFreight)
                    .freightPrice(freightPrice)
                    .build());
        }

        if (totalReturnBalance.compareTo(BigDecimal.ZERO) > 0) {
            String account = buyer.getAccount();
            Map<String, String> extraInfos = new LinkedHashMap<>();
            extraInfos.put("buyerId", buyer.getId());
            extraInfos.put("account", account);
            extraInfos.put("activityType", activityType);
            String storeIdStr = String.valueOf(storeId);
            extraInfos.put("storeId", storeIdStr);
            CusWalletVO cusWalletVO = customerWalletProvider.queryCustomerWallet(WalletInfoRequest.builder().storeId(storeIdStr).storeFlag(true).build()).getContext();
            BigDecimal customserBalacne = cusWalletVO == null ? BigDecimal.ZERO:cusWalletVO.getBalance();
            //是订单并且鲸币没有账户或者鲸币账户不可用或者鲸币账户余额小于本次付款金额
            boolean isCanBalance = (cusWalletVO != null && 1 == cusWalletVO.getIsEnable() && cusWalletVO.getBalance().compareTo(totalReturnBalance) > -1);
            if (!isTrade || (isTrade && isCanBalance)) {
                refundItemList.add(RefundByChannelRequest.RefundItem.builder()
                        .sourceType(RefundSourceType.RETURN_ORDER)
                        .refundType(RefundType.BALANCE)
                        .storeId(storeId)
                        .bizId(bizId)
                        .refundBizId(refundBizId)
                        .refundAmount(totalReturnBalance)
                        .description(description)
                        .extraInfos(extraInfos)
                        .tid(tid)
                        .refundFreight(refundFreight)
                        .freightPrice(freightPrice)
                        .build());
            }
            if(isTrade){
                if(!isCanBalance){
                    log.info("商家账户余额不足。用线下支付,tid:[{}],storeId:[{}],balance:[{}],totalAmount:[{}]",tid,storeId,customserBalacne,totalReturnBalance);
                }
            }
        }

        Map<String, List<RefundByChannelRequest.RefundItem>> resultMap = new LinkedHashMap<>();
        resultMap.put(refundBizId, refundItemList);
        return resultMap;
    }

    protected boolean isHistoryReturnOrder(LocalDateTime createTime) {
        Assert.notNull(createTime, "退单创建时间不能为null");
        LocalDateTime refundOriginEnableTime = LocalDateTime.of(2023, 1, 14, 2, 0, 0);
        return createTime.isBefore(refundOriginEnableTime);
    }

    public static void main(String[] args) {
        System.out.println(new NewPileReturnRefundImpl().isHistoryReturnOrder(LocalDateTime.now()));
        System.out.println(new NewPileReturnRefundImpl().isHistoryReturnOrder(LocalDateTime.of(2023, 1, 14, 8, 0, 0)));
    }
}
