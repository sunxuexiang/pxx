package com.wanmi.sbc.returnorder.trade.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.*;
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
 * Created by mac on 2017/4/7.
 */
@Component
public class AuditPileBuilder implements PileBuilder {

    @Autowired
    private AuditActionPile auditAction;

    @Autowired
    private DeliverActionPile deliverAction;

    @Autowired
    private RefundActionPile refundAction;

    @Autowired
    private ReAuditActionPile reAuditAction;

    @Autowired
    private CancelActionPile cancelAction;

    @Autowired
    private RemedyActionPile remedyAction;

    @Autowired
    private PickUpActionPile pickUpAction;

    @Override
    public FlowState supportState() {
        return FlowState.AUDIT;
    }

    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("AUDIT").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(FlowState.AUDIT)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()

                // 审核 -> [退款] -> 退款
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.AUDIT)
                .event(TradeEvent.REFUND)
                .action(refundAction)
                .and()

                // 审核 -> [审核驳回] -> 作废
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction)
                .and()

                // 审核 -> [回审] -> 审核
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.AUDIT)
                .event(TradeEvent.AUDIT)
                .action(auditAction)
                .and()

                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.INIT)
                .event(TradeEvent.RE_AUDIT)
                .action(reAuditAction)
                .and()

                // 审核 -> [修改] -> 审核
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.AUDIT)
                .event(TradeEvent.REMEDY)
                .action(remedyAction)
                .and()

                // 审核 -> [发货] -> 部分发货
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.DELIVERED_PART)
                .event(TradeEvent.DELIVER)
                .action(deliverAction)
                .and()

                // 审核 -> [付款] -> 待自提
                .withExternal()
                .source(FlowState.AUDIT).target(FlowState.TOPICKUP)
                .event(TradeEvent.PAY)
                .action(pickUpAction)
                .and()
        ;

        return builder.build();
    }

    public StateMachineListener<FlowState, TradeEvent> listener() {
        return new StateMachineListenerAdapter<FlowState, TradeEvent>() {
            @Override
            public void stateChanged(State<FlowState, TradeEvent> from, State<FlowState, TradeEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }
}
