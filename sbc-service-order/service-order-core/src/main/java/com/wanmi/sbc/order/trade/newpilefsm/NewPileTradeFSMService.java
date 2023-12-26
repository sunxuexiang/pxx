package com.wanmi.sbc.order.trade.newpilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.repository.newPileTrade.NewPileTradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NewPileTradeFSMService {

    @Autowired
    private NewPileTradeRepository newPileTradeRepository;

    @Autowired
    private NewPileBuilderFactory builderFactory;

    /**
     * 修改订单状态
     *
     * @param request
     * @return
     */
    public boolean changeState(StateRequest request) {
        //1、查找订单信息
        NewPileTrade trade = newPileTradeRepository.findById(request.getTid()).orElse(null);
        if (trade == null) {
            throw new SbcRuntimeException("K-050100", new Object[]{request.getTid()});
        }

        //2. 根据订单创建状态机
        StateMachine<NewPileFlowState, TradeEvent> stateMachine = builderFactory.create(trade);
        stateMachine.getExtendedState().getVariables().put(StateRequest.class, request);

        //3. 发送当前请求的状态
        boolean isSend = stateMachine.sendEvent(request.getEvent());
        if (!isSend) {
            log.error("创建订单状态机失败,无法从状态 {} 转向 => {}", trade.getTradeState().getFlowState().getDescription(), request.getEvent().getDescription());
            throw new SbcRuntimeException("K-050102");
        }

        //4. 判断处理过程中是否出现了异常
        Exception exception = stateMachine.getExtendedState().get(Exception.class, Exception.class);
        if (exception != null) {
            if (exception.getClass().isAssignableFrom(SbcRuntimeException.class)) {
                throw (SbcRuntimeException) exception;
            } else {
                throw new SbcRuntimeException("K-050102");
            }
        }
        return true;
    }

    /**
     * 修改囤货订单状态
     *
     * @param request
     * @return
     */
    public boolean changePileTradeState(StateRequest request) {
        //1、查找订单信息
        NewPileTrade trade = newPileTradeRepository.findById(request.getTid()).orElse(null);
        if (trade == null) {
            throw new SbcRuntimeException("K-050100", new Object[]{request.getTid()});
        }

        //2. 根据订单创建状态机
        StateMachine<NewPileFlowState, TradeEvent> stateMachine = builderFactory.create(trade);
        stateMachine.getExtendedState().getVariables().put(StateRequest.class, request);

        //3. 发送当前请求的状态
        boolean isSend = stateMachine.sendEvent(request.getEvent());
        if (!isSend) {
            log.error("创建订单状态机失败,无法从状态 {} 转向 => {}", trade.getTradeState().getFlowState().getDescription(), request.getEvent().getDescription());
            throw new SbcRuntimeException("K-050102");
        }

        //4. 判断处理过程中是否出现了异常
        Exception exception = stateMachine.getExtendedState().get(Exception.class, Exception.class);
        if (exception != null) {
            if (exception.getClass().isAssignableFrom(SbcRuntimeException.class)) {
                throw (SbcRuntimeException) exception;
            } else {
                throw new SbcRuntimeException("K-050102");
            }
        }
        return true;
    }
}
