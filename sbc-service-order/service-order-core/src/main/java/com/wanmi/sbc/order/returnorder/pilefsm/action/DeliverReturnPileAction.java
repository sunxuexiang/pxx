package com.wanmi.sbc.order.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnWay;
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
 * 发货action
 * Created by jinwei on 21/4/2017.
 */
@Component
public class DeliverReturnPileAction extends ReturnPileAction {

    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {

        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnLogistics(rsc.findRequestData());
        returnPileOrder.setReturnFlowState(ReturnFlowState.DELIVERED);
        String format = "退单[%s]货物已发,操作人:%s";
        if (returnPileOrder.getReturnWay() == ReturnWay.OTHER) {
            format = "退单[%s]退回方式为非快递，审核后流程状态自动变更为已发退货，审核人:%s";
        }
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.DELIVER.getDesc())
                .eventDetail(String.format(format, returnPileOrder.getId(), operator.getName()))
                .eventTime(LocalDateTime.now())
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.DELIVER.getDesc(), eventLog.getEventDetail());
    }
}
