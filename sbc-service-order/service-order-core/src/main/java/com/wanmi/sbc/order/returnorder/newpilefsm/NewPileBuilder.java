package com.wanmi.sbc.order.returnorder.newpilefsm;

import com.wanmi.sbc.order.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.newpilefsm.event.NewPileReturnEvent;
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
