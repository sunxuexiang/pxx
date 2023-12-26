package com.wanmi.sbc.returnorder.returnorder.fsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.ReturnAction;
import com.wanmi.sbc.returnorder.returnorder.fsm.ReturnStateContext;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动完成
 * Created by jinwei on 22/4/2017.
 */
@Component
public class CompleteReturnAction extends ReturnAction {
    @Override
    protected void evaluateInternal(ReturnOrder returnOrder, ReturnStateRequest request, ReturnStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnOrder.setReturnFlowState(ReturnFlowState.COMPLETED);
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.COMPLETE.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format("退单[%s]已完成,操作人:%s", returnOrder.getId(), operator.getName()))
                .build();
        returnOrder.appendReturnEventLog(eventLog);
        returnOrderService.updateReturnOrder(returnOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.COMPLETE.getDesc(), eventLog.getEventDetail());
    }
}
