package com.wanmi.sbc.returnorder.trade.pilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Just for build each state builder
 * Created by mac on 2017/4/7.
 */
@Component
public class PileBuilderFactory implements InitializingBean {

    /**
     *
     */
    private Map<FlowState, PileBuilder> stateBuilderMap = new ConcurrentHashMap<>();

    /**
     *
     */
    @Autowired
    private List<PileBuilder> statePileBuilders;

    /**
     *
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * @param trade
     * @return
     */
    public StateMachine<FlowState, TradeEvent> create(PileTrade trade) {
        FlowState flowState = trade.getTradeState().getFlowState();
        PileBuilder pileBuilder = stateBuilderMap.get(flowState);
        if (pileBuilder == null) {
            throw new SbcRuntimeException("K-050102");
        }

        //
        StateMachine<FlowState, TradeEvent> sm;
        try {
            sm = pileBuilder.build(trade, beanFactory);
            sm.start();
        } catch (Exception e) {
            throw new SbcRuntimeException("K-050102");
        }

        //
        sm.getExtendedState().getVariables().put(PileTrade.class, trade);
        return sm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        stateBuilderMap = statePileBuilders.stream().collect(Collectors.toMap(PileBuilder::supportState, Function.identity()));
    }
}
