package com.wanmi.sbc.returnorder.trade.pilefsm.action;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
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
public class RefundActionPile extends PileTradeAction {

    @Override
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
        TradeState tradeState = trade.getTradeState();

        // 已完成的退款操作
        if (tradeState.getFlowState().equals(FlowState.COMPLETED)) {
            String detail = String.format("订单[%s],申请退货,操作人:%s", trade.getId(), tsc.getOperator().getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(tsc.getOperator())
                    .eventType(TradeEvent.REFUND.getDescription())
                    .eventDetail(detail)
                    .eventTime(LocalDateTime.now())
                    .build());
            save(trade);
            super.operationLogMq.convertAndSend(tsc.getOperator(), TradeEvent.REFUND.getDescription(), detail);
        }

        //
        else {
            // 判断已支付
            if (!tradeState.getPayState().equals(PayState.PAID)) {
                throw new SbcRuntimeException("K-050105");
            }

            // 判断已发货
            if (!tradeState.getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                throw new SbcRuntimeException("K-050106");
            }
            String detail = String.format("订单%s,申请退款,操作人:%s", trade.getId(), tsc.getOperator().getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(tsc.getOperator())
                    .eventType(TradeEvent.REFUND.getDescription())
                    .eventDetail(detail)
                    .eventTime(LocalDateTime.now())
                    .build());
            save(trade);
            super.operationLogMq.convertAndSend(tsc.getOperator(), TradeEvent.REFUND.getDescription(), detail);
        }

    }

}
