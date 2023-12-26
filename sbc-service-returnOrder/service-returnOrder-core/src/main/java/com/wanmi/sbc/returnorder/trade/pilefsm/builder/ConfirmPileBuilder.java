package com.wanmi.sbc.returnorder.trade.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.CompleteActionPile;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Created by mac on 2017/4/7.
 */
@Component
public class ConfirmPileBuilder implements PileBuilder {

    @Autowired
    private CompleteActionPile completeAction;

    @Override
    public FlowState supportState() {
        return FlowState.CONFIRMED;
    }


    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("CONFIRMED").beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(FlowState.CONFIRMED)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()

                // 确认 -> 完成
                .withExternal()
                .source(FlowState.CONFIRMED).target(FlowState.COMPLETED)
                .event(TradeEvent.COMPLETE)
                .action(completeAction);

        return builder.build();
    }
}
