package com.wanmi.ares.provider.impl;

import com.google.common.collect.Lists;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.replay.trade.model.request.ReplayTradeSevenDayRequest;
import com.wanmi.ares.replay.trade.model.response.*;
import com.wanmi.ares.replay.trade.service.ReplayTradeService;
import com.wanmi.ares.request.ReplayTradeWareIdRequest;
import com.wanmi.ares.request.replay.BossCustomerTradeRequest;
import com.wanmi.ares.request.replay.CustomerTradeRequest;
import com.wanmi.ares.request.replay.ReplayTradeBuyerStoreQuery;
import com.wanmi.ares.request.replay.ReplayTradeRequest;
import com.wanmi.ares.response.datecenter.BossCustomerTradeResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeResponse;
import com.wanmi.ares.utils.KsBeanUtil;
import com.wanmi.ares.view.replay.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReplayTradeQueryController implements ReplayTradeQueryProvider {


    @Autowired
    private ReplayTradeService replayTradeService;


    public static final Map<Long, Long> wareHouseMap = new HashMap() {
        {
            this.put(49l, 1l);
            this.put(50l, 46l);
            this.put(51l, 47l);
        }
    };


    /**
     * 最近七天正常下单金额、箱数
     */
    @Override
    public ReplayTradeSevenDayView tradeSevenDaySaleStatistic(ReplayTradeRequest request) {

        ReplayTradeSevenDayRequest dayRequest = KsBeanUtil.convert(request, ReplayTradeSevenDayRequest.class);

        List<ReplayTradeSevenDayResponse> replayTradeSaleSevenDayResponses = replayTradeService.tradeSevenDaySaleStatistic(dayRequest);

        List<ReplayTradeSevenDayResponse> replayTradeItemsSevenDayResponses = replayTradeService.tradeSevenDayItemsStatistic(dayRequest);

        ReplayTradeSevenDayView replayTradeSevenDayView = new ReplayTradeSevenDayView();
        List<ReplayTradeStatisticView> replayTradeStatisticViewList = Lists.newArrayList();
        replayTradeSaleSevenDayResponses.forEach(r -> {
            ReplayTradeStatisticView view = new ReplayTradeStatisticView();
            view.setDayTime(r.getDayTime());
            ReplayTradeSevenDayResponse replayTrade = replayTradeItemsSevenDayResponses.stream().filter(i -> i.getDayTime().equals(r.getDayTime())).findFirst().orElse(null);
            view.setTotalPrice(r.getTotalPrice());
            view.setTotalNum(replayTrade.getTotalNum());
            replayTradeStatisticViewList.add(view);
        });
        replayTradeSevenDayView.setReplayTradeStatisticViewList(replayTradeStatisticViewList);
        return replayTradeSevenDayView;
    }

    /**
     * 今昨日正常下单金额
     */
    @Override
    public ReplayTradeSevenDayView tradeTwoDaySaleStatistic() {

        List<ReplayTradeSevenDayResponse> replayTradeSaleSevenDayResponses = replayTradeService.tradeTwoDaySaleStatistic();

        ReplayTradeSevenDayView replayTradeSevenDayView = new ReplayTradeSevenDayView();
        List<ReplayTradeStatisticView> replayTradeStatisticViewList = Lists.newArrayList();
        replayTradeSaleSevenDayResponses.forEach(r -> {
            ReplayTradeStatisticView view = new ReplayTradeStatisticView();
            view.setDayTime(r.getDayTime());
            view.setTotalPrice(r.getTotalPrice());
            replayTradeStatisticViewList.add(view);
        });
        replayTradeSevenDayView.setReplayTradeStatisticViewList(replayTradeStatisticViewList);
        return replayTradeSevenDayView;
    }

    @Override
    public ReplaySaleStatisticDataView queryTwoDaySaleStatistic(ReplayTradeWareIdRequest request) {

        List<SalesAmountData> salesAmountDataList = replayTradeService.queryTwoDaySalesAmountStatistic();
        List<SalesCaseData> salesCaseDataList = replayTradeService.queryTwoDaySalesCaseStatistic();
        List<SalesOrderData> salesOrderDataList = replayTradeService.queryTwoDaySalesOrderStatistic();
        List<SalesUserData> salesUserDataList = replayTradeService.queryTodayDaySalesUserStatistic();

        // 初始化没有数据的仓库
        initNoDataWare(salesAmountDataList,salesCaseDataList,salesOrderDataList,salesUserDataList,request.getWareIds());

        // 合并今昨日销售金额统计
        List<SalesAmountData> originSalesList = salesAmountDataList.stream().filter(item -> !wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesAmountData> newSpSalesList = salesAmountDataList.stream().filter(item -> wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesAmountDataView> salesAmountDataViewList = originSalesList.stream().map(o -> {
            SalesAmountDataView salesAmountDataView = new SalesAmountDataView();
            salesAmountDataView.setYesterdaySalesPrice(o.getYesterdaySalesPrice().setScale(2, RoundingMode.HALF_UP));
            salesAmountDataView.setTodaySalesPrice(o.getTodaySalesPrice().setScale(2, RoundingMode.HALF_UP));
            salesAmountDataView.setWareId(o.getWareId());
            newSpSalesList.stream().filter(item -> o.getWareId() == wareHouseMap.get(item.getWareId())).findFirst().ifPresent(amountData -> {
                salesAmountDataView.setYesterdaySPSalePrice(amountData.getYesterdaySalesPrice().setScale(2, RoundingMode.HALF_UP));
                salesAmountDataView.setTodaySPSalePrice(amountData.getTodaySalesPrice().setScale(2, RoundingMode.HALF_UP));
            });
            return salesAmountDataView;
        }).collect(Collectors.toList());

        // 合并今日销售箱数统计
        List<SalesCaseData> originCaseDataList = salesCaseDataList.stream().filter(item -> !wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesCaseData> newSpCaseDataList = salesCaseDataList.stream().filter(item -> wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesCaseDataView> salesCaseDataViewList = originCaseDataList.stream().map(o -> {
            SalesCaseDataView salesCaseDataView = new SalesCaseDataView();
            salesCaseDataView.setTodaySalesCase(o.getTodaySalesCase());
            salesCaseDataView.setWareId(o.getWareId());
            newSpCaseDataList.stream().filter(item -> o.getWareId() == wareHouseMap.get(item.getWareId())).findFirst().ifPresent(dataView -> {
                salesCaseDataView.setTodaySPSaleCase(dataView.getTodaySalesCase());
            });
            return salesCaseDataView;
        }).collect(Collectors.toList());

        // 合并今日下单数统计
        List<SalesOrderData>  originOrderDataList = salesOrderDataList.stream().filter(item -> !wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesOrderData>  newSpOrderDataList = salesOrderDataList.stream().filter(item -> wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesOrderDataView> salesOrderDataViewList = originOrderDataList.stream().map(o -> {
            SalesOrderDataView salesOrderDataView = new SalesOrderDataView();
            salesOrderDataView.setTodayOrderCount(o.getTodayOrderCount());
            salesOrderDataView.setWareId(o.getWareId());
            newSpOrderDataList.stream().filter(item -> o.getWareId() == wareHouseMap.get(item.getWareId())).findFirst().ifPresent(dataView -> {
                salesOrderDataView.setTotalSPOrderCount(dataView.getTodayOrderCount());
            });
            return salesOrderDataView;
        }).collect(Collectors.toList());

        // 合并今日下单用户数
        List<SalesUserData>  originUserDataList = salesUserDataList.stream().filter(item -> !wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesUserData>  newSpUserDataList = salesUserDataList.stream().filter(item -> wareHouseMap.containsKey(item.getWareId())).collect(Collectors.toList());
        List<SalesUserDataView> salesUserDataViewList = originUserDataList.stream().map(o -> {
            SalesUserDataView salesUserDataView = new SalesUserDataView();
            salesUserDataView.setUserCount(o.getUserCount());
            salesUserDataView.setWareId(o.getWareId());
            newSpUserDataList.stream().filter(item -> o.getWareId() == wareHouseMap.get(item.getWareId())).findFirst().ifPresent(dataView -> {
                salesUserDataView.setUserSPCount(dataView.getUserCount());
            });

            return salesUserDataView;
        }).collect(Collectors.toList());



        ReplaySaleStatisticDataView replaySaleStatisticDataView = new ReplaySaleStatisticDataView();
        replaySaleStatisticDataView.setSalesAmountDataViewList(salesAmountDataViewList);
        replaySaleStatisticDataView.setSalesCaseDataViewList(salesCaseDataViewList);
        replaySaleStatisticDataView.setSalesOrderDataViewList(salesOrderDataViewList);
        replaySaleStatisticDataView.setSalesUserDataViewList(salesUserDataViewList);


        return replaySaleStatisticDataView;
    }

    @Override
    public ReplaySaleStatisticNewDataView queryTwoDaySaleStatisticNew() {

        // 今昨订单金额
        List<SalesAmountNewData> salesAmountNewDataList = replayTradeService.queryTwoDaySalesAmountStatisticNew();
        List<SalesAmountNewDataView> salesAmountNewDataViews = salesAmountNewDataList.stream().map(o -> {
            SalesAmountNewDataView newDataView = KsBeanUtil.convert(o, SalesAmountNewDataView.class);
            // 入驻商家暂时设置为 0
            newDataView.setThirdTodaySalesPrice(BigDecimal.ZERO);
            newDataView.setThirdYesterdaySalesPrice(BigDecimal.ZERO);
            newDataView.setThirdTowDayTotalSalesPrice(BigDecimal.ZERO);
            return newDataView;
        }).collect(Collectors.toList());

        // 今昨销售箱数
        List<SalesCaseNewData> salesCaseNewDataList = replayTradeService.queryTwoDaySalesCaseStatisticNew();
        List<SalesCaseNewDataView> salesCaseNewDataViews = salesCaseNewDataList.stream().map(o -> {
            SalesCaseNewDataView newDataView = KsBeanUtil.convert(o, SalesCaseNewDataView.class);
            // 入驻商家暂时设置为 0
            newDataView.setThirdTodaySalesCase(BigDecimal.ZERO);
            newDataView.setThirdYesterdaySalesCase(BigDecimal.ZERO);
            newDataView.setThirdTowDaySalesCase(BigDecimal.ZERO);
            return newDataView;
        }).collect(Collectors.toList());

        // 今昨订单数量
        List<SalesOrderNewData> salesOrderNewDataList = replayTradeService.queryTwoDaySalesOrderStatisticNew();
        List<SalesOrderNewDataView> salesOrderNewDataViews = salesOrderNewDataList.stream().map(o -> {
            SalesOrderNewDataView newDataView = KsBeanUtil.convert(o, SalesOrderNewDataView.class);
            // 入驻商家暂时设置为 0
            newDataView.setThirdTodayOrderCount(0L);
            newDataView.setThirdYesterdayOrderCount(0L);
            newDataView.setThirdTowDayOrderCount(0L);
            return newDataView;
        }).collect(Collectors.toList());

        List<SalesUserNewData> salesUserNewDataList = replayTradeService.queryTodayDaySalesUserStatisticNew();
        List<SalesUserNewDataView> salesUserNewDataViews = salesUserNewDataList.stream().map(o -> {
            SalesUserNewDataView newDataView = KsBeanUtil.convert(o, SalesUserNewDataView.class);
            //  入驻商家暂时设置为 0
            newDataView.setThirdUserCount(0L);
            return newDataView;
        }).collect(Collectors.toList());

        ReplaySaleStatisticNewDataView replaySaleStatisticNewDataView = new ReplaySaleStatisticNewDataView();
        replaySaleStatisticNewDataView.setSalesAmountDataViewList(salesAmountNewDataViews);
        replaySaleStatisticNewDataView.setSalesCaseDataViewList(salesCaseNewDataViews);
        replaySaleStatisticNewDataView.setSalesOrderDataViewList(salesOrderNewDataViews);
        replaySaleStatisticNewDataView.setSalesUserDataViewList(salesUserNewDataViews);

        return replaySaleStatisticNewDataView;
    }

    @Override
    public List<TowDayStoreSalePriceView> queryTowDayStoreSalePriceView() {
        return replayTradeService.queryTowDayStoreSalePriceView();
    }

    @Override
    public List<TodayProvinceSalePriceView> queryTodayProvinceSalePriceView() {
        return replayTradeService.queryTodayProvinceSalePriceView();
    }

    @Override
    public List<SevenDaySalePriceView> querySevenDaySalePriceView() {
        return replayTradeService.querySevenDaySalePriceView();
    }

    @Override
    public List<ReplayTradeBuyerStoreResponse> staticsCompanyPayFeeByStartTime(ReplayTradeBuyerStoreQuery query) {
        checkParams(query);
        if (query.getType().equals(1)){
            return replayTradeService.staticsCompanyPayFeeByStartTime(query.getStartTime());
        }else if (query.getType().equals(2)){
            return replayTradeService.staticsCustomerCompanyPayFeeByStartTime(query.getStartTime());
        }else {
          return new ArrayList<>();
        }
    }

    private void checkParams(ReplayTradeBuyerStoreQuery query) {
        if (null == query.getType() || query.getStartTime() == null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"参数为空");
        }
        if (DateUtil.intervalDay(query.getStartTime(),new Date()) > 31){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"最多只能查找一个月数据");
        }
    }

    private void initNoDataWare(List<SalesAmountData> salesAmountDataList,
                                List<SalesCaseData> salesCaseDataList,
                                List<SalesOrderData> salesOrderDataList,
                                List<SalesUserData> salesUserDataList,
                                List<Long> wareIdList) {
        wareIdList.forEach(wareId -> {
            Optional<SalesAmountData> salesAmountDataOptional = salesAmountDataList.stream().filter(item -> item.getWareId() == wareId).findFirst();
            if (!salesAmountDataOptional.isPresent()) {// 存在
                SalesAmountData salesAmountData = new SalesAmountData();
                salesAmountData.setWareId(wareId);
                salesAmountData.setYesterdaySalesPrice(BigDecimal.ZERO);
                salesAmountData.setTodaySalesPrice(BigDecimal.ZERO);
                salesAmountDataList.add(salesAmountData);
            }

            Optional<SalesCaseData> salesCaseDataOptional = salesCaseDataList.stream().filter(item -> item.getWareId() == wareId).findFirst();
            if (!salesCaseDataOptional.isPresent()) {// 存在
                SalesCaseData salesCaseData = new SalesCaseData();
                salesCaseData.setWareId(wareId);
                salesCaseData.setTodaySalesCase(BigDecimal.ZERO);
                salesCaseData.setYesterdaySalesCase(BigDecimal.ZERO);
                salesCaseDataList.add(salesCaseData);
            }

            Optional<SalesOrderData> salesOrderDataOptional = salesOrderDataList.stream().filter(item -> item.getWareId() == wareId).findFirst();
            if (!salesOrderDataOptional.isPresent()) {// 不存在
                SalesOrderData salesOrderData = new SalesOrderData();
                salesOrderData.setWareId(wareId);
                salesOrderData.setTodayOrderCount(0l);
                salesOrderData.setYesterdayOrderCount(0l);
                salesOrderDataList.add(salesOrderData);
            }
            Optional<SalesUserData> salesUserDataOptional = salesUserDataList.stream().filter(item -> item.getWareId() == wareId).findFirst();
            if (!salesUserDataOptional.isPresent()) {// 不存在
                SalesUserData salesUserData = new SalesUserData();
                salesUserData.setWareId(wareId);
                salesUserData.setUserCount(0l);
                salesUserDataList.add(salesUserData);
            }
        });
    }

    public final static HashMap<Long, String> ERP_NO_PREFIX = new HashMap<Long, String>(){
        {
            put(1L,"001-");put(45l,"001-");put(46L,"002-");put(47L,"003-");
            put(49L,"001-");put(50L,"002-");put(51L,"003-");
        }
    };

    /**
     * 查询近七日及本月数据
     */
    @Override
    public SaleSynthesisStatisticDataView queryCurrentMonthSaleStatistic() {
        SaleSynthesisStatisticDataView saleSynthesisStatisticDataView = new SaleSynthesisStatisticDataView();

        //查询本月销售额
        List<SaleSynthesisData> saleAmountSynthesisDataList = replayTradeService.queryCurrentMonthAmountStatistic();
        //查询本月销售箱数
        List<SaleSynthesisData> saleCaseSynthesisDataList = replayTradeService.queryCurrentMonthCaseStatistic();
        //查询本月订单数
        List<SaleSynthesisData> saleOrderSynthesisDataList = replayTradeService.queryCurrentMonthOrderStatistic();
        //查询本月下单用户数
        List<SaleSynthesisData> saleUserSynthesisDataList = replayTradeService.queryCurrentMonthUserStatistic();
        //查询近七天销售箱数
        List<RecentSevenDaySale> recentSevenDaySaleList = replayTradeService.querySevenDayCaseStatistic();
        //查询近七日销售金额
        List<RecentSevenDaySalePirce> recentSevenDaySalePirceList = replayTradeService.querySevenDayPriceStatistic();

        //所有仓库
        HashMap<Long, String> erpNoPrefix = ERP_NO_PREFIX;
        ArrayList<SaleSynthesisDataView> saleSynthesisDataViewArrayList = Lists.newArrayList();
        erpNoPrefix.forEach((k, v) -> {
            SaleSynthesisDataView saleSynthesisDataView = new SaleSynthesisDataView();

            if (CollectionUtils.isNotEmpty(saleAmountSynthesisDataList)) {
                SaleSynthesisData saleAmountSynthesisData = saleAmountSynthesisDataList.stream().filter(s -> s.getWareId().equals(k)).findFirst().orElse(null);
                if (Objects.nonNull(saleAmountSynthesisData)) {
                    saleSynthesisDataView.setSalePrice(saleAmountSynthesisData.getSalePrice());
                }
            }

            if (CollectionUtils.isNotEmpty(saleCaseSynthesisDataList)) {
                SaleSynthesisData saleCaseSynthesisData = saleCaseSynthesisDataList.stream().filter(s -> s.getWareId().equals(k)).findFirst().orElse(null);
                if (Objects.nonNull(saleCaseSynthesisData)) {
                    saleSynthesisDataView.setOrderItemNum(saleCaseSynthesisData.getOrderItemNum());
                }
            }

            if (CollectionUtils.isNotEmpty(saleOrderSynthesisDataList)) {
                SaleSynthesisData saleOrderSynthesisData = saleOrderSynthesisDataList.stream().filter(s -> s.getWareId().equals(k)).findFirst().orElse(null);
                if (Objects.nonNull(saleOrderSynthesisData)) {
                    saleSynthesisDataView.setOrderNum(saleOrderSynthesisData.getOrderNum());
                }
            }

            if (CollectionUtils.isNotEmpty(saleUserSynthesisDataList)) {
                SaleSynthesisData saleUserSynthesisData = saleUserSynthesisDataList.stream().filter(s -> s.getWareId().equals(k)).findFirst().orElse(null);
                if (Objects.nonNull(saleUserSynthesisData)) {
                    saleSynthesisDataView.setBuyNum(saleUserSynthesisData.getUserNum());
                }
            }

            if (CollectionUtils.isNotEmpty(recentSevenDaySaleList)) {
                List<RecentSevenDaySale> sevenDaySaleList = recentSevenDaySaleList.stream().filter(s -> s.getWareId().equals(k)).collect(Collectors.toList());

                List<RecentSevenDaySalePirce> sevenDaySalePirceList = recentSevenDaySalePirceList.stream().filter(s -> s.getWareId().equals(k)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(sevenDaySaleList)) {
                    saleSynthesisDataView.setRecentSevenDaySaleTotalCaseList(KsBeanUtil.convertList(sevenDaySaleList, RecentSevenDaySaleView.class));
                }

                if (CollectionUtils.isNotEmpty(sevenDaySalePirceList)) {
                    saleSynthesisDataView.setRecentSevenDaySaleTotalPriceList(KsBeanUtil.convertList(sevenDaySalePirceList, RecentSevenDaySalePriceView.class));
                }
            }

            if (Objects.nonNull(saleSynthesisDataView)) {
                saleSynthesisDataView.setWareId(k);
                saleSynthesisDataViewArrayList.add(saleSynthesisDataView);
            }
        });

        saleSynthesisStatisticDataView.setSaleSynthesisDataViewList(saleSynthesisDataViewArrayList);
        return saleSynthesisStatisticDataView;
    }

    /**
     * 查询省份近七日及本月数据
     */
    @Override
    public SaleSynthesisStatisticNewDataView queryCurrentMonthSaleStatisticNew() {

        List<SaleSynthesisNewData> saleAmountSynthesisDataList = replayTradeService.queryCurrentMonthAmountStatisticNew();
        List<SaleSynthesisNewData> saleCaseSynthesisDataList = replayTradeService.queryCurrentMonthCaseStatisticNew();
        List<SaleSynthesisNewData> saleOrderSynthesisDataList = replayTradeService.queryCurrentMonthOrderStatisticNew();
        List<SaleSynthesisNewData> saleUserSynthesisDataList = replayTradeService.queryCurrentMonthUserStatisticNew();
        List<RecentSevenDaySaleNew> recentSevenDaySaleList = replayTradeService.querySevenDayCaseStatisticNew();
        List<RecentSevenDaySalePirceNew> recentSevenDaySalePirceList = replayTradeService.querySevenDayPriceStatisticNew();

        Map<String, SaleSynthesisNewData> saleAmountSynthesisDataMap = saleAmountSynthesisDataList.stream().collect(Collectors.toMap(SaleSynthesisNewData::getProvinceId, o -> o));
        Map<String, SaleSynthesisNewData> saleSynthesisNewDataListMap = saleCaseSynthesisDataList.stream().collect(Collectors.toMap(SaleSynthesisNewData::getProvinceId, o -> o));
        Map<String, SaleSynthesisNewData> orderMap = saleOrderSynthesisDataList.stream().collect(Collectors.toMap(SaleSynthesisNewData::getProvinceId, o -> o));
        Map<String, SaleSynthesisNewData> userMap = saleUserSynthesisDataList.stream().collect(Collectors.toMap(SaleSynthesisNewData::getProvinceId, o -> o));

        List<SaleSynthesisNewDataView> saleSynthesisDataViewList = new ArrayList<>();
        saleAmountSynthesisDataList.forEach(data ->{
            SaleSynthesisNewDataView saleSynthesisDataView = SaleSynthesisNewDataView
                    .builder()
                    .provinceId(data.getProvinceId())
                    .provinceName(data.getProvinceName())
                    .build();

            if (Objects.nonNull(saleAmountSynthesisDataMap.get(data.getProvinceId()))) {
                saleSynthesisDataView.setSalePrice(saleAmountSynthesisDataMap.get(data.getProvinceId()).getSalePrice());
            }

            if (Objects.nonNull(saleSynthesisNewDataListMap.get(data.getProvinceId()))) {
                saleSynthesisDataView.setOrderItemNum(saleSynthesisNewDataListMap.get(data.getProvinceId()).getOrderItemNum());
            }

            if (Objects.nonNull(orderMap.get(data.getProvinceId()))) {
                saleSynthesisDataView.setOrderNum(orderMap.get(data.getProvinceId()).getOrderNum());
            }

            if (Objects.nonNull(userMap.get(data.getProvinceId()))) {
                saleSynthesisDataView.setBuyNum(userMap.get(data.getProvinceId()).getUserNum());
            }

            List<String> sevenDayStr = querySevenDayStr();

            Map<String, RecentSevenDaySaleNew> sevenDaySaleMap = recentSevenDaySaleList.stream()
                    .filter(o -> Objects.equals(data.getProvinceId(), o.getProvinceId()))
                    .collect(Collectors.toMap(RecentSevenDaySaleNew::getDayTime, o -> o));

            List<RecentSevenDaySaleNewView> recentSevenDaySaleTotalCaseList = new ArrayList<>();

            for (String dayStr : sevenDayStr) {
                RecentSevenDaySaleNewView saleNewView = RecentSevenDaySaleNewView
                        .builder()
                        .provinceId(data.getProvinceId())
                        .provinceName(data.getProvinceName())
                        .dayTime(dayStr)
                        .totalNum(0L)
                        .build();

                if (Objects.nonNull(sevenDaySaleMap.get(dayStr))) {
                    saleNewView.setTotalNum(sevenDaySaleMap.get(dayStr).getTotalNum());
                }

                recentSevenDaySaleTotalCaseList.add(saleNewView);
            }
            saleSynthesisDataView.setRecentSevenDaySaleTotalCaseList(recentSevenDaySaleTotalCaseList);

            Map<String, RecentSevenDaySalePirceNew> sevenDaySalePirceMap = recentSevenDaySalePirceList.stream()
                    .filter(o -> Objects.equals(data.getProvinceId(), o.getProvinceId()))
                    .collect(Collectors.toMap(RecentSevenDaySalePirceNew::getDayTime, o -> o));

            List<RecentSevenDaySalePriceNewView> recentSevenDaySaleTotalPriceList = new ArrayList<>();

            for (String dayStr : sevenDayStr) {
                RecentSevenDaySalePriceNewView priceNewView = RecentSevenDaySalePriceNewView
                        .builder()
                        .provinceId(data.getProvinceId())
                        .provinceName(data.getProvinceName())
                        .dayTime(dayStr)
                        .daySalePrice(BigDecimal.ZERO)
                        .build();

                if (Objects.nonNull(sevenDaySalePirceMap.get(dayStr))) {
                    priceNewView.setDaySalePrice(sevenDaySalePirceMap.get(dayStr).getDaySalePrice());
                }
                recentSevenDaySaleTotalPriceList.add(priceNewView);
            }
            saleSynthesisDataView.setRecentSevenDaySaleTotalPriceList(recentSevenDaySaleTotalPriceList);

            saleSynthesisDataViewList.add(saleSynthesisDataView);
        });

        SaleSynthesisStatisticNewDataView saleSynthesisStatisticDataView = new SaleSynthesisStatisticNewDataView();
        saleSynthesisStatisticDataView.setSaleSynthesisDataViewList(saleSynthesisDataViewList);

        return saleSynthesisStatisticDataView;
    }

    private List<String> querySevenDayStr() {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i > -7) {
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE,i);
            String format = DateUtil.format(cal.getTime(), DateUtil.FMT_DATE_1);
            result.add(format);
            i--;
        }
        return result;
    }

    @Override
    public List<CustomerTradeItemResponse> queryCustomerTradeItemByCid(CustomerTradeRequest customerTradeRequest) {
        List<CustomerTradeItemResponse> result = replayTradeService.queryCustomerTradeItemByCid(customerTradeRequest.getCustomerIds().get(0), customerTradeRequest.getDate());
        return result;
    }

    /**
     * 查询本月下单用户详细数据
     *
     * @param customerTradeRequest
     * @return
     */
    @Override
    public List<CustomerTradeResponse> queryCustomerTradeDetail(CustomerTradeRequest customerTradeRequest) {
        List<CustomerTradeResponse> result = replayTradeService.queryCustomerTradeDetail(customerTradeRequest);
        return result;
    }

    @Override
    public List<BossCustomerTradeResponse> queryAllTradeByDate(BossCustomerTradeRequest request) {
        return replayTradeService.queryAllTradeByDate(request);
    }


}
