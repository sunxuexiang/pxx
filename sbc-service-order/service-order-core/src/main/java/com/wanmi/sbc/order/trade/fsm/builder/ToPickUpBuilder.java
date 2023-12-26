package com.wanmi.sbc.order.trade.fsm.builder;

import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.trade.fsm.Builder;
import com.wanmi.sbc.order.trade.fsm.action.*;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.root.Trade;
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
public class ToPickUpBuilder implements Builder {

    @Autowired
    private ConfirmAction confirmAction;

    @Autowired
    private CompleteAction completeAction;

    @Autowired
    private RefundAction refundAction;

    @Autowired
    private CancelAction cancelAction;


    @Override
    public FlowState supportState() {
        return FlowState.TOPICKUP;
    }

    @Override
    public StateMachine<FlowState, TradeEvent> build(Trade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("TOPICKUP").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(FlowState.TOPICKUP)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()
                // 待自提 -> [自提] -> 已确认
                .withExternal()
                .source(FlowState.TOPICKUP).target(FlowState.CONFIRMED)
                .event(TradeEvent.PICK_UP)
                .action(confirmAction)
                .and()

                // 待自提 -> [退款] -> 退款
                .withExternal()
                .source(FlowState.TOPICKUP).target(FlowState.AUDIT)
                .event(TradeEvent.REFUND)
                .action(refundAction)
                .and()

                // 待自提 -> [自提] -> 已完成
                .withExternal()
                .source(FlowState.TOPICKUP).target(FlowState.COMPLETED)
                .event(TradeEvent.COMPLETE)
                .action(completeAction)
                .and()

                // 待自提 -> [完成退款] -> 作废
                .withExternal()
                .source(FlowState.TOPICKUP).target(FlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction);



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
