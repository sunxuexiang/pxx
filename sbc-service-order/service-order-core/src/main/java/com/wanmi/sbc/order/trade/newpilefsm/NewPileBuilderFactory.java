package com.wanmi.sbc.order.trade.newpilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NewPileBuilderFactory implements InitializingBean {

    /**
     *
     */
    private Map<NewPileFlowState, NewPileBuilder> stateBuilderMap = new ConcurrentHashMap<>();

    /**
     *
     */
    @Autowired
    private List<NewPileBuilder> stateBuilders;

    /**
     *
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * @param trade
     * @return
     */
    public StateMachine<NewPileFlowState, TradeEvent> create(NewPileTrade trade) {
        NewPileFlowState flowState = trade.getTradeState().getFlowState();
        NewPileBuilder builder = stateBuilderMap.get(flowState);
        if (builder == null) {
            throw new SbcRuntimeException("K-050102");
        }
        //
        StateMachine<NewPileFlowState, TradeEvent> sm;
        try {
            sm = builder.build(trade, beanFactory);
            sm.start();
        } catch (Exception e) {
            throw new SbcRuntimeException("K-050102");
        }
        //
        sm.getExtendedState().getVariables().put(NewPileTrade.class, trade);
        return sm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        stateBuilderMap = stateBuilders.stream().collect(Collectors.toMap(NewPileBuilder::supportState, Function.identity()));
    }
}
