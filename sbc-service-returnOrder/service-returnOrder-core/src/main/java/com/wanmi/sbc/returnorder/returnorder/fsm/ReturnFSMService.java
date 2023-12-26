package com.wanmi.sbc.returnorder.returnorder.fsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.aspect.LockUnlock;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.ReturnPileBuilderFactory;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderRepository;
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
public class ReturnFSMService {

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnPileOrderRepository returnPileOrderRepository;

    @Autowired
    private ReturnBuilderFactory builderFactory;

    @Autowired
    private ReturnPileBuilderFactory pileBuilderFactory;

    /**
     * 修改订单状态
     *
     * @param request
     * @return
     */
    @LockUnlock
    public boolean changeState(ReturnStateRequest request) {
        //1、查找退单信息
        ReturnOrder returnOrder = returnOrderRepository.findById(request.getRid()).orElse(null);
        if (returnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }

        //2. 根据订单创建状态机
        StateMachine<ReturnFlowState, ReturnEvent> stateMachine = builderFactory.create(returnOrder);
        if (stateMachine == null) {
            throw new SbcRuntimeException("K-050004");
        }
        stateMachine.getExtendedState().getVariables().put(ReturnStateRequest.class, request);

        //3. 发送当前请求的事件
        boolean isSend = stateMachine.sendEvent(request.getReturnEvent());
        if (!isSend) {
            log.error("[退单]无法从状态[{}]转向 => [{}]", returnOrder.getReturnFlowState(), request.getReturnEvent());
            throw new SbcRuntimeException("K-050002");
        }

        //4. 判断处理过程中是否出现了异常
        Exception exception = stateMachine.getExtendedState().get(Exception.class, Exception.class);
        if (exception != null) {
            if (exception.getClass().isAssignableFrom(SbcRuntimeException.class)) {
                throw (SbcRuntimeException) exception;
            } else {
                throw new SbcRuntimeException(exception);
            }
        }
        return isSend;
    }

    /**
     * 修改囤货订单状态
     *
     * @param request
     * @return
     */
    @LockUnlock
    public boolean changePileState(ReturnStateRequest request) {
        //1、查找退单信息
        ReturnPileOrder returnPileOrder = returnPileOrderRepository.findById(request.getRid()).orElse(null);
        if (returnPileOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }

        //2. 根据订单创建状态机
        StateMachine<ReturnFlowState, ReturnEvent> stateMachine = pileBuilderFactory.create(returnPileOrder);
        if (stateMachine == null) {
            throw new SbcRuntimeException("K-050004");
        }
        stateMachine.getExtendedState().getVariables().put(ReturnStateRequest.class, request);

        //3. 发送当前请求的事件
        boolean isSend = stateMachine.sendEvent(request.getReturnEvent());
        if (!isSend) {
            log.error("[退单]无法从状态[{}]转向 => [{}]", returnPileOrder.getReturnFlowState(), request.getReturnEvent());
            throw new SbcRuntimeException("K-050002");
        }

        //4. 判断处理过程中是否出现了异常
        Exception exception = stateMachine.getExtendedState().get(Exception.class, Exception.class);
        if (exception != null) {
            if (exception.getClass().isAssignableFrom(SbcRuntimeException.class)) {
                throw (SbcRuntimeException) exception;
            } else {
                throw new SbcRuntimeException(exception);
            }
        }
        return isSend;
    }

}
