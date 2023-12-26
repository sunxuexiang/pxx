package com.wanmi.sbc.returnorder.trade.newpilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.newpilefsm.NewPileBuilder;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.CancelActionNewPile;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.PickUpActionNewPile;
import com.wanmi.sbc.returnorder.trade.newpilefsm.action.RefundActionNewPile;
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
public class PileNewPileBuilder implements NewPileBuilder {

    @Autowired
    private CancelActionNewPile cancelAction;

    @Autowired
    private PickUpActionNewPile pickUpAction;

    @Autowired
    private RefundActionNewPile refundAction;


    @Override
    public NewPileFlowState supportState() {
        return NewPileFlowState.PILE;
    }

    @Override
    public StateMachine<NewPileFlowState, TradeEvent> build(NewPileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<NewPileFlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("PILE").beanFactory(beanFactory)
                .listener(listener())
        ;

        builder.configureStates()
                .withStates()
                .initial(NewPileFlowState.PILE)
                .states(EnumSet.allOf(NewPileFlowState.class))
        ;

        builder.configureTransitions()
                // 已囤货 -> [提货] -> 部分提货
                .withExternal()
                .source(NewPileFlowState.PILE).target(NewPileFlowState.PICK_PART)
                .event(TradeEvent.PICK_UP)
                .action(pickUpAction)
                .and()

                // 已囤货 -> [提货] -> 已完成
                .withExternal()
                .source(NewPileFlowState.PILE).target(NewPileFlowState.COMPLETED)
                .event(TradeEvent.COMPLETE)
                .action(pickUpAction)
                .and()

                // 已囤货 -> [退款] -> 作废
                .withExternal()
                .source(NewPileFlowState.PILE).target(NewPileFlowState.VOID)
                .event(TradeEvent.REFUND)
                .action(refundAction)
                .and()

                // 已囤货 -> [作废] -> 作废
                .withExternal()
                .source(NewPileFlowState.PILE).target(NewPileFlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction)
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
