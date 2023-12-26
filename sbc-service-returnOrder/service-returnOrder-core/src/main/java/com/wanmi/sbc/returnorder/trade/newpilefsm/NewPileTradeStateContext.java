package com.wanmi.sbc.returnorder.trade.newpilefsm;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

/**
 * Created by Administrator on 2017/4/19.
 */
public class NewPileTradeStateContext {

    private StateContext<NewPileFlowState, TradeEvent> stateContext;

    public NewPileTradeStateContext(StateContext<NewPileFlowState, TradeEvent> stateContext) {
        this.stateContext = stateContext;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public NewPileTradeStateContext put(Object key, Object value) {
        stateContext.getExtendedState().getVariables().put(key, value);
        return this;
    }

    /**
     * @return
     */
    public NewPileTrade getTrade() {
        return this.stateContext.getExtendedState().get(NewPileTrade.class, NewPileTrade.class);
    }

    /**
     * @return
     */
    public StateRequest getRequest() {
        return this.stateContext.getExtendedState().get(StateRequest.class, StateRequest.class);
    }

    /**
     * @return
     */
    public Operator getOperator() {
        return getRequest().getOperator();
    }

    /**
     * @param <T>
     * @return
     */
    public <T> T getRequestData() {
        return (T) getRequest().getData();
    }

    /**
     * @return
     */
    public StateMachine<NewPileFlowState, TradeEvent> getStateMachine() {
        return this.stateContext.getStateMachine();
    }

    /**
     * @return
     */
    public StateContext<NewPileFlowState, TradeEvent> getStateContext() {
        return stateContext;
    }
}
