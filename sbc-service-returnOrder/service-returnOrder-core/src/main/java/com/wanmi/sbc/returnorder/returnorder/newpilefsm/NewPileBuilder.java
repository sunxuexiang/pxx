package com.wanmi.sbc.returnorder.returnorder.newpilefsm;

import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.event.NewPileReturnEvent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;

/**
 *
 */
public interface NewPileBuilder {

    /**
     *
     * @return
     */
    NewPileReturnFlowState supportState();

    /**
     *
     * @param returnOrder
     * @return
     */
    StateMachine<NewPileReturnFlowState, NewPileReturnEvent> build(NewPileReturnOrder returnOrder, BeanFactory beanFactory) throws Exception;
}
