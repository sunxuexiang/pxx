package com.wanmi.sbc.returnorder.returnorder.pilefsm;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnPileOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Description:
 * @author: jiangxin
 * @create: 2021-09-28 17:02
 */
@Slf4j
public abstract class ReturnPileAction implements Action<ReturnFlowState, ReturnEvent>{

    @Autowired
    protected ReturnPileOrderService returnPileOrderService;

    @Autowired
    public OperationLogMq operationLogMq;

    @Override
    public void execute(StateContext<ReturnFlowState, ReturnEvent> stateContext) {
        ReturnPileStateContext rsc = new ReturnPileStateContext(stateContext);
        try {
            evaluateInternal(rsc.findReturnPileOrder(),rsc.findRequest(), rsc);
        } catch (Exception e) {
            rsc.put(Exception.class, e);
            log.error(String.format("[退货]处理, 从状态[ %s ], 经过事件[ %s ], 到状态[ %s ], 出现异常[ %s ]", stateContext.getSource().getId(), stateContext.getTarget().getId(), stateContext.getEvent(), e.getMessage()));
        }
    }
    protected abstract void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc);
}
