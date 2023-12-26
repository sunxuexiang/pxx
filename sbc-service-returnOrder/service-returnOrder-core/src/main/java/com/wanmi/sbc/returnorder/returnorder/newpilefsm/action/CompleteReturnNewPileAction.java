package com.wanmi.sbc.returnorder.returnorder.newpilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.NewPileReturnAction;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.NewPileReturnStateContext;
import com.wanmi.sbc.returnorder.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动完成
 */
@Component
public class CompleteReturnNewPileAction extends NewPileReturnAction {
    @Autowired
    private InventoryDetailSamountService inventoryDetailSamountService;

    @Override
    protected void evaluateInternal(NewPileReturnOrder returnOrder, NewPileReturnStateRequest request, NewPileReturnStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnOrder.setReturnFlowState(NewPileReturnFlowState.COMPLETED);
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.COMPLETE.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format("退单[%s]已完成,操作人:%s", returnOrder.getId(), operator.getName()))
                .build();
        returnOrder.appendReturnEventLog(eventLog);

        inventoryDetailSamountService.returnAmountByRid(returnOrder.getId());
        newPileReturnOrderService.updateReturnOrder(returnOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.COMPLETE.getDesc(), eventLog.getEventDetail());
    }
}
