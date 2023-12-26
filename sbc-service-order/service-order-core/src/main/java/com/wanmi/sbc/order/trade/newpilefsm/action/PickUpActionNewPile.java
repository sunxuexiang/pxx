package com.wanmi.sbc.order.trade.newpilefsm.action;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.order.api.request.growthvalue.OrderGrowthValueTempQueryRequest;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.growthvalue.model.root.OrderGrowthValueTemp;
import com.wanmi.sbc.order.growthvalue.repository.OrderGrowthValueTempRepository;
import com.wanmi.sbc.order.growthvalue.service.OrderGrowthValueTempService;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.NewPileTradeState;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.TradeGroupon;
import com.wanmi.sbc.order.trade.newpilefsm.NewPileTradeAction;
import com.wanmi.sbc.order.trade.newpilefsm.NewPileTradeStateContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class PickUpActionNewPile extends NewPileTradeAction {

    @Autowired
    private OrderGrowthValueTempService orderGrowthValueTempService;

    @Autowired
    private OrderGrowthValueTempRepository orderGrowthValueTempRepository;

    @Autowired
    private CustomerPointsDetailQueryProvider customerPointsDetailQueryProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Override
    protected void evaluateInternal(NewPileTrade trade, StateRequest request, NewPileTradeStateContext tsc) {
        Operator operator = tsc.getOperator();
        NewPileTradeState tradeState = trade.getTradeState();
        // 调用此作废订单action的行为共有4种，分别为取消订单、审核驳回、仅退款、全部退货或退款

        tradeState.setEndTime(LocalDateTime.now());
        tradeState.setFlowState(NewPileFlowState.VOID);
        tradeState.setObsoleteReason(Objects.toString(request.getData(), ""));
        String detail = String.format("[%s]提货了订单%s", operator.getName(), trade.getId());
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(NewPileFlowState.VOID.getDescription())
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
        super.operationLogMq.convertAndSend(operator, NewPileFlowState.VOID.getDescription(), detail);

        // 删除订单成长值临时表中的数据
        List<OrderGrowthValueTemp> result = orderGrowthValueTempService.list(
                OrderGrowthValueTempQueryRequest.builder().orderNo(trade.getId()).build());
        if (CollectionUtils.isNotEmpty(result)) {
            orderGrowthValueTempRepository.deleteAll(result);
        }

    }
}
