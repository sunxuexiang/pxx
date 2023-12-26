package com.wanmi.sbc.order.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
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
 * 商家接受退货
 * Created by jinwei on 22/4/2017.
 */
@Component
public class ReceiveReturnPileAction extends ReturnPileAction {

    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        String detail = "";
        if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.DELIVERED)) {
            detail = "退单[%s]货物已签收,操作人:%s";

        } else if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.COMPLETED)) {
            detail = "退单[%s]执行作废退款操作,退单状态扭转为待退款，操作人:%s";
        } else {
            throw new SbcRuntimeException("K-050002");
        }

        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnFlowState(ReturnFlowState.RECEIVED);
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.RECEIVE.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format(detail, returnPileOrder.getId(), operator.getName()))
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.RECEIVE.getDesc(), eventLog.getEventDetail());
    }
}
