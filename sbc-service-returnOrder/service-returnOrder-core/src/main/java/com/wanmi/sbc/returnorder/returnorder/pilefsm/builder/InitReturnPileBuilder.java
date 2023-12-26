package com.wanmi.sbc.returnorder.returnorder.pilefsm.builder;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.PileBuilder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.AuditReturnPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.CancelReturnPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.action.RemedyReturnPileAction;
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
 *
 * Created by jinwei on 21/4/2017.
 */
@Component
public class InitReturnPileBuilder implements PileBuilder {

    @Autowired
    private RemedyReturnPileAction remedyReturnAction;

    @Autowired
    private AuditReturnPileAction auditReturnAction;

    @Autowired
    private CancelReturnPileAction cancelReturnAction;

    @Override
    public ReturnFlowState supportState() {
        return ReturnFlowState.INIT;
    }

    @Override
    public StateMachine<ReturnFlowState, ReturnEvent> build(ReturnPileOrder returnOrder, BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ReturnFlowState, ReturnEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
            .withConfiguration()
            .machineId("INIT").beanFactory(beanFactory)
            .listener(listener());

        builder.configureStates()
            .withStates()
            .initial(ReturnFlowState.INIT)
            .states(EnumSet.allOf(ReturnFlowState.class));

        builder.configureTransitions()
            // 已新建 -> [修改] -> 已新建
            .withInternal()
            .source(ReturnFlowState.INIT)
            .event(ReturnEvent.REMEDY)
            .action(remedyReturnAction)
            .and()
            // 已新建 -> 已审核
            .withExternal()
            .source(ReturnFlowState.INIT).target(ReturnFlowState.AUDIT)
            .event(ReturnEvent.AUDIT)
            .action(auditReturnAction)
            .and()
            // 已新建 -> [取消] -> 已作废
            .withExternal()
            .source(ReturnFlowState.INIT).target(ReturnFlowState.VOID)
            .event(ReturnEvent.VOID)
            .action(cancelReturnAction);



        return builder.build();
    }

    public StateMachineListener<ReturnFlowState, ReturnEvent> listener() {
        return new StateMachineListenerAdapter<ReturnFlowState, ReturnEvent>() {
            @Override
            public void stateChanged(State<ReturnFlowState, ReturnEvent> from, State<ReturnFlowState, ReturnEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }

}
