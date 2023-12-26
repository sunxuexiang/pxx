package com.wanmi.sbc.returnorder.trade.fsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.fsm.TradeAction;
import com.wanmi.sbc.returnorder.trade.fsm.TradeStateContext;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>自提Action</p>
 * Created by of628-wenzhi on 2017-06-02-下午6:01.
 */

@Component
public class PickUpAction extends TradeAction {

    @Override
    protected void evaluateInternal(Trade trade, StateRequest request, TradeStateContext tsc) {
        if (trade.getDeliverWay().equals(DeliverWay.PICK_SELF)){
            TradeState tradeState = trade.getTradeState();
            Operator operator = tsc.getOperator();
            tradeState.setFlowState(FlowState.TOPICKUP);
            tradeState.setPayState(PayState.PAID);
            String detail = String.format("订单[%s]已付款,待自提,操作人:%s", trade.getId(), operator.getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventTime(LocalDateTime.now())
                    .eventType("订单待自提")
                    .eventDetail(detail)
                    .build());
            save(trade);
            super.operationLogMq.convertAndSend(operator, "订单待自提", detail);
        }
    }
}
