package com.wanmi.sbc.returnorder.trade.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.CancelActionPile;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.CompleteActionPile;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.ConfirmActionPile;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.RefundActionPile;
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
public class ToPickUpPileBuilder implements PileBuilder {

    @Autowired
    private ConfirmActionPile confirmAction;

    @Autowired
    private CompleteActionPile completeAction;

    @Autowired
    private RefundActionPile refundAction;

    @Autowired
    private CancelActionPile cancelAction;


    @Override
    public FlowState supportState() {
        return FlowState.TOPICKUP;
    }

    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
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
