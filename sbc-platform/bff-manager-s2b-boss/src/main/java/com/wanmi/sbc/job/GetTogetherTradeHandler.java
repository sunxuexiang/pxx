package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.view.replay.ReplaySaleStatisticDataView;
import com.wanmi.ares.view.replay.SaleSynthesisStatisticDataView;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.response.trade.TradeSaleStatisticResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JobHandler(value="getTogetherTradeHandler")
@Component
@Slf4j
public class GetTogetherTradeHandler extends IJobHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired RedisService redisService;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        String dayKey = "DAY";
        String monthKey = "MONTH";
        String dayPriceKey = "DAY_PRICE";
        String weekKey = "WEEK";
        //今日销售统计
        BaseResponse<TradeSaleStatisticResponse> pileTradesByDate = pileTradeQueryProvider.getPileTradesByDate(NumberUtils.INTEGER_ZERO);
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, dayKey,  JSONObject.toJSONString(pileTradesByDate.getContext(), SerializerFeature.WriteMapNullValue));
        //本月销售统计
        BaseResponse<TradeSaleStatisticResponse> pileTradesByMonth = pileTradeQueryProvider.getPileTradesByDate(NumberUtils.INTEGER_ONE);
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey, JSONObject.toJSONString(pileTradesByMonth.getContext(), SerializerFeature.WriteMapNullValue));
        //囤货提货数据统计（今昨日）
        BaseResponse<TradeSaleStatisticResponse> pileAndTradesPriceByDate = pileTradeQueryProvider.getPileAndTradesPriceByDate();
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, dayPriceKey,JSONObject.toJSONString(pileAndTradesPriceByDate.getContext(), SerializerFeature.WriteMapNullValue));
        //近七日销售统计
        BaseResponse<TradeSaleStatisticResponse> tradeSaleStatisticResponseBaseResponse = pileTradeQueryProvider.recentSevenDaySaleStatistic();
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, weekKey, JSONObject.toJSONString(tradeSaleStatisticResponseBaseResponse.getContext(), SerializerFeature.WriteMapNullValue));
        return SUCCESS;
    }

//    @Override
//    public ReturnT<String> execute(String param) throws Exception {
//        String amountKey = "AMOUNT";
//        String caseKey = "CASE";
//        String orderKey = "ORDER";
//        String userKey = "USER";
//        String monthKey = "CURRENTMONTH";
//
//        Map<Long, List<WareHouseVO>> wareMap = commonUtil.queryAllWareHouses().stream().collect(Collectors.groupingBy(WareHouseVO::getWareId));
//
//        ReplaySaleStatisticDataView replaySaleStatisticDataView = replayTradeQueryProvider.queryTwoDaySaleStatistic();
//        //设置仓库名
//        if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesAmountDataViewList())){
//            replaySaleStatisticDataView.getSalesAmountDataViewList().forEach(s->{
//                List<WareHouseVO> wareHouseVOS = wareMap.get(s.getWareId());
//                if(CollectionUtils.isNotEmpty(wareHouseVOS)){
//                    WareHouseVO wareHouseVO = wareHouseVOS.get(0);
//                    if(Objects.nonNull(wareHouseVO)){
//                        s.setWareHouse(wareHouseVO.getWareName());
//                    }
//                }
//            });
//        }
//
//        if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesCaseDataViewList())){
//            replaySaleStatisticDataView.getSalesCaseDataViewList().forEach(s->{
//                List<WareHouseVO> wareHouseVOS = wareMap.get(s.getWareId());
//                if(CollectionUtils.isNotEmpty(wareHouseVOS)){
//                    WareHouseVO wareHouseVO = wareHouseVOS.get(0);
//                    if(Objects.nonNull(wareHouseVO)){
//                        s.setWareHouse(wareHouseVO.getWareName());
//                    }
//                }
//            });
//        }
//
//        if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesOrderDataViewList())){
//            replaySaleStatisticDataView.getSalesOrderDataViewList().forEach(s->{
//                List<WareHouseVO> wareHouseVOS = wareMap.get(s.getWareId());
//                if(CollectionUtils.isNotEmpty(wareHouseVOS)){
//                    WareHouseVO wareHouseVO = wareHouseVOS.get(0);
//                    if(Objects.nonNull(wareHouseVO)){
//                        s.setWareHouse(wareHouseVO.getWareName());
//                    }
//                }
//            });
//        }
//
//        if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesUserDataViewList())){
//            replaySaleStatisticDataView.getSalesUserDataViewList().forEach(s->{
//                List<WareHouseVO> wareHouseVOS = wareMap.get(s.getWareId());
//                if(CollectionUtils.isNotEmpty(wareHouseVOS)){
//                    WareHouseVO wareHouseVO = wareHouseVOS.get(0);
//                    if(Objects.nonNull(wareHouseVO)){
//                        s.setWareHouse(wareHouseVO.getWareName());
//                    }
//                }
//            });
//        }
//
//        SaleSynthesisStatisticDataView saleSynthesisStatisticDataView = replayTradeQueryProvider.queryCurrentMonthSaleStatistic();
//
//        if(CollectionUtils.isNotEmpty(saleSynthesisStatisticDataView.getSaleSynthesisDataViewList())){
//            saleSynthesisStatisticDataView.getSaleSynthesisDataViewList().forEach(s->{
//                List<WareHouseVO> wareHouseVOS = wareMap.get(s.getWareId());
//                if(CollectionUtils.isNotEmpty(wareHouseVOS)){
//                    WareHouseVO wareHouseVO = wareHouseVOS.get(0);
//                    if(Objects.nonNull(wareHouseVO)){
//                        s.setWareHouse(wareHouseVO.getWareName());
//                    }
//                }
//            });
//        }
//
//        //今昨日销售金额
//        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesAmountDataViewList()));
//        //今昨日销售箱数
//        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesCaseDataViewList()));
//        //今昨日订单数
//        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesOrderDataViewList()));
//        //今昨日下单用户数
//        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, userKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesUserDataViewList()));
//        //本月及近七天销售箱数
//        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey, JSONObject.toJSONString(saleSynthesisStatisticDataView.getSaleSynthesisDataViewList()));
//
//        return SUCCESS;
//    }

}
