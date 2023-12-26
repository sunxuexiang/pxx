package com.wanmi.sbc.returnorder.returnorder.newpilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.NewPileBuilder;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.action.CancelReturnNewPileAction;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.action.CompleteReturnNewPileAction;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.event.NewPileReturnEvent;
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
public class WaitRefundReturnBuilder implements NewPileBuilder {
    @Autowired
    private CancelReturnNewPileAction cancelReturnAction;

    @Autowired
    private CompleteReturnNewPileAction completeReturnAction;


    @Override
    public NewPileReturnFlowState supportState() {
        return NewPileReturnFlowState.AUDIT;
    }

    @Override
    public StateMachine<NewPileReturnFlowState, NewPileReturnEvent> build(NewPileReturnOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<NewPileReturnFlowState, NewPileReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("AUDIT").beanFactory(beanFactory)
                .listener(listener());

        builder.configureStates()
                .withStates()
                .initial(NewPileReturnFlowState.AUDIT)
                .states(EnumSet.allOf(NewPileReturnFlowState.class));

        builder.configureTransitions()
                // 待退款 -> [退款] -> 已完成
                .withInternal()
                .source(NewPileReturnFlowState.AUDIT)
                .event(NewPileReturnEvent.REFUND)
                .action(completeReturnAction)
                .and()
                // 待退款 -> [取消] -> 已作废
                .withExternal()
                .source(NewPileReturnFlowState.AUDIT).target(NewPileReturnFlowState.VOID)
                .event(NewPileReturnEvent.VOID)
                .action(cancelReturnAction);


        return builder.build();
    }

    public StateMachineListener<NewPileReturnFlowState, NewPileReturnEvent> listener() {
        return new StateMachineListenerAdapter<NewPileReturnFlowState, NewPileReturnEvent>() {
            @Override
            public void stateChanged(State<NewPileReturnFlowState, NewPileReturnEvent> from, State<NewPileReturnFlowState, NewPileReturnEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }

}
