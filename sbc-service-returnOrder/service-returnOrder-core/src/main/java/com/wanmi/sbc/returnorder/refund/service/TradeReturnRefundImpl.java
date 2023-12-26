package com.wanmi.sbc.returnorder.refund.service;

import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TradeReturnRefundImpl extends AbstractRefund<Trade, ReturnOrder> {
    @Override
    public Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(Trade trade, ReturnOrder returnOrder) {
        return super.getRefundMapForReturn(trade, returnOrder);
    }

    @Override
    public void refund(Trade trade, ReturnOrder returnOrder) {
        super.doRefund(trade, returnOrder);
    }
}
