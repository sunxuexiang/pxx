package com.wanmi.sbc.order.trade.pilefsm;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

/**
 * Created by Administrator on 2017/4/19.
 */
public class PileTradeStateContext {

    private StateContext<FlowState, TradeEvent> stateContext;

    public PileTradeStateContext(StateContext<FlowState, TradeEvent> stateContext) {
        this.stateContext = stateContext;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public PileTradeStateContext put(Object key, Object value) {
        stateContext.getExtendedState().getVariables().put(key, value);
        return this;
    }

    /**
     * @return
     */
    public PileTrade getTrade() {
        return this.stateContext.getExtendedState().get(PileTrade.class, PileTrade.class);
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
    public StateMachine<FlowState, TradeEvent> getStateMachine() {
        return this.stateContext.getStateMachine();
    }

    /**
     * @return
     */
    public StateContext<FlowState, TradeEvent> getStateContext() {
        return stateContext;
    }
}
