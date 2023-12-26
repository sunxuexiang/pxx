package com.wanmi.sbc.returnorder.trade.pilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

/**
 * 状态机服务
 * Created by jinwei on 28/03/2017.
 */
@Service
@Slf4j
public class PileTradeFSMService {

    @Autowired
    private PileTradeRepository pileTradeRepository;

    @Autowired
    private PileBuilderFactory pileBuilderFactory;

    /**
     * 修改囤货订单状态
     *
     * @param request
     * @return
     */
    public boolean changePileTradeState(StateRequest request) {
        //1、查找订单信息
        PileTrade trade = pileTradeRepository.findById(request.getTid()).orElse(null);
        if (trade == null) {
            throw new SbcRuntimeException("K-050100", new Object[]{request.getTid()});
        }

        //2. 根据订单创建状态机
        StateMachine<FlowState, TradeEvent> stateMachine = pileBuilderFactory.create(trade);
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
