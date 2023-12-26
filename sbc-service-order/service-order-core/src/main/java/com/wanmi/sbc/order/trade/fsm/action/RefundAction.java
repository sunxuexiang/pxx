package com.wanmi.sbc.order.trade.fsm.action;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.bean.enums.YesOrNo;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.order.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.order.trade.fsm.TradeAction;
import com.wanmi.sbc.order.trade.fsm.TradeStateContext;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.root.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class RefundAction extends TradeAction {

    @Override
    protected void evaluateInternal(Trade trade, StateRequest request, TradeStateContext tsc) {
        TradeState tradeState = trade.getTradeState();

        // 已完成的退款操作
        if (tradeState.getFlowState().equals(FlowState.COMPLETED)) {
            String detail = String.format("订单[%s],申请退货,操作人:%s", trade.getId(), tsc.getOperator().getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(tsc.getOperator())
                    .eventType(TradeEvent.REFUND.getDescription())
                    .eventDetail(detail)
                    .eventTime(LocalDateTime.now())
                    .build());
            save(trade);
            super.operationLogMq.convertAndSend(tsc.getOperator(), TradeEvent.REFUND.getDescription(), detail);
        }

        //
        else {
            // 判断已支付
            if (!tradeState.getPayState().equals(PayState.PAID)) {
                throw new SbcRuntimeException("K-050105");
            }

            // 判断已发货
            if (!tradeState.getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)) {
                throw new SbcRuntimeException("K-050106");
            }
            String detail = String.format("订单%s,申请退款,操作人:%s", trade.getId(), tsc.getOperator().getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(tsc.getOperator())
                    .eventType(TradeEvent.REFUND.getDescription())
                    .eventDetail(detail)
                    .eventTime(LocalDateTime.now())
                    .build());
            save(trade);
            super.operationLogMq.convertAndSend(tsc.getOperator(), TradeEvent.REFUND.getDescription(), detail);
        }

    }

}
