package com.wanmi.sbc.returnorder.returnorder.newpilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.NewPileReturnAction;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.NewPileReturnStateContext;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.event.NewPileReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 取消退单
 */
@Component
public class CancelReturnNewPileAction extends NewPileReturnAction {
    @Override
    protected void evaluateInternal(NewPileReturnOrder returnOrder, NewPileReturnStateRequest request, NewPileReturnStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnOrder.setReturnFlowState(NewPileReturnFlowState.VOID);
        returnOrder.setRejectReason(request.getData().toString());
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(NewPileReturnEvent.VOID.getDesc())
                .eventDetail(String.format("%s取消了退单,退单号%s", operator.getName(), returnOrder.getId()))
                .remark(request.getData().toString())
                .build();
        returnOrder.appendReturnEventLog(eventLog);
        newPileReturnOrderService.updateReturnOrder(returnOrder);
        super.operationLogMq.convertAndSend(operator, NewPileReturnEvent.VOID.getDesc(), eventLog.getEventDetail());
    }
}
