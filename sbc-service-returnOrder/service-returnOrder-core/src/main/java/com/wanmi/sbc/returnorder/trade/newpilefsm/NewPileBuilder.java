package com.wanmi.sbc.returnorder.trade.newpilefsm;

import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
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
