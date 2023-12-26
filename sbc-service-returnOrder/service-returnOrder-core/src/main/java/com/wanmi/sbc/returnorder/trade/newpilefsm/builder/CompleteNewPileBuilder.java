package com.wanmi.sbc.returnorder.trade.newpilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.newpilefsm.NewPileBuilder;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.RefundActionNewPile;
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
public class CompleteNewPileBuilder implements NewPileBuilder {

    @Autowired
    private RefundActionNewPile refundAction;

    @Override
    public NewPileFlowState supportState() {
        return NewPileFlowState.COMPLETED;
    }


    @Override
    public StateMachine<NewPileFlowState, TradeEvent> build(NewPileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<NewPileFlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("COMPLETED").beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(NewPileFlowState.COMPLETED)
                .states(EnumSet.allOf(NewPileFlowState.class));

        builder.configureTransitions()
                // 完成 -> [退款] -> 完成
                .withExternal()
                .source(NewPileFlowState.COMPLETED).target(NewPileFlowState.COMPLETED)
                .event(TradeEvent.REFUND)
                .action(refundAction)

                // 完成 -> [退款] -> 已作废
                .and()
                .withExternal()
                .source(NewPileFlowState.COMPLETED).target(NewPileFlowState.VOID)
                .event(TradeEvent.VOID)
                .action(refundAction);
        return builder.build();
    }
}
