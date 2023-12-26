package com.wanmi.sbc.order.trade.pilefsm.builder;

import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.order.trade.pilefsm.action.DeliverActionPile;
import com.wanmi.sbc.order.trade.pilefsm.action.ObsoleteDeliverActionPile;
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
public class DeliveredPartPileBuilder implements PileBuilder {

    @Autowired
    private DeliverActionPile deliverAction;


    @Autowired
    private ObsoleteDeliverActionPile obsoleteDeliverAction;

    @Override
    public FlowState supportState() {
        return FlowState.DELIVERED_PART;
    }

    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("DeliveredPart").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(FlowState.DELIVERED_PART)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()

                // 部分发货 -> [发货] -> 发货
                .withExternal()
                .source(FlowState.DELIVERED_PART).target(FlowState.DELIVERED)
                .event(TradeEvent.DELIVER)
                .action(deliverAction)
                .and()

                // 部分发货 -> [作废] -> 审核
                .withExternal()
                .source(FlowState.DELIVERED_PART).target(FlowState.AUDIT)
                .event(TradeEvent.OBSOLETE_DELIVER)
                .action(obsoleteDeliverAction)

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
