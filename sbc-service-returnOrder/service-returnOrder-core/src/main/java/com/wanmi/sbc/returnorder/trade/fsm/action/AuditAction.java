package com.wanmi.sbc.returnorder.trade.fsm.action;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailQueryRequest;
import com.wanmi.sbc.customer.api.response.points.CustomerPointsDetailResponse;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.AuditState;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.trade.fsm.TradeAction;
import com.wanmi.sbc.returnorder.trade.fsm.TradeStateContext;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class AuditAction extends TradeAction {

    @Autowired
    private CustomerPointsDetailQueryProvider customerPointsDetailQueryProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Override
    protected void evaluateInternal(Trade trade, StateRequest request, TradeStateContext tsc) {
        AuditState auditState = tsc.getRequestData();
        if (auditState == AuditState.CHECKED || trade.getTradeState().getFlowState() == FlowState.VOID) {
            check(trade, tsc.getOperator());
        } else {
            reject(trade, tsc.getOperator());
        }
    }

    /**
     * 审核
     *
     * @param trade
     */
    private void check(Trade trade, Operator operator) {

        TradeState tradeState = trade.getTradeState();

        tradeState.setAuditState(AuditState.CHECKED);
        String detail = String.format("[%s]审核通过了订单", operator.getName());
        if(trade.getTradeState().getFlowState() == FlowState.VOID){
            detail = String.format("[%s]作废了退款单，订单状态扭转为待发货", operator.getName());
        }
        //如果是自提订单切换成待自提
        if (trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
            tradeState.setFlowState(FlowState.TOPICKUP);
            detail = String.format("[%s]审核通过了订单(自提订),订单扭转为待自提", operator.getName());
            if(trade.getTradeState().getFlowState() == FlowState.VOID){
                detail = String.format("[%s]作废了退款单(自提订)，订单状态扭转为待自提", operator.getName());
            }
        } else {
            tradeState.setFlowState(FlowState.AUDIT);
        }
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(FlowState.AUDIT.getDescription())
                .eventDetail(detail)
                .build());
        save(trade);
        super.operationLogMq.convertAndSend(operator, FlowState.AUDIT.getDescription(), detail);
    }

    /**
     * @param trade
     */
    private void reject(Trade trade, Operator operator) {
        TradeState tradeState = trade.getTradeState();
        tradeState.setAuditState(AuditState.REJECTED);
        tradeState.setFlowState(FlowState.VOID);
        String detail = String.format("[%s]审核驳回了订单%s", operator.getName(), trade.getId());
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(FlowState.AUDIT.getDescription())
                .eventDetail(detail)
                .build());
        save(trade);
        super.operationLogMq.convertAndSend(operator, FlowState.AUDIT.getDescription(), detail);

        // 全额返还订单使用积分
        BaseResponse<CustomerPointsDetailResponse> response = customerPointsDetailQueryProvider.getOne(
                CustomerPointsDetailQueryRequest.builder()
                        .content(JSONObject.toJSONString(Collections.singletonMap("orderNo", trade.getId())))
                        .build());
        Long points = response.getContext().getCustomerPointsDetailVO().getPoints();
        if (points != null && points > 0) {
            customerPointsDetailSaveProvider.returnPoints(CustomerPointsDetailAddRequest.builder()
                    .customerId(trade.getBuyer().getId())
                    .type(OperateType.GROWTH)
                    .serviceType(PointsServiceType.ORDER_CANCEL_BACK)
                    .points(points)
                    .content(JSONObject.toJSONString(Collections.singletonMap("orderNo", trade.getId())))
                    .build());
        }
    }
}
