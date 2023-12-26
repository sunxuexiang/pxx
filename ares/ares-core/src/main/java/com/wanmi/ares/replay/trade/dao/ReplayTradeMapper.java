package com.wanmi.ares.replay.trade.dao;

import com.wanmi.ares.replay.trade.model.request.ReplayTradeSevenDayRequest;
import com.wanmi.ares.replay.trade.model.response.*;
import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import com.wanmi.ares.response.datecenter.TradeStatisticResponse;
import com.wanmi.ares.view.replay.ReplayTradeBuyerStoreResponse;
import com.wanmi.ares.view.replay.SevenDaySalePriceView;
import com.wanmi.ares.view.replay.TodayProvinceSalePriceView;
import com.wanmi.ares.view.replay.TowDayStoreSalePriceView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ReplayTradeMapper {

    List<ReplayTradeSevenDayResponse> tradeSevenDaySaleStatistic(ReplayTradeSevenDayRequest request);

    List<ReplayTradeSevenDayResponse> tradeSevenDayItemsStatistic(ReplayTradeSevenDayRequest request);

    List<ReplayTradeSevenDayResponse> tradeTwoDaySaleStatistic();

    List<SalesAmountData> queryTwoDaySalesAmountStatistic();

    List<SalesAmountNewData> queryTwoDaySalesAmountStatisticNew();

    List<SalesCaseData> queryTwoDaySalesCaseStatistic();

    List<SalesCaseNewData> queryTwoDaySalesCaseStatisticNew();

    List<SalesOrderData> queryTwoDaySalesOrderStatistic();

    List<SalesOrderNewData> queryTwoDaySalesOrderStatisticNew();

    List<SalesUserData> queryTodayDaySalesUserStatistic();

    List<SalesUserNewData> queryTodayDaySalesUserStatisticNew();

    List<RecentSevenDaySale> querySevenDayCaseStatistic();
    List<RecentSevenDaySaleNew> querySevenDayCaseStatisticNew();

    List<RecentSevenDaySalePirce> querySevenDayPriceStatistic();

    List<RecentSevenDaySalePirceNew> querySevenDayPriceStatisticNew();

    List<SaleSynthesisData> queryCurrentMonthCaseStatistic();

    List<SaleSynthesisNewData> queryCurrentMonthCaseStatisticNew();

    List<SaleSynthesisData> queryCurrentMonthAmountStatistic();

    List<SaleSynthesisNewData> queryCurrentMonthAmountStatisticNew();

    List<SaleSynthesisData> queryCurrentMonthOrderStatistic();

    List<SaleSynthesisNewData> queryCurrentMonthOrderStatisticNew();

    List<SaleSynthesisData> queryCurrentMonthUserStatistic();

    List<SaleSynthesisNewData> queryCurrentMonthUserStatisticNew();

    List<CustomerTradeItemResponse> queryCustomerTradeItem(@Param("customerId") String customerId, @Param("date") String date);

    List<TradeStatisticResponse>  queryTradeByDataForBoss(@Param("date") String date,@Param("customerIds") List<String> customerIds);

    List<TowDayStoreSalePriceView> queryTowDayStoreSalePriceView();

    List<TodayProvinceSalePriceView> queryTodayProvinceSalePriceView();

    List<SevenDaySalePriceView> querySevenDaySalePriceView();

    List<ReplayTradeBuyerStoreResponse> staticsCompanyPayFeeByStartTime(@Param("startTime") Date startTime);

    List<ReplayTradeBuyerStoreResponse> staticsCustomerCompanyPayFeeByStartTime(@Param("startTime") Date startTime);
}
