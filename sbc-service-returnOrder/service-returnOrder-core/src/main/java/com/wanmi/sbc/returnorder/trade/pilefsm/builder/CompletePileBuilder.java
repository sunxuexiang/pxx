package com.wanmi.sbc.returnorder.trade.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.CancelActionPile;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.ObsoletePayActionPile;
import com.wanmi.sbc.returnorder.trade.pilefsm.action.RefundActionPile;
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
public class CompletePileBuilder implements PileBuilder {

    @Autowired
    private RefundActionPile refundAction;

    @Autowired
    private CancelActionPile cancelAction;

    @Autowired
    private ObsoletePayActionPile obsoletePayAction;

    @Override
    public FlowState supportState() {
        return FlowState.COMPLETED;
    }


    @Override
    public StateMachine<FlowState, TradeEvent> build(PileTrade trade, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<FlowState, TradeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId("COMPLETED").beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(FlowState.COMPLETED)
                .states(EnumSet.allOf(FlowState.class));

        builder.configureTransitions()

                // 完成 -> [申请退货] -> 退货
                .withExternal()
                .source(FlowState.COMPLETED).target(FlowState.COMPLETED)
                .event(TradeEvent.REFUND)
                .action(refundAction)

                // 完成 -> [申请退货] -> 退货
                .and()
                .withExternal()
                .source(FlowState.COMPLETED).target(FlowState.VOID)
                .event(TradeEvent.VOID)
                .action(cancelAction)

                .and()
                .withExternal()
                .source(FlowState.COMPLETED).target(FlowState.CONFIRMED)
                .event(TradeEvent.OBSOLETE_PAY)
                .action(obsoletePayAction);
        return builder.build();
    }
}
