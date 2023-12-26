package com.wanmi.sbc.order.trade.newpilefsm.builder;

import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.newpilefsm.NewPileBuilder;
import com.wanmi.sbc.order.trade.newpilefsm.action.CancelActionNewPile;
import com.wanmi.sbc.order.trade.newpilefsm.action.PickUpActionNewPile;
import com.wanmi.sbc.order.trade.newpilefsm.action.RefundActionNewPile;
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
public class PickPartNewPileBuilder implements NewPileBuilder {

    @Autowired
    private PickUpActionNewPile pickUpAction;

    @Autowired
    private CancelActionNewPile cancelAction;

    @Autowired
    private RefundActionNewPile refundAction;

    @Override
    public NewPileFlowState supportState() {
        return NewPileFlowState.PICK_PART;
    }

    @Override
    public StateMachine<NewPileFlowState, TradeEvent> build(NewPileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<NewPileFlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("PICK_PART").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(NewPileFlowState.PICK_PART)
                .states(EnumSet.allOf(NewPileFlowState.class))
        ;

        builder.configureTransitions()
                // 部分提货 -> [退款] -> 部分提货
                .withExternal()
                .source(NewPileFlowState.PICK_PART).target(NewPileFlowState.PICK_PART)
                .event(TradeEvent.REFUND)
                .action(refundAction)
                .and()

                // 部分提货 -> [退款] -> 待提货
                .withExternal()
                .source(NewPileFlowState.PICK_PART).target(NewPileFlowState.PILE)
                .event(TradeEvent.REFUND)
                .action(refundAction)
                .and()

                // 部分提货 -> [提货] -> 部分提货
                .withExternal()
                .source(NewPileFlowState.PICK_PART).target(NewPileFlowState.PICK_PART)
                .event(TradeEvent.PICK_UP)
                .action(pickUpAction)
                .and()

                // 部分提货 -> [提货] -> 已完成
                .withExternal()
                .source(NewPileFlowState.PICK_PART).target(NewPileFlowState.COMPLETED)
                .event(TradeEvent.COMPLETE)
                .action(pickUpAction)
                .and()

                // 部分提货 -> [退款] -> 已完成
                .withExternal()
                .source(NewPileFlowState.PICK_PART).target(NewPileFlowState.COMPLETED)
                .event(TradeEvent.REFUND)
                .action(refundAction)
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
