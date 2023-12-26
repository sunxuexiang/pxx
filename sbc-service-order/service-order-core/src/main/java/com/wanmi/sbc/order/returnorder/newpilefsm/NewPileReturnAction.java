package com.wanmi.sbc.order.returnorder.newpilefsm;

import com.wanmi.sbc.order.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.order.common.OperationLogMq;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.newpilefsm.event.NewPileReturnEvent;
import com.wanmi.sbc.order.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 *
 * Created by jinwei on 2017/4/21.
 */
public abstract class NewPileReturnAction implements Action<NewPileReturnFlowState, NewPileReturnEvent> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    public OperationLogMq operationLogMq;

    @Override
    public void execute(StateContext<NewPileReturnFlowState, NewPileReturnEvent> stateContext) {
        NewPileReturnStateContext rsc = new NewPileReturnStateContext(stateContext);
        try {
            evaluateInternal(rsc.findReturnOrder(),rsc.findRequest(), rsc);
        } catch (Exception e) {
            rsc.put(Exception.class, e);
            logger.error(String.format("[退货]处理, 从状态[ %s ], 经过事件[ %s ], 到状态[ %s ], 出现异常[ %s ]", stateContext.getSource().getId(), stateContext.getTarget().getId(), stateContext.getEvent(), e.getMessage()));
        }
    }
    protected abstract void evaluateInternal(NewPileReturnOrder returnOrder, NewPileReturnStateRequest request, NewPileReturnStateContext rsc);
}
