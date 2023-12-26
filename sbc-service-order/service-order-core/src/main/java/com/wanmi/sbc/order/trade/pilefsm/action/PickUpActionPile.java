package com.wanmi.sbc.order.trade.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeAction;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>自提Action</p>
 * Created by of628-wenzhi on 2017-06-02-下午6:01.
 */

@Component
public class PickUpActionPile extends PileTradeAction {

    @Override
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
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
