package com.wanmi.sbc.datacenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.ares.view.replay.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.datacenter.response.ReplaySaleStatisticDataResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.redis.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class OrderDataController {

//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private RedisService redisService;
//
//    @ApiOperation(value = "销售统计")
//    @RequestMapping(value = "/getTogether", method = RequestMethod.POST)
//    public BaseResponse getTogether(){
//        String dayKey = "DAY";
//        String monthKey = "MONTH";
//        String dayPriceKey = "DAY_PRICE";
//        String weekKey = "WEEK";
//        Map map = new HashMap();
//        map.put(dayKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey)) || "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey))?"": JSONObject.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayKey),SerializerFeature.WriteMapNullValue)));
//        map.put(monthKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey))|| "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,monthKey),SerializerFeature.WriteMapNullValue)));
//        map.put(dayPriceKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey)) || "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,dayPriceKey),SerializerFeature.WriteMapNullValue)));
//        map.put(weekKey, JSON.parse(StringUtils.isEmpty(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey))|| "null".equalsIgnoreCase(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey))?"":JSON.toJSONString(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER,weekKey),SerializerFeature.WriteMapNullValue)));
//        return BaseResponse.success(map);
//    }
//
//    @ApiOperation(value = "销售数据统计")
//    @RequestMapping(value = "/queryTogether", method = RequestMethod.POST)
//    public BaseResponse<ReplaySaleStatisticDataResponse> queryTogether(){
//
//        String amountKey = "AMOUNT";
//        String caseKey = "CASE";
//        String orderKey = "ORDER";
//        String userKey = "USER";
//        String monthKey = "CURRENTMONTH";
//
//        //金额
//        List<SalesAmountDataView> salesAmountDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey)
//                , SalesAmountDataView.class);
//
//        List<SalesCaseDataView> salesCaseDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey)
//                , SalesCaseDataView.class);
//
//        List<SalesOrderDataView> salesOrderDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey)
//               , SalesOrderDataView.class);
//
//        List<SalesUserDataView> salesUserDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, userKey)
//                , SalesUserDataView.class);
//
//        List<SaleSynthesisDataView> saleSynthesisDataViews = JSON.parseArray(redisService.hget(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey)
//                , SaleSynthesisDataView.class);
//
//        ReplaySaleStatisticDataResponse replaySaleStatisticDataResponse = new ReplaySaleStatisticDataResponse();
//        replaySaleStatisticDataResponse.setSalesAmountDataViewList(salesAmountDataViews);
//        replaySaleStatisticDataResponse.setSalesCaseDataViewList(salesCaseDataViews);
//        replaySaleStatisticDataResponse.setSalesOrderDataViewList(salesOrderDataViews);
//        replaySaleStatisticDataResponse.setSalesUserDataViewList(salesUserDataViews);
//        replaySaleStatisticDataResponse.setSaleSynthesisDataViewList(saleSynthesisDataViews);
//
//        return BaseResponse.success(replaySaleStatisticDataResponse);
//    }
}
