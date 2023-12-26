package com.wanmi.sbc.order.trade.newpilefsm;

import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;

/**
 *
 * Created by mac on 2017/4/7.
 */
public interface NewPileBuilder {

    /**
     *
     * @return
     */
    NewPileFlowState supportState();

    /**
     *
     * @param trade
     * @return
     */
    StateMachine<NewPileFlowState, TradeEvent> build(NewPileTrade trade, BeanFactory beanFactory) throws Exception;

}
