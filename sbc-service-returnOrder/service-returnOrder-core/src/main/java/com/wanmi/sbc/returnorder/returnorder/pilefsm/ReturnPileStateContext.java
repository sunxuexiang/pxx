package com.wanmi.sbc.returnorder.returnorder.pilefsm;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

/**
 * 退货状态上下文管理工具
 * Created by jinwei on 21/4/2017.
 */
public class ReturnPileStateContext {

    private StateContext<ReturnFlowState, ReturnEvent> stateContext;

    public ReturnPileStateContext(StateContext<ReturnFlowState, ReturnEvent> stateContext) {
        this.stateContext = stateContext;
    }

    /**
     * 传递参数
     * @param key
     * @param value
     * @return
     */
    public ReturnPileStateContext put(Object key, Object value){
        stateContext.getExtendedState().getVariables().put(key, value);
        return this;
    }

    public ReturnPileOrder findReturnPileOrder(){
        return this.stateContext.getExtendedState().get(ReturnPileOrder.class, ReturnPileOrder.class);
    }

    public ReturnStateRequest findRequest(){
        return this.stateContext.getExtendedState().get(ReturnStateRequest.class, ReturnStateRequest.class);
    }

    public Operator findOperator() {
        return findRequest().getOperator();
    }


    public <T> T findRequestData() {
        return (T) findRequest().getData();
    }

    public StateMachine<ReturnFlowState, ReturnEvent> findStateMachine(){
        return this.stateContext.getStateMachine();
    }

    public StateContext<ReturnFlowState, ReturnEvent> findStateContext(){
        return stateContext;
    }
}
