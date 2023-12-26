package com.wanmi.sbc.returnorder.returnorder.newpilefsm;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.event.NewPileReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

/**
 * 退货状态上下文管理工具
 * Created by jinwei on 21/4/2017.
 */
public class NewPileReturnStateContext {

    private StateContext<NewPileReturnFlowState, NewPileReturnEvent> stateContext;

    public NewPileReturnStateContext(StateContext<NewPileReturnFlowState, NewPileReturnEvent> stateContext) {
        this.stateContext = stateContext;
    }

    /**
     * 传递参数
     * @param key
     * @param value
     * @return
     */
    public NewPileReturnStateContext put(Object key, Object value){
        stateContext.getExtendedState().getVariables().put(key, value);
        return this;
    }

    public NewPileReturnOrder findReturnOrder(){
        return this.stateContext.getExtendedState().get(NewPileReturnOrder.class, NewPileReturnOrder.class);
    }

    public NewPileReturnStateRequest findRequest(){
        return this.stateContext.getExtendedState().get(NewPileReturnStateRequest.class, NewPileReturnStateRequest.class);
    }

    public Operator findOperator() {
        return findRequest().getOperator();
    }


    public <T> T findRequestData() {
        return (T) findRequest().getData();
    }

    public StateMachine<NewPileReturnFlowState, NewPileReturnEvent> findStateMachine(){
        return this.stateContext.getStateMachine();
    }

    public StateContext<NewPileReturnFlowState, NewPileReturnEvent> findStateContext(){
        return stateContext;
    }
}
