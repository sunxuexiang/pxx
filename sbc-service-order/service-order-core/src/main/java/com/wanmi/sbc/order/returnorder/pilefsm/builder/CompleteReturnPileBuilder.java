package com.wanmi.sbc.order.returnorder.pilefsm.builder;

import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.pilefsm.action.AuditReturnPileAction;
import com.wanmi.sbc.order.returnorder.pilefsm.action.ReceiveReturnPileAction;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Created by jinwei on 21/4/2017.
 */
@Component
public class CompleteReturnPileBuilder implements PileBuilder {

    @Autowired
    private ReceiveReturnPileAction receiveReturnAction;

    @Autowired
    private AuditReturnPileAction auditReturnAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.COMPLETED;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();
        builder.configureConfiguration()
            .withConfiguration()
            .machineId("COMPLETED").beanFactory(beanFactory)
            .listener(listener())
        ;

        builder.configureStates()
            .withStates()
            .initial(ReturnFlowState.COMPLETED)
            .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()

            // 已完成 -> 已审核
            .withExternal()
            .source(ReturnFlowState.COMPLETED).target(ReturnFlowState.AUDIT)
            .event(ReturnEvent.REVERSE_REFUND)
            .action(auditReturnAction)
            .and()

            .withExternal()
            .source(ReturnFlowState.COMPLETED).target(ReturnFlowState.RECEIVED)
            .event(ReturnEvent.REVERSE_RETURN)
            .action(receiveReturnAction)
        ;

        return builder.build();
    }

    public StateMachineListener<ReturnFlowState, ReturnEvent> listener() {
        return new StateMachineListenerAdapter<ReturnFlowState, ReturnEvent>() {
            @Override
            public void stateChanged(State<ReturnFlowState, ReturnEvent> from, State<ReturnFlowState, ReturnEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }
}
