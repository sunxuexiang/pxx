package com.wanmi.sbc.returnorder.returnorder.pilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.ReturnPileAction;
import com.wanmi.sbc.returnorder.returnorder.pilefsm.ReturnPileStateContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 修改退货单
 * Created by jinwei on 21/4/2017.
 */
@Component
public class RemedyReturnPileAction extends ReturnPileAction {
    @Override
    protected void evaluateInternal(ReturnPileOrder oldReturnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        ReturnPileOrder newReturnPileOrder = rsc.findRequestData();
        oldReturnPileOrder.setReturnFlowState(ReturnFlowState.INIT);
        String join = StringUtils.join(oldReturnPileOrder.buildDiffStr(newReturnPileOrder), ";");
        if (StringUtils.isNotBlank(join) && !join.equals("null")) {
            oldReturnPileOrder.appendReturnEventLog(
                    ReturnEventLog.builder()
                            .operator(operator)
                            .eventTime(LocalDateTime.now())
                            .eventType(ReturnEvent.REMEDY.getDesc())
                            .eventDetail(join)
                            .build()
            );
        }
        //merge returnorder
        oldReturnPileOrder.merge(newReturnPileOrder);

        returnPileOrderService.updateReturnOrder(oldReturnPileOrder);
        if (StringUtils.isNotBlank(join) && !join.equals("null")) {
            super.operationLogMq.convertAndSend(operator, ReturnEvent.REMEDY.getDesc(), join);
        }
    }
}
