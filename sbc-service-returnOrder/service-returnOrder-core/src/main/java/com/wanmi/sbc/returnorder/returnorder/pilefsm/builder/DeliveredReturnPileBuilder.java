package com.wanmi.sbc.returnorder.returnorder.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.ReceiveRejectPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.ReceiveReturnPileAction;
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
 * 已退货状态机
 * Created by jinwei on 22/4/2017.
 */
@Component
public class DeliveredReturnPileBuilder implements PileBuilder {

    @Autowired
    private ReceiveReturnPileAction receiveReturnAction;

    @Autowired
    private ReceiveRejectPileAction receiveRejectAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.DELIVERED;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
            .withConfiguration()
            .machineId("DELIVERED").beanFactory(beanFactory)
            .listener(listener());

        builder.configureStates()
            .withStates()
            .initial(ReturnFlowState.DELIVERED)
            .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()
            // 已发货 -> [D收货] -> 已收货
            .withExternal()
            .source(ReturnFlowState.DELIVERED).target(ReturnFlowState.RECEIVED)
            .event(ReturnEvent.RECEIVE)
            .action(receiveReturnAction)
            // 已发货 -> [D拒绝收货] -> 已作废
            .and()
            .withExternal()
            .source(ReturnFlowState.DELIVERED).target(ReturnFlowState.REJECT_RECEIVE)
            .event(ReturnEvent.REJECT_RECEIVE)
            .action(receiveRejectAction)
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
