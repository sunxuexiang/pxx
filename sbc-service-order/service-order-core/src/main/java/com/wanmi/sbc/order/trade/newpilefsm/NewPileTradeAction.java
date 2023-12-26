package com.wanmi.sbc.order.trade.newpilefsm;

import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.common.OperationLogMq;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * Created by Administrator on 2017/4/19.
 */
public abstract class NewPileTradeAction implements Action<NewPileFlowState, TradeEvent> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected OperationLogMq operationLogMq;

    @Autowired
    protected NewPileTradeService newPileTradeService;


    @Override
    public void execute(StateContext<NewPileFlowState, TradeEvent> stateContext) {
        NewPileTradeStateContext tsc = new NewPileTradeStateContext(stateContext);
        try {
            evaluateInternal(tsc.getTrade(), tsc.getRequest(), tsc);
        } catch (Exception e) {
            tsc.put(Exception.class, e);
            e.printStackTrace();
            logger.error(String.format("处理, 从状态[ %s ], 经过事件[ %s ], 到状态[ %s ], 出现异常[ %s ]", stateContext.getSource().getId(), stateContext.getEvent(), stateContext.getTarget().getId(), e));
        }
    }

    /**
     * @param trade
     */
    protected void save(NewPileTrade trade) {
        newPileTradeService.updateTrade(trade);
    }


    protected abstract void evaluateInternal(NewPileTrade trade, StateRequest request, NewPileTradeStateContext tsc);
}
