package com.wanmi.sbc.order.refund.service;

import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import com.wanmi.sbc.pay.bean.enums.RefundSourceType;
import com.wanmi.sbc.pay.bean.enums.RefundType;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenchang
 * @since 2023/04/18 10:45
 */
@Component
@Slf4j
public class NewPileClaimsRefundImpl extends AbstractRefund<NewPileTrade, NewPileReturnOrder> {

    @Override
    Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(NewPileTrade trade, NewPileReturnOrder returnOrder) {
        Assert.notNull(returnOrder,"returnOrder must not be null");
        Assert.notNull(returnOrder.getId(),"returnOrder.getId() must not be null");

        String account = trade.getBuyer().getAccount();
        Map<String, String> extraInfos = new LinkedHashMap<>();
        extraInfos.put("buyerId", trade.getBuyer().getId());
        extraInfos.put("account", account);
        extraInfos.put("activityType", trade.getActivityType());
        extraInfos.put("chaimApplyType", returnOrder.getChaimApllyType().toString());

        String bizId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
        String refundBizId = returnOrder.getId();
        ReturnPrice returnPrice = returnOrder.getReturnPrice();

        List<RefundByChannelRequest.RefundItem> refundItemList = new ArrayList<>();
        refundItemList.add(RefundByChannelRequest.RefundItem.builder()
                .sourceType(RefundSourceType.RETURN_ORDER)
                .refundType(RefundType.BALANCE)
                .storeId(trade.getSupplier().getStoreId())
                .bizId(bizId)
                .refundBizId(refundBizId)
                .refundAmount(returnPrice.getApplyPrice())
                .description(returnOrder.getDescription())
                .extraInfos(extraInfos)
                .build());

        Map<String, List<RefundByChannelRequest.RefundItem>> resultMap = new HashMap<>();
        resultMap.put(refundBizId, refundItemList);
        return resultMap;
    }

    @Override
    public void refund(NewPileTrade newPileTrade, NewPileReturnOrder newPileReturnOrder) {
        Map<String, List<RefundByChannelRequest.RefundItem>> channelRefundMap = calcChannelRefundAmount(newPileTrade, newPileReturnOrder);
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
        newPileReturnOrderService.pushKingdeeForClaims(newPileTrade,newPileReturnOrder);
    }
}
