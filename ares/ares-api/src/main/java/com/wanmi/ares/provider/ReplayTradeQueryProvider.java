package com.wanmi.ares.provider;

import com.wanmi.ares.request.ReplayTradeWareIdRequest;
import com.wanmi.ares.request.replay.BossCustomerTradeRequest;
import com.wanmi.ares.request.replay.CustomerTradeRequest;
import com.wanmi.ares.request.replay.ReplayTradeBuyerStoreQuery;
import com.wanmi.ares.request.replay.ReplayTradeRequest;
import com.wanmi.ares.response.datecenter.BossCustomerTradeResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeResponse;
import com.wanmi.ares.view.replay.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.ares.name}", url="${feign.url.ares:#{null}}",contextId = "ReplayTradeQueryProvider")
public interface ReplayTradeQueryProvider {

    @PostMapping("/ares/${application.ares.version}/replay/trade/tradeSevenDaySaleStatistic")
    ReplayTradeSevenDayView tradeSevenDaySaleStatistic(@RequestBody @Valid ReplayTradeRequest request);

    @PostMapping("/ares/${application.ares.version}/replay/trade/tradeTwoDaySaleStatistic")
    ReplayTradeSevenDayView tradeTwoDaySaleStatistic();

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryTwoDaySaleStatistic")
    ReplaySaleStatisticDataView queryTwoDaySaleStatistic(@RequestBody @Valid ReplayTradeWareIdRequest request);

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryCurrentMonthSaleStatistic")
    SaleSynthesisStatisticDataView queryCurrentMonthSaleStatistic();

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryCurrentMonthSaleStatisticNew")
    SaleSynthesisStatisticNewDataView queryCurrentMonthSaleStatisticNew();

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryCustomerTradeItemByCid")
    List<CustomerTradeItemResponse> queryCustomerTradeItemByCid(@RequestBody CustomerTradeRequest customerTradeRequest);

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryCustomerTradeDetail")
    List<CustomerTradeResponse> queryCustomerTradeDetail(@RequestBody CustomerTradeRequest customerTradeRequest);

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryAllTradeByDate")
    List<BossCustomerTradeResponse> queryAllTradeByDate(@RequestBody BossCustomerTradeRequest build);

    @PostMapping("/ares/${application.ares.version}/replay/trade/queryTwoDaySaleStatisticNew")
    ReplaySaleStatisticNewDataView queryTwoDaySaleStatisticNew();

    @GetMapping("/ares/${application.ares.version}/replay/trade/queryTowDayStoreSalePriceView")
    List<TowDayStoreSalePriceView> queryTowDayStoreSalePriceView();

    @GetMapping("/ares/${application.ares.version}/replay/trade/queryTodayProvinceSalePriceView")
    List<TodayProvinceSalePriceView> queryTodayProvinceSalePriceView();

    @GetMapping("/ares/${application.ares.version}/replay/trade/querySevenDaySalePriceView")
    List<SevenDaySalePriceView> querySevenDaySalePriceView();

    @PostMapping("/ares/${application.ares.version}/replay/trade/staticsCompanyPayFeeByStartTime")
    List<ReplayTradeBuyerStoreResponse> staticsCompanyPayFeeByStartTime(@RequestBody ReplayTradeBuyerStoreQuery query);

}
