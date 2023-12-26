package com.wanmi.sbc.order.returnorder.pilefsm.builder;

import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.pilefsm.action.DeliverReturnPileAction;
import com.wanmi.sbc.order.returnorder.pilefsm.action.RefundFailedPileAction;
import com.wanmi.sbc.order.returnorder.pilefsm.action.RefundRejectPileAction;
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
 * Created by jinwei on 21/4/2017.
 */
@Component
public class AuditReturnPileBuilder implements PileBuilder {

    @Autowired
    private DeliverReturnPileAction deliverReturnAction;

    @Autowired
    private RefundReturnPileAction refundReturnAction;

    @Autowired
    private RefundRejectPileAction refundRejectAction;

    @Autowired
    private RefundFailedPileAction refundFailedAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.AUDIT;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();
        builder.configureConfiguration()
                .withConfiguration()
                .machineId("AUDIT").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(ReturnFlowState.AUDIT)
                .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()

                // 已审核 -> [发货] -> 已发货
                .withExternal()
                .source(ReturnFlowState.AUDIT).target(ReturnFlowState.DELIVERED)
                .event(ReturnEvent.DELIVER)
                .action(deliverReturnAction)
                .and()

                .withExternal()
                .source(ReturnFlowState.AUDIT).target(ReturnFlowState.COMPLETED)
                .event(ReturnEvent.REFUND)
                .action(refundReturnAction)
                .and()

//                .withExternal()
//                .source(ReturnFlowState.AUDIT).target(ReturnFlowState.VOID)
//                .event(ReturnEvent.VOID)
//                .action(cancelReturnAction)
//                .and()

                .withExternal()
                .source(ReturnFlowState.AUDIT).target(ReturnFlowState.REJECT_REFUND)
                .event(ReturnEvent.REJECT_REFUND)
                .action(refundRejectAction)
                .and()

                // 已审核 -> [退款失败] -> 退款失败
                .withExternal()
                .source(ReturnFlowState.AUDIT).target(ReturnFlowState.REFUND_FAILED)
                .event(ReturnEvent.REFUND_FAILED)
                .action(refundFailedAction)

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
