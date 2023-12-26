package com.wanmi.sbc.order.refund.service;

import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.order.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import com.wanmi.sbc.pay.bean.enums.RefundSourceType;
import com.wanmi.sbc.pay.bean.enums.RefundType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NewPickReturnRefundImpl extends AbstractRefund<Trade, ReturnOrder> {

    @Override
    public Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(Trade trade, ReturnOrder returnOrderT) {
        ReturnOrder returnOrder = returnOrderService.findById(returnOrderT.getId());
        if (isHistoryReturnOrder(returnOrder.getCreateTime())) {
            log.info("历史囤货提货退单，退余额：单号={},创建时间={}", returnOrder.getId(), returnOrder.getCreateTime());
            ReturnPrice returnPrice = returnOrder.getReturnPrice();
            BigDecimal returnBalanceAmount = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();

            String activityType = trade.getActivityType();
            String account = trade.getBuyer().getAccount();
            Map<String, String> extraInfos = new LinkedHashMap<>();
            extraInfos.put("buyerId", trade.getBuyer().getId());
            extraInfos.put("account", account);
            extraInfos.put("activityType", activityType);

            // 拼接描述信息
            String description = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                    () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
            if (trade.getTradeItems().size() > 1) {
                description = description + " 等多件商品";
            }

            RefundByChannelRequest.RefundItem refundItem = RefundByChannelRequest.RefundItem.builder()
                    .sourceType(RefundSourceType.RETURN_ORDER)
                    .refundType(RefundType.BALANCE)
                    .storeId(trade.getSupplier().getStoreId())
                    .bizId(trade.getId())
                    .refundBizId(returnOrder.getId())
                    .refundAmount(returnBalanceAmount)
                    .extraInfos(extraInfos)
                    .description(description)
                    .build();

            Map<String, List<RefundByChannelRequest.RefundItem>> resultMap = new LinkedHashMap<>();
            resultMap.put(returnOrder.getId(), Collections.singletonList(refundItem));
            return resultMap;
        }

        List<String> pileNos = returnOrder.getReturnItems().stream()
                .flatMap(returnItem -> returnItem.getReturnGoodsList().stream())
                .map(PickGoodsDTO::getNewPileOrderNo)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(pileNos)) {
            log.warn("提货退单对应的囤货单号为空：退单号={}", returnOrder.getId());
            return null;
        }

        //商品费用，拆分到每个囤货单
        //商品费用（改价），拆分到每个囤货单
        if (returnOrder.getReturnPrice().getApplyStatus()) {
            returnOrderService.refillReturnBalancePriceAndCashPrice(returnOrder, returnOrder.getReturnPrice().getApplyPrice());
            returnOrderService.updateReturnOrder(returnOrder);
        }

        //null值设置为0
        returnOrder.getReturnItems().forEach(returnItem -> {
            returnItem.getReturnGoodsList().forEach(pickGoodsDTO -> {
                if (Objects.isNull(pickGoodsDTO.getReturnCashPrice())) {
                    pickGoodsDTO.setReturnCashPrice(BigDecimal.ZERO);
                }
                if (Objects.isNull(pickGoodsDTO.getReturnBalancePrice())) {
                    pickGoodsDTO.setReturnBalancePrice(BigDecimal.ZERO);
                }
            });
        });


        // 拼接描述信息
        String description = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trade.getTradeItems().size() > 1) {
            description = description + " 等多件商品";
        }

        Buyer buyer = trade.getBuyer();
        String activityType = trade.getActivityType();
        String account = buyer.getAccount();
        Map<String, String> extraInfos = new LinkedHashMap<>();
        extraInfos.put("buyerId", trade.getBuyer().getId());
        extraInfos.put("account", account);
        extraInfos.put("activityType", activityType);

        //遍历每一个returnItem，按囤货单及付款方式汇总应退金额
        //按囤货单分组汇总退现金
        Map<String, BigDecimal> returnCashAmountByOrderMap = returnOrder.getReturnItems().stream()
                .flatMap(returnItem -> returnItem.getReturnGoodsList().stream())
                .collect(Collectors.groupingBy(
                        PickGoodsDTO::getNewPileOrderNo,
                        Collectors.reducing(BigDecimal.ZERO, PickGoodsDTO::getActualReturnCashPrice, BigDecimal::add)
                ));
        //按囤货单分组汇总退余额
        Map<String, BigDecimal> returnBalanceAmountByOrderMap = returnOrder.getReturnItems().stream()
                .flatMap(returnItem -> returnItem.getReturnGoodsList().stream())
                .collect(Collectors.groupingBy(
                        PickGoodsDTO::getNewPileOrderNo,
                        Collectors.reducing(BigDecimal.ZERO, PickGoodsDTO::getActualReturnBalancePrice, BigDecimal::add)
                ));

        //获取囤货单映射
        Map<String, NewPileTrade> newPileOrderByNoMap = newPileTradeService.listByPileNos(pileNos).stream()
                .collect(Collectors.toMap(NewPileTrade::getId, v -> v, (k1, k2) -> k1));


        NewPileTrade newPileTrade;
        Map<String, List<RefundByChannelRequest.RefundItem>> resultMap = new LinkedHashMap<>();

        int index = 1;
        for (Map.Entry<String, BigDecimal> entry : returnCashAmountByOrderMap.entrySet()) {
            String newPileOrderNo = entry.getKey();
            BigDecimal returnCashAmount = entry.getValue();
            if (Objects.isNull(resultMap.get(newPileOrderNo))) {
                resultMap.put(newPileOrderNo, new ArrayList<>());
            }

            newPileTrade = newPileOrderByNoMap.get(newPileOrderNo);
            //在线支付：退款方式 分为在线退 和 退余额两种
            if (PayType.ONLINE == PayType.fromValue(Integer.parseInt(newPileTrade.getPayInfo().getPayTypeId()))) {
                BigDecimal totalOnlineTradePrice = newPileTrade.getTradePrice().getTotalPayCash();
                if (returnCashAmount.compareTo(BigDecimal.ZERO) > 0) {
                    resultMap.get(newPileOrderNo).add(
                            RefundByChannelRequest.RefundItem.builder()
                                    .sourceType(RefundSourceType.RETURN_ORDER)
                                    .refundType(RefundType.ONLINE)
                                    .storeId(newPileTrade.getSupplier().getStoreId())
                                    .bizId(newPileOrderNo)
                                    .refundBizId(returnOrder.getId() + "_" + index++)
                                    .totalOnlineTradePrice(totalOnlineTradePrice)
                                    .refundAmount(returnCashAmount)
                                    .description(description)
                                    .build()
                    );
                }

                BigDecimal returnBalanceAmount = returnBalanceAmountByOrderMap.get(newPileOrderNo);
                if (returnBalanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                    resultMap.get(newPileOrderNo).add(
                            RefundByChannelRequest.RefundItem.builder()
                                    .sourceType(RefundSourceType.RETURN_ORDER)
                                    .refundType(RefundType.BALANCE)
                                    .storeId(newPileTrade.getSupplier().getStoreId())
                                    .bizId(newPileOrderNo)
                                    .refundBizId(returnOrder.getId() + "_" + index++)
                                    .refundAmount(returnBalanceAmount)
                                    .extraInfos(extraInfos)
                                    .description(description)
                                    .build()
                    );
                }
                continue;
            }

            //不是在线支付：全部退余额
            BigDecimal returnBalanceAmount = returnBalanceAmountByOrderMap.get(newPileOrderNo);
            BigDecimal totalReturnBalanceAmount = returnBalanceAmount.add(returnCashAmount);
            if (totalReturnBalanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                resultMap.get(newPileOrderNo).add(
                        RefundByChannelRequest.RefundItem.builder()
                                .sourceType(RefundSourceType.RETURN_ORDER)
                                .refundType(RefundType.BALANCE)
                                .storeId(newPileTrade.getSupplier().getStoreId())
                                .bizId(newPileOrderNo)
                                .refundBizId(returnOrder.getId() + "_" + index++)
                                .refundAmount(totalReturnBalanceAmount)
                                .extraInfos(extraInfos)
                                .description(description)
                                .tid(newPileOrderNo)
                                .build()
                );
            }
        }

        //退运费: 未发货 且 运费 > 0
        if (DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())
                && trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) > 0
        ) {
            if (Objects.isNull(resultMap.get(trade.getId()))) {
                resultMap.put(trade.getId(), new ArrayList<>());
            }

            resultMap.get(trade.getId()).add(
                    RefundByChannelRequest.RefundItem.builder()
                            .sourceType(RefundSourceType.RETURN_ORDER)
                            .refundType(RefundType.ONLINE)
                            .storeId(trade.getSupplier().getStoreId())
                            .bizId(trade.getId())
                            .refundBizId(returnOrder.getId()+"_D")
                            .totalOnlineTradePrice(trade.getTradePrice().getDeliveryPrice())
                            .refundAmount(trade.getTradePrice().getDeliveryPrice())
                            .description("运费")
                            .tid(trade.getId())
                            .build()
            );
        }
        return resultMap;
    }

    @Override
    public void refund(Trade trade, ReturnOrder returnOrder) {
        super.doRefund(trade, returnOrder);
    }
}
