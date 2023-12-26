package com.wanmi.sbc.returnorder.returnorder.fsm;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;

/**
 *
 * Created by mac on 2017/4/7.
 */
public interface Builder {

    /**
     *
     * @return
     */
    ReturnFlowState supportState();

    /**
     *
     * @param returnOrder
     * @return
     */
    StateMachine<ReturnFlowState, ReturnEvent> build(ReturnOrder returnOrder, BeanFactory beanFactory) throws Exception;
}
