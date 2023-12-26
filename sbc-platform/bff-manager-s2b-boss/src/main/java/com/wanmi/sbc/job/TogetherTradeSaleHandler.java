package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.request.ReplayTradeWareIdRequest;
import com.wanmi.ares.request.replay.ReplayTradeRequest;
import com.wanmi.ares.view.replay.*;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@JobHandler(value="togetherTradeSaleHandler")
@Component
@Slf4j
@RequestMapping("togetherTradeSaleHandler")
public class TogetherTradeSaleHandler extends IJobHandler {

    @Autowired
    RedisService redisService;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    public static final Map<Long,Long> wareHouseMap = new HashMap(){
        {
            this.put(49l,1l);
            this.put(50l,46l);
            this.put(51l,47l);
        }
    };

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Override
    @GetMapping("execute")
    public ReturnT<String> execute(String s) throws Exception {
        String amountKey = "AMOUNT";
        String caseKey = "CASE";
        String orderKey = "ORDER";
        String userKey = "USER";
        String monthKey = "CURRENTMONTH";
        log.info("TogetherTradeSaleHandler =========》 同步开始");

        List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .build()).getContext().getWareHouseVOList();
        Map<Long, List<WareHouseVO>> wareMap = wareHouseVOList.stream().collect(Collectors.groupingBy(WareHouseVO::getWareId));
        /*今昨日数据统计*/
        List<Long> wareIdList = wareHouseVOList.stream().map(WareHouseVO::getWareId).distinct().collect(Collectors.toList());
        ReplaySaleStatisticDataView replaySaleStatisticDataView = replayTradeQueryProvider.queryTwoDaySaleStatistic(ReplayTradeWareIdRequest.builder().wareIds(wareIdList).build());
        /*当月数据统计*/
        SaleSynthesisStatisticDataView saleSynthesisStatisticDataView = replayTradeQueryProvider.queryCurrentMonthSaleStatistic();

        // 本月数据同居
        List<SaleSynthesisDataView> saleSynthesisDataViewList = Lists.newArrayList();

        // 设置仓库名称并过滤掉删除掉的仓库数据
        for (WareHouseVO wareHouseVO : wareHouseVOList) {
            if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesAmountDataViewList())){
                List<SalesAmountDataView> salesAmountDataViews = replaySaleStatisticDataView.getSalesAmountDataViewList().stream().filter(item -> item.getWareId() == wareHouseVO.getWareId())
                        .collect(Collectors.toList());
                salesAmountDataViews.forEach(d->{
                    d.setWareHouse(wareHouseVO.getWareName());
                    d.setWareCode(wareHouseVO.getWareCode());
                });
            }

            if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesCaseDataViewList())){
                List<SalesCaseDataView> salesCaseDataViews = replaySaleStatisticDataView.getSalesCaseDataViewList().stream().filter(item -> item.getWareId() == wareHouseVO.getWareId())
                        .collect(Collectors.toList());
                salesCaseDataViews.forEach(d->{
                    d.setWareHouse(wareHouseVO.getWareName());
                    d.setWareCode(wareHouseVO.getWareCode());
                });
            }


            if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesOrderDataViewList())){
                List<SalesOrderDataView> salesOrderDataViews = replaySaleStatisticDataView.getSalesOrderDataViewList().stream().filter(item -> item.getWareId() == wareHouseVO.getWareId())
                        .collect(Collectors.toList());
                salesOrderDataViews.forEach(d->{
                    d.setWareHouse(wareHouseVO.getWareName());
                    d.setWareCode(wareHouseVO.getWareCode());
                });
            }

            if(CollectionUtils.isNotEmpty(replaySaleStatisticDataView.getSalesUserDataViewList())){
                List<SalesUserDataView> salesUserDataViews = replaySaleStatisticDataView.getSalesUserDataViewList().stream().filter(item -> item.getWareId() == wareHouseVO.getWareId())
                        .collect(Collectors.toList());
                salesUserDataViews.forEach(d->{
                    d.setWareHouse(wareHouseVO.getWareName());
                    d.setWareCode(wareHouseVO.getWareCode());
                });
            }


            if(CollectionUtils.isNotEmpty(saleSynthesisStatisticDataView.getSaleSynthesisDataViewList())){
                List<SaleSynthesisDataView> saleSynthesisDataViews = saleSynthesisStatisticDataView.getSaleSynthesisDataViewList().stream().filter(item -> item.getWareId() == wareHouseVO.getWareId())
                        .collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(saleSynthesisDataViews)){
                    saleSynthesisDataViews.forEach(d->{
                        d.setWareHouse(wareHouseVO.getWareName());
                        d.setWareCode(wareHouseVO.getWareCode());
                    });
                }
                saleSynthesisDataViewList.addAll(saleSynthesisDataViews);
            }
        }

        // 合并每月统计数据
        List<SaleSynthesisDataView> oldView = saleSynthesisDataViewList.stream().filter(saleSynthesisDataView -> !wareHouseMap.containsKey(saleSynthesisDataView.getWareId())).collect(Collectors.toList());
        List<SaleSynthesisDataView> newView = saleSynthesisDataViewList.stream().filter(saleSynthesisDataView -> wareHouseMap.containsKey(saleSynthesisDataView.getWareId())).collect(Collectors.toList());
        List<SaleSynthesisDataView> saleSynthesisDataViewListFinal = oldView.stream().map(oldData -> {
            newView.stream().filter(n -> Objects.equals(oldData.getWareId(),wareHouseMap.get(n.getWareId()))).forEach(newData -> {
                mergeResultToOld(oldData,newData);
            });
            return oldData;
        }).collect(Collectors.toList());
        // 重新排序，将零售仓放置租后
        SaleSynthesisDataView last = null;
        int index = 0;
        for (int i = 0; i < saleSynthesisDataViewListFinal.size(); i++) {
            index = i;
            SaleSynthesisDataView synthesisDataView = saleSynthesisDataViewListFinal.get(i);
            if(synthesisDataView.getWareId() == 45){
                last = synthesisDataView;
                break;
            }
        }
        if(Objects.nonNull(last)){
            saleSynthesisDataViewListFinal.remove(index);
            saleSynthesisDataViewListFinal.add(last);
        }

        log.info("TogetherTradeSaleHandler =========》 同步结束");
        //今昨日销售金额
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, amountKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesAmountDataViewList()));
        //今昨日销售箱数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, caseKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesCaseDataViewList()));
        //今昨日订单数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, orderKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesOrderDataViewList()));
        //今昨日下单用户数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, userKey, JSONObject.toJSONString(replaySaleStatisticDataView.getSalesUserDataViewList()));
        //本月及近七天销售箱数
        redisService.hset(CacheKeyConstant.TRADE_GET_TOGETHER, monthKey, JSONObject.toJSONString(saleSynthesisDataViewListFinal));
        log.info("TogetherTradeSaleHandler =========》 同步缓存成功");
        return SUCCESS;
    }


    private void mergeResultToOld(SaleSynthesisDataView oldData,SaleSynthesisDataView newData){
        oldData.setSalePrice(Optional.ofNullable(newData.getSalePrice()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(oldData.getSalePrice()).orElse(BigDecimal.ZERO)));
        oldData.setBuyNum(Optional.ofNullable(newData.getBuyNum()).orElse(0)+Optional.ofNullable(oldData.getBuyNum()).orElse(0));
        oldData.setOrderNum(Optional.ofNullable(newData.getOrderNum()).orElse(0) + Optional.ofNullable(oldData.getOrderNum()).orElse(0));
        oldData.setOrderItemNum(Optional.ofNullable(newData.getOrderItemNum()).orElse(0l)+Optional.ofNullable(oldData.getOrderItemNum()).orElse(0l));

        // 合并七天内销售金额
        if(CollectionUtils.isNotEmpty(oldData.getRecentSevenDaySaleTotalPriceList())){
            List<RecentSevenDaySalePriceView> orderSalePriceData = oldData.getRecentSevenDaySaleTotalPriceList().stream().map(o -> {
                Optional.ofNullable(newData.getRecentSevenDaySaleTotalPriceList()).ifPresent(data -> {
                    data.stream().filter(n -> Objects.equals(o.getDayTime(), n.getDayTime())).forEach(n -> {
                        o.setDaySalePrice(Optional.ofNullable(o.getDaySalePrice()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(n.getDaySalePrice()).orElse(BigDecimal.ZERO)));
                    });
                });
                return o;
            }).collect(Collectors.toList());
            oldData.setRecentSevenDaySaleTotalPriceList(orderSalePriceData);
        }

        // 合并七天内销售
        if(CollectionUtils.isNotEmpty(oldData.getRecentSevenDaySaleTotalCaseList())){
            List<RecentSevenDaySaleView> orderSaleItemData = oldData.getRecentSevenDaySaleTotalCaseList().stream().map(o -> {
                Optional.ofNullable(newData.getRecentSevenDaySaleTotalCaseList()).ifPresent(data -> {
                    data.stream().filter(n -> Objects.equals(o.getDayTime(), n.getDayTime())).forEach(n -> {
                        o.setTotalNum(Optional.ofNullable(o.getTotalNum()).orElse(0l) + Optional.ofNullable(n.getTotalNum()).orElse(0l));
                    });
                });
                return o;
            }).collect(Collectors.toList());
            oldData.setRecentSevenDaySaleTotalCaseList(orderSaleItemData);
        }

    }
}

