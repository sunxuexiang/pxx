package com.wanmi.sbc.returnorder.refund.service;

import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NewPileCancelRefundImpl extends AbstractRefund<NewPileTrade, NewPileReturnOrder> {

    @Override
    public Map<String, List<RefundByChannelRequest.RefundItem>> calcChannelRefundAmount(NewPileTrade trade, NewPileReturnOrder returnOrder) {
        return super.getRefundMapForCancel(trade);
    }

    @Override
    public void refund(NewPileTrade trade, NewPileReturnOrder returnOrder) {
        super.doRefund(trade, returnOrder);
    }
}
