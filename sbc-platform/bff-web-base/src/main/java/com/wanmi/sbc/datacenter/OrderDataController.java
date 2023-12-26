package com.wanmi.sbc.datacenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.view.replay.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.datacenter.response.ReplaySaleStatisticDataResponse;
import com.wanmi.sbc.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据中心订单统计
 *
 * @author liaozhaohong
 * @date 2022/1/06
 */
@Api(tags = "OrderDataController", description = "数据中心订单统计")
@RestController
@RequestMapping("/dataCenter")
@Validated
@Slf4j
public class OrderDataController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    public static final Map<String,String> PROVINCE_MAP = new LinkedHashMap<String, String>(){
        {
            this.put("430000", "湖南省");
            this.put("420000", "湖北省");
            this.put("360000", "江西省");
        }
    };

    @ApiOperation(value = "销售统计")
    @RequestMapping(value = "/getTogether", method = RequestMethod.POST)
    public BaseResponse getTogether(){
        String dayKey = "DAY";
        String monthKey = "MONTH";
        String dayPriceKey = "DAY_PRICE";
        String weekKey = "WEEK";
        Map map = new HashMap();
        map.put(dayKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey)) || "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey))?"": JSONObject.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey), SerializerFeature.WriteMapNullValue)));
        map.put(monthKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey))|| "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey),SerializerFeature.WriteMapNullValue)));
        map.put(dayPriceKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey)) || "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey),SerializerFeature.WriteMapNullValue)));
        map.put(weekKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey))|| "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey),SerializerFeature.WriteMapNullValue)));
        return BaseResponse.success(map);
    }

    @ApiOperation(value = "销售数据统计")
    @RequestMapping(value = "/queryTogether", method = RequestMethod.POST)
    public BaseResponse<ReplaySaleStatisticDataResponse> queryTogether(){


        Claims claims = (Claims) HttpUtil.getRequest().getAttribute("claims");
        log.info("OrderDataController.queryTogether()--->"+claims);
        if (claims == null || claims.get("employAccount") == null) {
            log.info("OrderDataController.queryTogether()--->"+claims.get("employeeId"));
            return BaseResponse.FAILED();
        }
        String employAccount = claims.get("employAccount").toString();
        log.info("OrderDataController.queryTogether()--->"+employAccount);
        if("13333333333".equals(employAccount)){
            return BaseResponse.FAILED();
        }

        String amountKey = "AMOUNT";
        String caseKey = "CASE";
        String orderKey = "ORDER";
        String userKey = "USER";
        String monthKey = "CURRENTMONTH";

        //金额
        List<SalesAmountDataView> salesAmountDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey)
                , SalesAmountDataView.class);

        List<SalesCaseDataView> salesCaseDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey)
                , SalesCaseDataView.class);

        List<SalesOrderDataView> salesOrderDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey)
                , SalesOrderDataView.class);

        List<SalesUserDataView> salesUserDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, userKey)
                , SalesUserDataView.class);

        List<SaleSynthesisDataView> saleSynthesisDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey)
                , SaleSynthesisDataView.class);

        ReplaySaleStatisticDataResponse replaySaleStatisticDataResponse = new ReplaySaleStatisticDataResponse();
        replaySaleStatisticDataResponse.setSalesAmountDataViewList(salesAmountDataViews);
        replaySaleStatisticDataResponse.setSalesCaseDataViewList(salesCaseDataViews);
        replaySaleStatisticDataResponse.setSalesOrderDataViewList(salesOrderDataViews);
        replaySaleStatisticDataResponse.setSalesUserDataViewList(salesUserDataViews);
        replaySaleStatisticDataResponse.setSaleSynthesisDataViewList(saleSynthesisDataViews);

        return BaseResponse.success(replaySaleStatisticDataResponse);
    }


    @ApiOperation(value = "销售数据统计（新）")
    @RequestMapping(value = "/queryTogetherNew", method = RequestMethod.POST)
    public BaseResponse<Map<String,Object>> queryTogetherNew(){

        String amountKey = "AMOUNT_NEW";
        String caseKey = "CASE_NEW";
        String orderKey = "ORDER_NEW";
        String userKey = "USER_NEW";
        String monthKey = "CURRENT_MONTH_NEW";

        List<SalesAmountNewDataView> salesAmountDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey), SalesAmountNewDataView.class);

        List<SalesCaseNewDataView> salesCaseDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey), SalesCaseNewDataView.class);

        List<SalesOrderNewDataView> salesOrderDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey), SalesOrderNewDataView.class);

        List<SalesUserNewDataView> salesUserDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, userKey), SalesUserNewDataView.class);

        List<SaleSynthesisNewDataView> saleSynthesisDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey), SaleSynthesisNewDataView.class);

//        ReplaySaleStatisticNewDataResponse replaySaleStatisticDataResponse = new ReplaySaleStatisticNewDataResponse();
//        replaySaleStatisticDataResponse.setSalesAmountDataViewList(salesAmountDataViews);
//        replaySaleStatisticDataResponse.setSalesCaseDataViewList(salesCaseDataViews);
//        replaySaleStatisticDataResponse.setSalesOrderDataViewList(salesOrderDataViews);
//        replaySaleStatisticDataResponse.setSalesUserDataViewList(salesUserDataViews);
//        replaySaleStatisticDataResponse.setSaleSynthesisDataViewList(saleSynthesisDataViews);
//        return BaseResponse.success(replaySaleStatisticDataResponse);
        HashMap<String, Object> result = new LinkedHashMap<>();


        BigDecimal todayAmount = salesAmountDataViews.stream().map(o -> o.getTodaySalesPrice().add(o.getThirdTodaySalesPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal yesterdayAmount = salesAmountDataViews.stream().map(o -> o.getYesterdaySalesPrice().add(o.getThirdYesterdaySalesPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
        HashMap<String, Object> amountData = new HashMap<>();
        amountData.put("todayAmount", todayAmount);
        amountData.put("yesterdayAmount", yesterdayAmount);
        //

        BigDecimal todayCase = salesCaseDataViews.stream().map(o -> o.getTodaySalesCase().add(o.getThirdTodaySalesCase())).reduce(BigDecimal.ZERO, BigDecimal::add);
        HashMap<String, Object> caseData = new HashMap<>();
        caseData.put("todayCase", todayCase);
        //

        Long todayUser = salesUserDataViews.stream().mapToLong(o -> o.getUserCount() + o.getThirdUserCount()).sum();
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("todayUser", todayUser);
        //

        Long todayOrder = salesOrderDataViews.stream().mapToLong(o -> o.getTodayOrderCount() + o.getThirdTodayOrderCount()).sum();
        HashMap<String, Object> orderData = new HashMap<>();
        orderData.put("todayOrder", todayOrder);
        //

        List<SaleSynthesisNewDataView> monthList = new ArrayList<>();
        BigDecimal salePrice = saleSynthesisDataViews.stream().map(SaleSynthesisNewDataView::getSalePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer buyNum = saleSynthesisDataViews.stream().mapToInt(SaleSynthesisNewDataView::getBuyNum).sum();
        Integer orderNum = saleSynthesisDataViews.stream().mapToInt(SaleSynthesisNewDataView::getOrderNum).sum();
        Long orderItemNum = saleSynthesisDataViews.stream().mapToLong(SaleSynthesisNewDataView::getOrderItemNum).sum();
        SaleSynthesisNewDataView dataView = new SaleSynthesisNewDataView();
        dataView.setProvinceId("00000");
        dataView.setProvinceName("全部");
        dataView.setSalePrice(salePrice);
        dataView.setBuyNum(buyNum);
        dataView.setOrderNum(orderNum);
        dataView.setOrderItemNum(orderItemNum);

        List<RecentSevenDaySaleNewView> allCaseList =new ArrayList<>();
        List<RecentSevenDaySalePriceNewView> allPriceList = new ArrayList<>();
        saleSynthesisDataViews.forEach(o -> {
            allCaseList.addAll(o.getRecentSevenDaySaleTotalCaseList());
            allPriceList.addAll(o.getRecentSevenDaySaleTotalPriceList());
        });

        Map<String, List<RecentSevenDaySaleNewView>> allCaseMap = allCaseList.stream().collect(Collectors.groupingBy(RecentSevenDaySaleNewView::getDayTime));
        Map<String, List<RecentSevenDaySalePriceNewView>> allPriceMap = allPriceList.stream().collect(Collectors.groupingBy(RecentSevenDaySalePriceNewView::getDayTime));

        List<String> sevenDayStr = querySevenDayStr();

        List<RecentSevenDaySaleNewView> sevenCaseList = new ArrayList<>();
        List<RecentSevenDaySalePriceNewView> sevenPriceList = new ArrayList<>();
        sevenDayStr.forEach(dayStr -> {

            RecentSevenDaySaleNewView view = RecentSevenDaySaleNewView
                    .builder()
                    .provinceId("00000")
                    .provinceName("全部")
                    .dayTime(dayStr)
                    .totalNum(0L)
                    .build();

            if (Objects.nonNull(allCaseMap.get(dayStr))) {
                List<RecentSevenDaySaleNewView> viewList = allCaseMap.get(dayStr);
                long total = viewList.stream().mapToLong(RecentSevenDaySaleNewView::getTotalNum).sum();
                view.setTotalNum(total);
            }
            sevenCaseList.add(view);

            RecentSevenDaySalePriceNewView priceNewView = RecentSevenDaySalePriceNewView
                    .builder()
                    .provinceId("00000")
                    .provinceName("全部")
                    .dayTime(dayStr)
                    .daySalePrice(BigDecimal.ZERO)
                    .build();
            if (Objects.nonNull(allPriceMap.get(dayStr))) {
                List<RecentSevenDaySalePriceNewView> priceNewViewList = allPriceMap.get(dayStr);
                BigDecimal total = priceNewViewList.stream().map(RecentSevenDaySalePriceNewView::getDaySalePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                priceNewView.setDaySalePrice(total);
            }
            sevenPriceList.add(priceNewView);
        });
        dataView.setRecentSevenDaySaleTotalCaseList(sevenCaseList);
        dataView.setRecentSevenDaySaleTotalPriceList(sevenPriceList);
        monthList.add(dataView);

        List<Map<String, Object>> amountList = new ArrayList<>();
        List<Map<String, Object>> caseList = new ArrayList<>();
        List<Map<String, Object>> userList = new ArrayList<>();
        List<Map<String, Object>> orderList = new ArrayList<>();
        Map<String, SalesAmountNewDataView> amountMap = salesAmountDataViews.stream().collect(Collectors.toMap(SalesAmountNewDataView::getProvinceId, Function.identity(), (v1, v2) -> v1));
        Map<String, SalesCaseNewDataView> caseMap = salesCaseDataViews.stream().collect(Collectors.toMap(SalesCaseNewDataView::getProvinceId, Function.identity(), (v1, v2) -> v1));
        Map<String, SalesUserNewDataView> userMap = salesUserDataViews.stream().collect(Collectors.toMap(SalesUserNewDataView::getProvinceId, Function.identity(), (v1, v2) -> v1));
        Map<String, SalesOrderNewDataView> orderMap = salesOrderDataViews.stream().collect(Collectors.toMap(SalesOrderNewDataView::getProvinceId, Function.identity(), (v1, v2) -> v1));
        Map<String, SaleSynthesisNewDataView> monthMap = saleSynthesisDataViews.stream().collect(Collectors.toMap(SaleSynthesisNewDataView::getProvinceId, Function.identity(), (v1, v2) -> v1));

        PROVINCE_MAP.forEach((k, v) -> {
            Map<String, Object> amountItem = new HashMap<>();
            amountItem.put("provinceId", k);
            amountItem.put("provinceName", v);
            amountItem.put("total", BigDecimal.ZERO);
            amountItem.put("today", BigDecimal.ZERO);
            amountItem.put("yesterday", BigDecimal.ZERO);
            amountItem.put("self", BigDecimal.ZERO);
            amountItem.put("third", BigDecimal.ZERO);
            if (Objects.nonNull(amountMap.get(k))) {
                SalesAmountNewDataView data = amountMap.get(k);
                BigDecimal today = data.getTodaySalesPrice().add(data.getThirdTodaySalesPrice());
                BigDecimal yesterday = data.getYesterdaySalesPrice().add(data.getThirdYesterdaySalesPrice());
                amountItem.put("total", today.add(yesterday)); // 销售金额共
                amountItem.put("today", today); // 今日金额
                amountItem.put("yesterday", yesterday); // 昨日金额
                amountItem.put("self", data.getTodaySalesPrice()); // 大白鲸今日金额
                amountItem.put("third", data.getThirdTodaySalesPrice()); // 入驻商家今日金额
            }
            amountList.add(amountItem);

            Map<String, Object> caseItem = new HashMap<>();
            caseItem.put("provinceId", k);
            caseItem.put("provinceName", v);
            caseItem.put("total", BigDecimal.ZERO);
            caseItem.put("self", BigDecimal.ZERO);
            caseItem.put("third", BigDecimal.ZERO);
            if (Objects.nonNull(caseMap.get(k))) {
                SalesCaseNewDataView data = caseMap.get(k);
                caseItem.put("total", data.getTodaySalesCase().add(data.getThirdTodaySalesCase()));
                caseItem.put("self", data.getTodaySalesCase());
                caseItem.put("third", data.getThirdTodaySalesCase());
            }
            caseList.add(caseItem);

            Map<String, Object> userItem = new HashMap<>();
            userItem.put("provinceId", k);
            userItem.put("provinceName", v);
            userItem.put("total", 0L);
            userItem.put("self", 0L);
            userItem.put("third", 0L);
            if (Objects.nonNull(userMap.get(k))) {
                SalesUserNewDataView data = userMap.get(k);
                userItem.put("total", data.getUserCount() + data.getThirdUserCount());
                userItem.put("self", data.getUserCount());
                userItem.put("third", data.getThirdUserCount());
            }
            userList.add(userItem);

            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("provinceId", k);
            orderItem.put("provinceName", v);
            orderItem.put("total", 0L);
            orderItem.put("self", 0L);
            orderItem.put("third", 0L);
            if (Objects.nonNull(orderMap.get(k))) {
                SalesOrderNewDataView data = orderMap.get(k);
                orderItem.put("total", data.getTodayOrderCount() + data.getThirdTodayOrderCount());
                orderItem.put("self", data.getTodayOrderCount());
                orderItem.put("third", data.getThirdTodayOrderCount());
            }
            orderList.add(orderItem);

            if (Objects.nonNull(monthMap.get(k))) {
                monthList.add(monthMap.get(k));
            }
        });

        amountData.put("amountList", amountList);
        caseData.put("caseList", caseList);
        userData.put("userList", userList);
        orderData.put("orderList", orderList);

        result.put("amountData", amountData);
        result.put("caseData", caseData);
        result.put("userData", userData);
        result.put("orderData", orderData);



        result.put("monthList", monthList);

        return BaseResponse.success(result);
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

    @ApiOperation(value = "数据看板-23年10月")
    @GetMapping("/queryTradeData")
    public BaseResponse<Map<String,Object>> queryTradeData(){
        HashMap<String, Object> result = new LinkedHashMap<>();
        // 今昨总销售金额
        HashMap<String, Object> towDayTotalSalePrice = new LinkedHashMap<>();
        towDayTotalSalePrice.put("todaySalesPrice", new BigDecimal("0.00"));
        towDayTotalSalePrice.put("yesterdaySalesPrice", new BigDecimal("0.00"));

        // 美美休闲食品家 今天昨销售金额
        HashMap<String, Object> towDayMeiMeiSalePrice = new LinkedHashMap<>();
        towDayMeiMeiSalePrice.put("todaySalesPrice", new BigDecimal("0.00"));
        towDayMeiMeiSalePrice.put("yesterdaySalesPrice", new BigDecimal("0.00"));

        List<TowDayStoreSalePriceView> towDayStoreSalePriceViews = replayTradeQueryProvider.queryTowDayStoreSalePriceView();
        if (!CollectionUtils.isEmpty(towDayStoreSalePriceViews)) {
            towDayTotalSalePrice.put("todaySalesPrice",towDayStoreSalePriceViews.stream().map(TowDayStoreSalePriceView::getTodaySalesPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            towDayTotalSalePrice.put("yesterdaySalesPrice",towDayStoreSalePriceViews.stream().map(TowDayStoreSalePriceView::getYesterdaySalesPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            towDayStoreSalePriceViews.stream().filter(o -> Objects.equals("1230", o.getCompanyId())).findFirst().ifPresent(o ->{
                towDayMeiMeiSalePrice.put("todaySalesPrice", o.getTodaySalesPrice());
                towDayMeiMeiSalePrice.put("yesterdaySalesPrice", o.getYesterdaySalesPrice());
            });
        }
        result.put("towDayTotalSalePrice", towDayTotalSalePrice);
        result.put("towDayMeiMeiSalePrice", towDayMeiMeiSalePrice);

        // 各省商家销售总金额
        List<TodayProvinceSalePriceView> todayProvinceSalePriceViews = replayTradeQueryProvider.queryTodayProvinceSalePriceView();
        result.put("todayProvinceSalePrice", todayProvinceSalePriceViews);

        // 平台7天销售金额
        List<SevenDaySalePriceView> sevenDaySalePriceViews = replayTradeQueryProvider.querySevenDaySalePriceView();
        result.put("sevenDaySalePrice", sevenDaySalePriceViews);

        return BaseResponse.success(result);
    }
}
