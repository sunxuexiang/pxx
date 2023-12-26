package com.wanmi.sbc.returnorder.trade.newpilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.newpilefsm.NewPileBuilder;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.CancelActionNewPile;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.PayActionNewPile;
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
public class InitNewPileBuilder implements NewPileBuilder {

    @Autowired
    private CancelActionNewPile cancelAction;

    @Autowired
    private PayActionNewPile payAction;

    @Override
    public NewPileFlowState supportState() {
        return NewPileFlowState.INIT;
    }

    @Override
    public StateMachine<NewPileFlowState, TradeEvent> build(NewPileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<NewPileFlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("INIT").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(NewPileFlowState.INIT)
                .states(EnumSet.allOf(NewPileFlowState.class))
        ;

        builder.configureTransitions()
                // 新建 -> [取消] -> 作废
                .withExternal()
                .source(NewPileFlowState.INIT).target(NewPileFlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction)
                .and()
                // 新建 -> [支付] -> 已囤货
                .withExternal()
                .source(NewPileFlowState.INIT).target(NewPileFlowState.PILE)
                .event(TradeEvent.PAY)
                .action(payAction)
                .and()
        ;

        return builder.build();
    }

    public StateMachineListener<NewPileFlowState, TradeEvent> listener() {
        return new StateMachineListenerAdapter<NewPileFlowState, TradeEvent>() {
            @Override
            public void stateChanged(State<NewPileFlowState, TradeEvent> from, State<NewPileFlowState, TradeEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }
}
