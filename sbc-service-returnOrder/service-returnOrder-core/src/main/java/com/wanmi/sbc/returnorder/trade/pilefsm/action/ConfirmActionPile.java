package com.wanmi.sbc.returnorder.trade.pilefsm.action;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileTradeAction;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileTradeStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class ConfirmActionPile extends PileTradeAction {

    @Override
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
        TradeState tradeState = trade.getTradeState();

        if (!tradeState.getDeliverStatus().equals(DeliverStatus.SHIPPED)&&!trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
            throw new SbcRuntimeException("K-050107");
        }

        //
        tradeState.setFlowState(FlowState.CONFIRMED);

        //
        String detail = String.format("订单%s已确认收货", trade.getId());
        if (trade.getDeliverWay().equals(DeliverWay.PICK_SELF)){
            tradeState.setDeliverStatus(DeliverStatus.SHIPPED);
            trade.getTradeItems().forEach(param->param.setDeliveredNum(param.getNum()));
            trade.getGifts().forEach(param->{ param.setDeliveredNum(param.getNum()); });
            detail = String.format("订单%s已自提", trade.getId());
        }
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(tsc.getOperator())
                .eventType(FlowState.CONFIRMED.getDescription())
                .eventDetail(detail)
                .eventTime(LocalDateTime.now())
                .build());
        save(trade);
        super.operationLogMq.convertAndSend(tsc.getOperator(), FlowState.COMPLETED.getDescription(), detail);
    }
}
