package com.wanmi.sbc.order.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileAction;
import com.wanmi.sbc.order.returnorder.fsm.ReturnStateContext;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 取消退单
 * Created by jinwei on 21/4/2017.
 */
@Component
public class CancelReturnPileAction extends ReturnPileAction {
    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnFlowState(ReturnFlowState.VOID);
        returnPileOrder.setRejectReason(request.getData().toString());
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(ReturnEvent.VOID.getDesc())
                .eventDetail(String.format("%s取消了退单,退单号%s", operator.getName(), returnPileOrder.getId()))
                .remark(request.getData().toString())
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.VOID.getDesc(), eventLog.getEventDetail());
    }
}
