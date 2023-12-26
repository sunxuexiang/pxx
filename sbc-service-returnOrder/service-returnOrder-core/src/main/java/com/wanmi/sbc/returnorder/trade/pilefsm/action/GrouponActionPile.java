package com.wanmi.sbc.returnorder.trade.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileTradeAction;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileTradeStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>成团后的订单状态流转与操作日志处理</p>
 * Created by of628-wenzhi on 2019-05-24-17:28.
 */

@Component
public class GrouponActionPile extends PileTradeAction {

    private static final String LOG_DETAIL = "拼团订单[%s]成功完成拼团，团号[%s]";

    @Override
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
        //待成团（已参团）-> 待发货（已审核）
        trade.getTradeState().setFlowState(FlowState.AUDIT);
        Operator operator = tsc.getOperator();
        String detail = String.format(LOG_DETAIL, trade.getId(), trade.getTradeGroupon().getGrouponNo());
        trade.appendTradeEventLog(TradeEventLog.builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(TradeEvent.JOIN_GROUPON.getDescription())
                .eventDetail(detail)
                .build()
        );
        trade.getTradeGroupon().setGrouponOrderStatus(GrouponOrderStatus.COMPLETE);
        trade.getTradeGroupon().setGrouponSuccessTime(LocalDateTime.now());
        save(trade);
        super.operationLogMq.convertAndSend(operator, TradeEvent.JOIN_GROUPON.getDescription(), detail);
    }

}
