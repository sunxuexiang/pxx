package com.wanmi.sbc.returnorder.returnorder.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.CompleteReturnPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.RefundFailedPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.RefundRejectPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.RefundReturnPileAction;
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
 * 确认收到退货
 * Created by jinwei on 22/4/2017.
 */
@Component
public class ReceiveReturnPileBuilder implements PileBuilder {

    @Autowired
    private RefundReturnPileAction refundReturnAction;

    @Autowired
    private RefundRejectPileAction refundRejectAction;

    @Autowired
    private RefundFailedPileAction refundFailedAction;

    @Autowired
    private CompleteReturnPileAction completeReturnAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.RECEIVED;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
            .withConfiguration()
            .machineId("RECEIVED").beanFactory(beanFactory)
            .listener(listener());

        builder.configureStates()
            .withStates()
            .initial(ReturnFlowState.RECEIVED)
            .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()
            // 已收货 -> [D退款]  -> 已完成
            .withExternal()
            .source(ReturnFlowState.RECEIVED).target(ReturnFlowState.COMPLETED)
            .event(ReturnEvent.REFUND)
            .action(refundReturnAction)

            // 已收货 -> [退款失败事件] -> 退款失败状态
            .and()
            .withExternal()
            .source(ReturnFlowState.RECEIVED).target(ReturnFlowState.REFUND_FAILED)
            .event(ReturnEvent.REFUND_FAILED)
            .action(refundFailedAction)

            // 已收货 -> [D拒绝退款] -> 已完成
            .and()
            .withExternal()
            .source(ReturnFlowState.RECEIVED).target(ReturnFlowState.REJECT_REFUND)
            .event(ReturnEvent.REJECT_REFUND)
            .action(refundRejectAction)

            // 已收货 -> 已完成
            .and()
            .withExternal()
            .source(ReturnFlowState.RECEIVED).target(ReturnFlowState.COMPLETED)
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
