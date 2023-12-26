package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.request.replay.ReplayTradeBuyerItemNumQuery;
import com.wanmi.ares.view.replay.ReplayTradeBuyerGoodsNumResponse;
import com.wanmi.sbc.config.BossNacosConfig;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class BuyerTradeItemNumService {

    private final static String key = "BUYER_GOODS_NUM_TRADE";
    @Autowired
    RedisService redisService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;
    @Autowired
    private BossNacosConfig bossNacosConfig;

    private static Date getStarToday(Integer day) {
        Date startDate = DateUtils.addDays(new Date(), -day);
        startDate = DateUtils.setHours(startDate, 0);
        startDate = DateUtils.setMinutes(startDate, 0);
        startDate = DateUtils.setSeconds(startDate, 0);
        return startDate;
    }

    private static Date getEndToday(Integer day) {
        Date startDate = DateUtils.addDays(new Date(), -day);
        startDate = DateUtils.setHours(startDate, 23);
        startDate = DateUtils.setMinutes(startDate, 59);
        startDate = DateUtils.setSeconds(startDate, 59);
        return startDate;
    }

    public int execute(ReplayTradeBuyerItemNumQuery query) {
        int total = 0;
        final Integer buyerGoodsNumTrade = bossNacosConfig.getBuyerGoodsNumTrade();
        query.setStartTime(getStarToday(buyerGoodsNumTrade));
        final List<ReplayTradeBuyerGoodsNumResponse> res = replayTradeQueryProvider.staticsCustomerItemNumByStartTime(query);
        if (CollectionUtils.isNotEmpty(res)) {
            final Map<String, Map<String, Integer>> mapRes = res.stream().collect(Collectors.groupingBy(ReplayTradeBuyerGoodsNumResponse::getCustomerId, Collectors.groupingBy(ReplayTradeBuyerGoodsNumResponse::getGoodsId, Collectors.summingInt(ReplayTradeBuyerGoodsNumResponse::getNum))));
            mapRes.forEach((k, v) -> {
                List<ReplayTradeBuyerGoodsNumResponse> list = new ArrayList<>();
                v.forEach((k1, v1) -> {
                    final ReplayTradeBuyerGoodsNumResponse replayTradeBuyerGoodsNumResponse = new ReplayTradeBuyerGoodsNumResponse();
                    list.add(replayTradeBuyerGoodsNumResponse);
                    replayTradeBuyerGoodsNumResponse.setGoodsId(k1);
                    replayTradeBuyerGoodsNumResponse.setNum(v1);
                    redisService.hset(key, k, JSON.toJSONString(list));
                });
            });
            total = mapRes.keySet().size();
        }
        log.info("处理客户下单商品记录，商家数量为：{}", total);
        return total;
    }
}
