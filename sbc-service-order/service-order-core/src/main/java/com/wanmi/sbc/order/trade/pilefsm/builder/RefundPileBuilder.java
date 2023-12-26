package com.wanmi.sbc.order.trade.pilefsm.builder;

import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.order.trade.pilefsm.action.CancelActionPile;
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
public class RefundPileBuilder implements PileBuilder {

    @Autowired
    private CancelActionPile cancelAction;

    @Override
    public FlowState supportState() {
        return FlowState.REFUND;
    }


    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("REFUND").beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(FlowState.REFUND)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()

                // 退款 -> [完成退款] -> 作废
                .withExternal()
                .source(FlowState.REFUND).target(FlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction);
        return builder.build();
    }
}
