package com.wanmi.sbc.returnorder.pointstrade.fsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.PointsFlowState;
import com.wanmi.sbc.returnorder.pointstrade.fsm.event.PointsTradeEvent;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
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
public class PointsTradeBuilderFactory implements InitializingBean {

    /**
     *
     */
    private Map<PointsFlowState, PointsTradeBuilder> stateBuilderMap = new ConcurrentHashMap<>();

    /**
     *
     */
    @Autowired
    private List<PointsTradeBuilder> stateBuilders;

    /**
     *
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * @param trade
     * @return
     */
    public StateMachine<PointsFlowState, PointsTradeEvent> create(Trade trade) {
        PointsFlowState pointsFlowState = PointsFlowState.forValue(trade.getTradeState().getFlowState().getStateId());
        PointsTradeBuilder builder = stateBuilderMap.get(pointsFlowState);
        if (builder == null) {
            throw new SbcRuntimeException("K-050102");
        }

        //
        StateMachine<PointsFlowState, PointsTradeEvent> sm;
        try {
            sm = builder.build(trade, beanFactory);
            sm.start();
        } catch (Exception e) {
            throw new SbcRuntimeException("K-050102");
        }

        //
        sm.getExtendedState().getVariables().put(Trade.class, trade);
        return sm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        stateBuilderMap = stateBuilders.stream().collect(Collectors.toMap(PointsTradeBuilder::supportState, Function.identity()));
    }
}
