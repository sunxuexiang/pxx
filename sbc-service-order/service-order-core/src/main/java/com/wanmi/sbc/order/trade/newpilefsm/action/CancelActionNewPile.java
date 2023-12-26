package com.wanmi.sbc.order.trade.newpilefsm.action;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.order.api.request.growthvalue.OrderGrowthValueTempQueryRequest;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.growthvalue.model.root.OrderGrowthValueTemp;
import com.wanmi.sbc.order.growthvalue.repository.OrderGrowthValueTempRepository;
import com.wanmi.sbc.order.growthvalue.service.OrderGrowthValueTempService;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.NewPileTradeState;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
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
public class CancelActionNewPile extends NewPileTradeAction {

    @Autowired
    private OrderGrowthValueTempService orderGrowthValueTempService;

    @Autowired
    private OrderGrowthValueTempRepository orderGrowthValueTempRepository;


    @Override
    protected void evaluateInternal(NewPileTrade trade, StateRequest request, NewPileTradeStateContext tsc) {
        Operator operator = tsc.getOperator();
        NewPileTradeState tradeState = trade.getTradeState();
        logger.info("evaluateInternal------------->" + JSONObject.toJSONString(tradeState));
        tradeState.setEndTime(LocalDateTime.now());
        tradeState.setFlowState(NewPileFlowState.VOID);
        tradeState.setObsoleteReason(Objects.toString(request.getData(), ""));
        String detail = String.format("[%s]取消了订单%s", operator.getName(), trade.getId());
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(NewPileFlowState.VOID.getDescription())
                .eventDetail(detail)
                .build());

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
