package com.wanmi.sbc.order.trade.pilefsm.action;

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
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.order.api.request.growthvalue.OrderGrowthValueTempQueryRequest;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.growthvalue.model.root.OrderGrowthValueTemp;
import com.wanmi.sbc.order.growthvalue.repository.OrderGrowthValueTempRepository;
import com.wanmi.sbc.order.growthvalue.service.OrderGrowthValueTempService;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.model.root.TradeGroupon;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeAction;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeStateContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class CancelActionPile extends PileTradeAction {

    @Autowired
    private OrderGrowthValueTempService orderGrowthValueTempService;

    @Autowired
    private OrderGrowthValueTempRepository orderGrowthValueTempRepository;

    @Autowired
    private CustomerPointsDetailQueryProvider customerPointsDetailQueryProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Override
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
        Operator operator = tsc.getOperator();
        TradeState tradeState = trade.getTradeState();
        // 调用此作废订单action的行为共有4种，分别为取消订单、审核驳回、仅退款、全部退货或退款
        // 此处返还订单积分需要排除全部退货(已完成)和仅退款(已支付未发货) 变成 已作废的这2种情况
        if (!tradeState.getFlowState().equals(FlowState.COMPLETED) &&
                !(tradeState.getPayState().equals(PayState.PAID) && tradeState.getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED))) {
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

        tradeState.setEndTime(LocalDateTime.now());
        tradeState.setFlowState(FlowState.VOID);
        tradeState.setObsoleteReason(Objects.toString(request.getData(), ""));
        String detail = String.format("[%s]作废了订单%s", operator.getName(), trade.getId());
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(FlowState.VOID.getDescription())
                .eventDetail(detail)
                .build());
        Boolean grouponFlag = trade.getGrouponFlag();
        TradeGroupon tradeGroupon = trade.getTradeGroupon();
        //拼团订单
        if (Objects.nonNull(grouponFlag) && Boolean.TRUE.equals(grouponFlag) && Objects.nonNull(tradeGroupon)
                && PayState.NOT_PAID == tradeState.getPayState()) {
            tradeGroupon.setGrouponOrderStatus(GrouponOrderStatus.FAIL);
            tradeGroupon.setFailTime(LocalDateTime.now());
        }
        save(trade);
        super.operationLogMq.convertAndSend(operator, FlowState.VOID.getDescription(), detail);

        // 删除订单成长值临时表中的数据
        List<OrderGrowthValueTemp> result = orderGrowthValueTempService.list(
                OrderGrowthValueTempQueryRequest.builder().orderNo(trade.getId()).build());
        if (CollectionUtils.isNotEmpty(result)) {
            orderGrowthValueTempRepository.deleteAll(result);
        }

    }
}
