package com.wanmi.sbc.returnorder.returnorder.newpilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.aspect.LockUnlock;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.event.NewPileReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.repository.NewPileReturnOrderRepository;
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
public class NewPileReturnFSMService {

    @Autowired
    private NewPileReturnOrderRepository newPileReturnOrderRepository;

    @Autowired
    private NewPileReturnBuilderFactory newPileReturnBuilderFactory;

    /**
     * 修改订单状态
     *
     * @param request
     * @return
     */
    @LockUnlock
    public boolean changeState(NewPileReturnStateRequest request) {
        //1、查找退单信息
        NewPileReturnOrder returnOrder = newPileReturnOrderRepository.findById(request.getRid()).orElse(null);
        if (returnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }

        //2. 根据订单创建状态机
        StateMachine<NewPileReturnFlowState, NewPileReturnEvent> stateMachine = newPileReturnBuilderFactory.create(returnOrder);
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
    public boolean changePileState(NewPileReturnStateRequest request) {
        //1、查找退单信息
        NewPileReturnOrder returnPileOrder = newPileReturnOrderRepository.findById(request.getRid()).orElse(null);
        if (returnPileOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }

        //2. 根据订单创建状态机
        StateMachine<NewPileReturnFlowState, NewPileReturnEvent> stateMachine = newPileReturnBuilderFactory.create(returnPileOrder);
        if (stateMachine == null) {
            throw new SbcRuntimeException("K-050004");
        }
        stateMachine.getExtendedState().getVariables().put(NewPileReturnStateRequest.class, request);

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
