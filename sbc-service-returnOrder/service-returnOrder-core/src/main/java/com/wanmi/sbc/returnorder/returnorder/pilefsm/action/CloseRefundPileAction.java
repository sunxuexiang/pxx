package com.wanmi.sbc.returnorder.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.ReturnPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.ReturnPileStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 取消退单
 */
@Component
public class CloseRefundPileAction extends ReturnPileAction {
    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnFlowState(ReturnFlowState.COMPLETED);
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.CLOSE_REFUND.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format("手动关闭退单，操作人:%s", operator.getName()))
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.CLOSE_REFUND.getDesc(), eventLog.getEventDetail());
    }
}
