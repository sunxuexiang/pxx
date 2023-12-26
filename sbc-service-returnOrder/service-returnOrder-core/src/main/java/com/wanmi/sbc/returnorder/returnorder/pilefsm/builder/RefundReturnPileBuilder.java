package com.wanmi.sbc.returnorder.returnorder.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.CompleteReturnPileAction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * @Description: 已退款状态机
 * @author: jiangxin
 * @create: 2021-09-28 16:55
 */
public class RefundReturnPileBuilder implements PileBuilder {

    @Autowired
    private CompleteReturnPileAction completeReturnAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.REFUNDED;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("REFUNDED").beanFactory(beanFactory)
                .listener(listener());

        builder.configureStates()
                .withStates()
                .initial(ReturnFlowState.REFUNDED)
                .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()
                // 已退款 -> 已完成
                .withExternal()
                .source(ReturnFlowState.REFUNDED).target(ReturnFlowState.COMPLETED)
                .event(ReturnEvent.COMPLETE)
                .action(completeReturnAction)
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
