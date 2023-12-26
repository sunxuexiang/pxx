package com.wanmi.ares.report.trade.service;

import com.wanmi.ares.enums.StatisticsDataType;
import com.wanmi.ares.report.trade.model.root.TradeWeek;
import com.wanmi.ares.request.flow.FlowDataListRequest;
import com.wanmi.ares.view.trade.TradePageView;
import com.wanmi.ares.view.trade.TradeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SaveFlowDataForWeekStrategy
 * @Description 定义按月统计的按周统计数据的处理器
 * @Author lvzhenwei
 * @Date 2019/8/26 10:28
 **/
@Component
public class TradeDataForMonthWeekStrategy extends TradeDataStrategy {

    @Autowired
    private TradeWeekService tradeWeekService;

    @Override
    public StatisticsDataType[] supports() {
        return new StatisticsDataType[]{StatisticsDataType.MONTH_WEEk};
    }

    @Override
    public List<TradeView> getTradeData(FlowDataListRequest flowDataListRequest) {
        flowDataListRequest.setType(flowDataListRequest.getStatisticsWeekType().toValue());
        List<TradeWeek> tradeWeeks = tradeWeekService.queryWeek(flowDataListRequest);
        List<TradeView> collect = tradeWeeks.stream().map(this::buildTitleForWeek
        ).collect(Collectors.toList());
        return collect;
    }

    @Override
    public TradeView getTradeView(FlowDataListRequest flowDataListRequest) {
        return null;
    }

    @Override
    public TradePageView getTradePage(FlowDataListRequest flowDataListRequest) {
        return null;
    }

    @Override
    public TradePageView getTradeStorePage(FlowDataListRequest flowDataListRequest) {
        return null;
    }

}
