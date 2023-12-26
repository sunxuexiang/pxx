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
 * 审核退单
 * Created by jinwei on 21/4/2017.
 */
@Component
public class AuditReturnPileAction extends ReturnPileAction {
    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.INIT) || returnPileOrder.getReturnFlowState().equals(ReturnFlowState.COMPLETED)) {
            Operator operator = rsc.findOperator();
            String detail = "退单[%s]审核通过";
            if (returnPileOrder.getReturnFlowState().equals(ReturnFlowState.COMPLETED)) {
                detail = "退单[%s]执行作废退款操作，状态扭转为待退款";
            }
            detail = String.format(detail + ",操作人:%s", returnPileOrder.getId(), operator.getName());
            returnPileOrder.setReturnFlowState(ReturnFlowState.AUDIT);
            returnPileOrder.setAuditTime(LocalDateTime.now());
            returnPileOrder.appendReturnEventLog(
                    ReturnEventLog.builder()
                            .operator(operator)
                            .eventTime(LocalDateTime.now())
                            .eventType(ReturnEvent.AUDIT.getDesc())
                            .eventDetail(detail)
                            .build()
            );
            returnPileOrderService.updateReturnOrder(returnPileOrder);
            super.operationLogMq.convertAndSend(operator, ReturnEvent.AUDIT.getDesc(), detail);
        } else {
            throw new SbcRuntimeException("K-050002");
        }
    }

}
