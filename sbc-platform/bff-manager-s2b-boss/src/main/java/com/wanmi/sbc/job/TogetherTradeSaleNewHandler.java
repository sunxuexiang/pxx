package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.view.replay.ReplaySaleStatisticNewDataView;
import com.wanmi.ares.view.replay.SaleSynthesisStatisticNewDataView;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@JobHandler(value="togetherTradeSaleNewHandler")
@Component
@Slf4j
@RequestMapping("togetherTradeSaleNewHandler")
public class TogetherTradeSaleNewHandler extends IJobHandler {

    @Autowired
    RedisService redisService;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    @Override
    @GetMapping("execute")
    public ReturnT<String> execute(String s) throws Exception {
        String amountKey = "AMOUNT_NEW";
        String caseKey = "CASE_NEW";
        String orderKey = "ORDER_NEW";
        String userKey = "USER_NEW";
        String monthKey = "CURRENT_MONTH_NEW";
        log.info("TogetherTradeSaleNewHandler =========》 同步开始");

        ReplaySaleStatisticNewDataView replaySaleStatisticNewDataView = replayTradeQueryProvider.queryTwoDaySaleStatisticNew();

        SaleSynthesisStatisticNewDataView saleSynthesisStatisticNewDataView = replayTradeQueryProvider.queryCurrentMonthSaleStatisticNew();

        log.info("TogetherTradeSaleNewHandler =========》 同步结束");
        //今昨日销售金额
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey, JSONObject.toJSONString(replaySaleStatisticNewDataView.getSalesAmountDataViewList()));
        //今昨日销售箱数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey, JSONObject.toJSONString(replaySaleStatisticNewDataView.getSalesCaseDataViewList()));
        //今昨日订单数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey, JSONObject.toJSONString(replaySaleStatisticNewDataView.getSalesOrderDataViewList()));
        //今昨日下单用户数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, userKey, JSONObject.toJSONString(replaySaleStatisticNewDataView.getSalesUserDataViewList()));
        //本月及近七天销售箱数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey, JSONObject.toJSONString(saleSynthesisStatisticNewDataView.getSaleSynthesisDataViewList()));
        log.info("TogetherTradeSaleNewHandler =========》 同步缓存成功");
        return SUCCESS;
    }

}

