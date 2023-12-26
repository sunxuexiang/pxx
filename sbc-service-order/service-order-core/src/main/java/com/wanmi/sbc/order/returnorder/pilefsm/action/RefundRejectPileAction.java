package com.wanmi.sbc.order.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileAction;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileStateContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 拒绝退款
 * Created by jinwei on 2/5/2017.
 */
@Component
public class RefundRejectPileAction extends ReturnPileAction {

    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnFlowState(ReturnFlowState.REJECT_REFUND);
        returnPileOrder.setRejectReason(rsc.findRequestData());
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.REJECT_REFUND.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format("退单[%s]拒绝退款,操作人:%s", returnPileOrder.getId(), operator.getName()))
                .remark(rsc.findRequestData())
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.REJECT_REFUND.getDesc(), eventLog.getEventDetail());
    }
}
