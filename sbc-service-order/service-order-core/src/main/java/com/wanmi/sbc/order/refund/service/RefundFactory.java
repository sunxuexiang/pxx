package com.wanmi.sbc.order.refund.service;

import com.wanmi.sbc.common.util.SpringContextHolder;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import org.springframework.util.Assert;

public class RefundFactory {
    public enum NewPileRefundType {
        NEW_PILE_CANCEL,
        NEW_PILE_RETURN,
        NEW_PILE_CLAIMS;
    }

    public enum TradeRefundType {
        NEW_PICK_CANCEL,
        NEW_PICK_RETURN,
        TRADE_CANCEL,
        TRADE_RETURN,
        /**
         * 理赔类退款
         */
        CLAIMS;
    }

    public static AbstractRefund<NewPileTrade, NewPileReturnOrder> getNewPileRefundImpl(NewPileRefundType refundType) {
        Assert.notNull(refundType, "refundType不能为null");
        switch (refundType) {
            case NEW_PILE_CANCEL:
                return SpringContextHolder.getBean(NewPileCancelRefundImpl.class);
            case NEW_PILE_RETURN:
                return SpringContextHolder.getBean(NewPileReturnRefundImpl.class);
            case NEW_PILE_CLAIMS:
                return SpringContextHolder.getBean(NewPileClaimsRefundImpl.class);
            default:
                throw new RuntimeException("不支持的退款类型" + refundType.name());
        }
    }

    public static AbstractRefund<Trade, ReturnOrder> getTradeRefundImpl(TradeRefundType refundType) {
        Assert.notNull(refundType, "refundType不能为null");
        switch (refundType) {
            case NEW_PICK_CANCEL:
                return SpringContextHolder.getBean(NewPickCancelRefundImpl.class);
            case NEW_PICK_RETURN:
                return SpringContextHolder.getBean(NewPickReturnRefundImpl.class);
            case TRADE_CANCEL:
                return SpringContextHolder.getBean(TradeCancelRefundImpl.class);
            case TRADE_RETURN:
                return SpringContextHolder.getBean(TradeReturnRefundImpl.class);
            case CLAIMS:
                return SpringContextHolder.getBean(ClaimsRefundImpl.class);
            default:
                throw new RuntimeException("不支持的退款类型" + refundType.name());
        }
    }
}
