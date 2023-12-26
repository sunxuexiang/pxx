package com.wanmi.sbc.order.refund.service;

import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NewPickCancelRefundImpl extends AbstractRefund<Trade, ReturnOrder> {

    @Override
    public Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(Trade trade, ReturnOrder returnOrder) {
        return super.getRefundMapForCancel(trade);
    }

    @Override
    public void refund(Trade trade, ReturnOrder returnOrder) {
        super.doRefund(trade, returnOrder);
    }
}
