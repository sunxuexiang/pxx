package com.wanmi.ares.replay.trade.service;

import com.wanmi.ares.replay.trade.dao.ReplayTradeMapper;
import com.wanmi.ares.replay.trade.model.request.ReplayTradeSevenDayRequest;
import com.wanmi.ares.replay.trade.model.response.*;
import com.wanmi.ares.request.replay.BossCustomerTradeRequest;
import com.wanmi.ares.request.replay.CustomerTradeRequest;
import com.wanmi.ares.response.datecenter.BossCustomerTradeResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeResponse;
import com.wanmi.ares.response.datecenter.TradeStatisticResponse;
import com.wanmi.ares.view.replay.ReplayTradeBuyerStoreResponse;
import com.wanmi.ares.view.replay.SevenDaySalePriceView;
import com.wanmi.ares.view.replay.TodayProvinceSalePriceView;
import com.wanmi.ares.view.replay.TowDayStoreSalePriceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReplayTradeService {

    @Autowired
    private ReplayTradeMapper replayTradeMapper;


    public List<ReplayTradeSevenDayResponse> tradeSevenDaySaleStatistic(ReplayTradeSevenDayRequest request){
        return replayTradeMapper.tradeSevenDaySaleStatistic(request);
    }

    public List<ReplayTradeSevenDayResponse> tradeSevenDayItemsStatistic(ReplayTradeSevenDayRequest request){
        return replayTradeMapper.tradeSevenDayItemsStatistic(request);
    }

    public List<ReplayTradeSevenDayResponse> tradeTwoDaySaleStatistic(){
        return replayTradeMapper.tradeTwoDaySaleStatistic();
    }

    public List<SalesAmountData> queryTwoDaySalesAmountStatistic(){
        return replayTradeMapper.queryTwoDaySalesAmountStatistic();
    }

    public List<SalesAmountNewData> queryTwoDaySalesAmountStatisticNew(){
        return replayTradeMapper.queryTwoDaySalesAmountStatisticNew();
    }

    public List<SalesCaseData> queryTwoDaySalesCaseStatistic(){
        return replayTradeMapper.queryTwoDaySalesCaseStatistic();
    }

    public List<SalesCaseNewData> queryTwoDaySalesCaseStatisticNew(){
        return replayTradeMapper.queryTwoDaySalesCaseStatisticNew();
    }

    public List<SalesOrderData> queryTwoDaySalesOrderStatistic(){
        return replayTradeMapper.queryTwoDaySalesOrderStatistic();
    }

    public List<SalesOrderNewData> queryTwoDaySalesOrderStatisticNew(){
        return replayTradeMapper.queryTwoDaySalesOrderStatisticNew();
    }

    public List<SalesUserData> queryTodayDaySalesUserStatistic(){
        return replayTradeMapper.queryTodayDaySalesUserStatistic();
    }

    public List<SalesUserNewData> queryTodayDaySalesUserStatisticNew(){
        return replayTradeMapper.queryTodayDaySalesUserStatisticNew();
    }

    public List<RecentSevenDaySale> querySevenDayCaseStatistic(){
        return replayTradeMapper.querySevenDayCaseStatistic();
    }

    public List<RecentSevenDaySaleNew> querySevenDayCaseStatisticNew(){
        return replayTradeMapper.querySevenDayCaseStatisticNew();
    }

    public List<RecentSevenDaySalePirce> querySevenDayPriceStatistic(){
        return replayTradeMapper.querySevenDayPriceStatistic();
    }

    public List<RecentSevenDaySalePirceNew> querySevenDayPriceStatisticNew(){
        return replayTradeMapper.querySevenDayPriceStatisticNew();
    }

    public List<SaleSynthesisData> queryCurrentMonthCaseStatistic(){
        return replayTradeMapper.queryCurrentMonthCaseStatistic();
    }

    public List<SaleSynthesisNewData> queryCurrentMonthCaseStatisticNew(){
        return replayTradeMapper.queryCurrentMonthCaseStatisticNew();
    }

    public List<SaleSynthesisData> queryCurrentMonthAmountStatistic(){
        return replayTradeMapper.queryCurrentMonthAmountStatistic();
    }

    public List<SaleSynthesisNewData> queryCurrentMonthAmountStatisticNew(){
        return replayTradeMapper.queryCurrentMonthAmountStatisticNew();
    }

    public List<SaleSynthesisData> queryCurrentMonthOrderStatistic(){
        return replayTradeMapper.queryCurrentMonthOrderStatistic();
    }

    public List<SaleSynthesisNewData> queryCurrentMonthOrderStatisticNew(){
        return replayTradeMapper.queryCurrentMonthOrderStatisticNew();
    }

    public List<SaleSynthesisData> queryCurrentMonthUserStatistic(){
        return replayTradeMapper.queryCurrentMonthUserStatistic();
    }

    public List<SaleSynthesisNewData> queryCurrentMonthUserStatisticNew(){
        return replayTradeMapper.queryCurrentMonthUserStatisticNew();
    }

    public List<CustomerTradeItemResponse> queryCustomerTradeItemByCid(String customerId, String date) {
        return replayTradeMapper.queryCustomerTradeItem(customerId,date);
    }

    public List<CustomerTradeResponse> queryCustomerTradeDetail(CustomerTradeRequest customerTradeRequest) {
        List<String> customerIds = customerTradeRequest.getCustomerIds();
        List<TradeStatisticResponse> tradeByDataForBoss = replayTradeMapper.queryTradeByDataForBoss(customerTradeRequest.getDate(),customerIds);
        Map<String, List<TradeStatisticResponse>> listMap = tradeByDataForBoss.stream().collect(Collectors.groupingBy(TradeStatisticResponse::getCustomerId));
        // 查询当月每个订单总箱数
        BigDecimal defaultVal = BigDecimal.ZERO;
        List<CustomerTradeResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<TradeStatisticResponse>> entry : listMap.entrySet()) {
            String customerId = entry.getKey();
            List<TradeStatisticResponse> tradeItemList = entry.getValue();
            // 查询订单箱数
            CustomerTradeResponse bossCustomerTrade = new CustomerTradeResponse();
            bossCustomerTrade.setTradeNum(tradeItemList.size());
            bossCustomerTrade.setTotalPrice(tradeItemList.stream().map(item -> item.getTradePrice()).reduce(defaultVal,BigDecimal::add));
            bossCustomerTrade.setCustomerId(customerId);
            // 设置总箱数
            bossCustomerTrade.setBuyNum(tradeItemList.stream().map(item -> item.getNum()).reduce(0,(a,b)->a+b));

            result.add(bossCustomerTrade);
        }
        return result;
    }

    public List<BossCustomerTradeResponse> queryAllTradeByDate(BossCustomerTradeRequest request) {
        List<TradeStatisticResponse> tradeByDataForBoss = replayTradeMapper.queryTradeByDataForBoss(request.getDate(),null);
        Map<String, List<TradeStatisticResponse>> listMap = tradeByDataForBoss.stream().collect(Collectors.groupingBy(TradeStatisticResponse::getCustomerId));
        // 查询当月每个订单总箱数
        BigDecimal defaultVal = BigDecimal.ZERO;
        List<BossCustomerTradeResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<TradeStatisticResponse>> entry : listMap.entrySet()) {
            String customerId = entry.getKey();
            List<TradeStatisticResponse> tradeItemList = entry.getValue();
            // 查询订单箱数
            BossCustomerTradeResponse bossCustomerTrade = new BossCustomerTradeResponse();
            bossCustomerTrade.setTradeNum(tradeItemList.size());
            bossCustomerTrade.setTradePrice(tradeItemList.stream().map(item -> item.getTradePrice()).reduce(defaultVal,BigDecimal::add));
            bossCustomerTrade.setCustomerId(customerId);
            // 设置总箱数
            bossCustomerTrade.setTradeItemNum(tradeItemList.stream().map(item -> item.getNum()).reduce(0,(a,b)->a+b));
            result.add(bossCustomerTrade);
        }
        return result;
    }

    public List<TowDayStoreSalePriceView> queryTowDayStoreSalePriceView() {
        return replayTradeMapper.queryTowDayStoreSalePriceView();
    }

    public List<TodayProvinceSalePriceView> queryTodayProvinceSalePriceView() {
        return replayTradeMapper.queryTodayProvinceSalePriceView();
    }

    public List<SevenDaySalePriceView> querySevenDaySalePriceView() {
        return replayTradeMapper.querySevenDaySalePriceView();
    }

    public List<ReplayTradeBuyerStoreResponse> staticsCompanyPayFeeByStartTime(Date startTime){
        return replayTradeMapper.staticsCompanyPayFeeByStartTime(startTime);
    }

    public List<ReplayTradeBuyerStoreResponse> staticsCustomerCompanyPayFeeByStartTime(Date startTime){
        return replayTradeMapper.staticsCustomerCompanyPayFeeByStartTime(startTime);
    }
}
