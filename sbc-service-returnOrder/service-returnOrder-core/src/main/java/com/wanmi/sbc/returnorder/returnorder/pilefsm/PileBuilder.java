package com.wanmi.sbc.returnorder.returnorder.pilefsm;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;

/**
 * @author marsjiang
 * @date 20210928
 */
public interface PileBuilder {

    /**
     *
     * @return
     */
    ReturnFlowState supportState();

    /**
     *
     * @param returnPileOrder
     * @return
     */
    StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnPileOrder, BeanFactory beanFactory) throws Exception;
}
