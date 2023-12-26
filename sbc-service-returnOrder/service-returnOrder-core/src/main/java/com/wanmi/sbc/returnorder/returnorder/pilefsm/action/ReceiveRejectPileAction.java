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
 * 退货拒绝签收
 * Created by jinwei on 22/4/2017.
 */
@Component
public class ReceiveRejectPileAction extends ReturnPileAction {
    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        //拒绝原因
        returnPileOrder.setRejectReason(rsc.findRequestData());
        returnPileOrder.setReturnFlowState(ReturnFlowState.REJECT_RECEIVE);
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.REJECT_RECEIVE.getDesc())
                .eventDetail(String.format("退单[%s]货物已拒绝签收,操作人:%s", returnPileOrder.getId(), operator.getName()))
                .eventTime(LocalDateTime.now())
                .remark(rsc.findRequestData())
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.REJECT_RECEIVE.getDesc(), eventLog.getEventDetail());
    }
}
