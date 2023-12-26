package com.wanmi.sbc.order.returnorder.pilefsm.builder;

import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.pilefsm.action.CloseRefundPileAction;
import com.wanmi.sbc.order.returnorder.pilefsm.action.RefundReturnPileAction;
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
 * 关闭退款
 */
@Component
public class CloseRefundPileBuilder implements PileBuilder {

    @Autowired
    private CloseRefundPileAction closeRefundAction;

    @Autowired
    private RefundReturnPileAction refundReturnAction;


    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.REFUND_FAILED;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
            .withConfiguration()
            .machineId("REFUND_FAILED").beanFactory(beanFactory)
            .listener(listener());

        builder.configureStates()
            .withStates()
            .initial(ReturnFlowState.REFUND_FAILED)
            .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()
            // 退款失败 -> [关闭退款]  -> 已完成
            .withExternal()
            .source(ReturnFlowState.REFUND_FAILED).target(ReturnFlowState.COMPLETED)
            .event(ReturnEvent.CLOSE_REFUND)
            .action(closeRefundAction)

            // 退款失败 -> [退款] -> 已完成
            .and()
            .withExternal()
            .source(ReturnFlowState.REFUND_FAILED).target(ReturnFlowState.COMPLETED)
            .event(ReturnEvent.REFUND)
            .action(refundReturnAction)
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
