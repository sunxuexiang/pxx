package com.wanmi.sbc.job;

import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeListAllRequest;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.dto.TradeStateDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 热销排行榜数据初始化redis
 * @author: XinJiang
 * @time: 2021/11/4 17:54
 */
@Component
@Slf4j
@JobHandler(value="hitListJobHandler")
public class HitListJobHandler extends IJobHandler {

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private RedisService redisService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("HitListJobHandler热销排行榜缓存redis定时任务执行开始： " + LocalDateTime.now());
        log.info("HitListJobHandler热销排行榜缓存redis定时任务执行开始：{}",LocalDateTime.now());
        //获取所有分类
        GoodsCateListByConditionRequest cateRequest = new GoodsCateListByConditionRequest();
        cateRequest.setDelFlag(DeleteFlag.NO.toValue());
        //二级分类
        cateRequest.setCateGrade(2);
        List<GoodsCateVO> goodsCateVOList = goodsCateQueryProvider.listByCondition(cateRequest).getContext().getGoodsCateVOList();
        List<Long> cateIds = goodsCateVOList.stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
        //获取近一月，所有已支付订单
        TradeListAllRequest tradeRequest = new TradeListAllRequest();
        TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
        tradeQueryDTO.setBeginTime(DateUtil.format(LocalDateTime.now().minus(7, ChronoUnit.DAYS),"yyyy-MM-dd HH:mm:ss"));
        tradeQueryDTO.setTradeState(TradeStateDTO.builder().payState(PayState.PAID).build());
        tradeRequest.setTradeQueryDTO(tradeQueryDTO);
        List<TradeVO> tradeVOS = tradeQueryProvider.listAll(tradeRequest).getContext().getTradeVOList();
        //近一个月订单商品集合
        List<TradeItemVO> tradeItemVOS = new ArrayList<>();
        tradeVOS.forEach(tradeVO -> {
            tradeItemVOS.addAll(tradeVO.getTradeItems());
        });

        //所有订单商品skuId集合
        List<String> skuIds = tradeItemVOS.stream().distinct().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listGoodsStatusByGoodsIds(GoodsInfoListByIdsRequest.builder()
                .goodsInfoIds(skuIds).build()).getContext().getGoodsInfos();
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));

        //所有订单商品销售量map集合
        Map<String,Long> allGoodsMap = tradeItemVOS.stream()
                .collect(Collectors.groupingBy(TradeItemVO::getSkuId,Collectors.summingLong(TradeItemVO::getNum)));
        //redis排行缓存集合
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        allGoodsMap.forEach((k,v) -> {
            //正常的商品才参与排行
            if(Objects.nonNull(goodsInfoMap.get(k)) && goodsInfoMap.get(k).getGoodsStatus().equals(GoodsStatus.OK)){
                DefaultTypedTuple<String> tuple = new DefaultTypedTuple<>(k, v.doubleValue());
                tuples.add(tuple);
            }
        });
        //缓存所有商品的排行榜
        redisService.delete(RedisKeyConstant.HIT_LIST);
        redisService.batchAddZset(RedisKeyConstant.HIT_LIST,tuples);
        XxlJobLogger.log("===================>"+RedisKeyConstant.HIT_LIST+"：缓存成功");

        cateIds.forEach(cateId -> {
            //查询二级分类下所有三级分类
            cateRequest.setCateGrade(3);
            cateRequest.setCateParentId(cateId);
            List<GoodsCateVO> childGoodsCate = goodsCateQueryProvider.listByCondition(cateRequest).getContext().getGoodsCateVOList();
            List<Long> childrenCateIds = childGoodsCate.stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
            XxlJobLogger.log("===================>cateId:"+cateId+",childrenCateIds:"+childrenCateIds);
            //分类商品近一个月订单集合
            List<TradeItemVO> cateTradeItems = tradeItemVOS.stream().filter(tradeItemVO -> childrenCateIds.contains(tradeItemVO.getCateId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cateTradeItems)){
                //分类商品订单销售量map集合
                Map<String,Long> cateGoodsMap = cateTradeItems.stream()
                        .collect(Collectors.groupingBy(TradeItemVO::getSkuId,Collectors.summingLong(TradeItemVO::getNum)));
                //redis排行缓存集合
                Set<ZSetOperations.TypedTuple<String>> cateTuples = new HashSet<>();
                cateGoodsMap.forEach((k,v) -> {
                    //正常的商品才参与排行
                    if(Objects.nonNull(goodsInfoMap.get(k)) && goodsInfoMap.get(k).getGoodsStatus().equals(GoodsStatus.OK)) {
                        DefaultTypedTuple<String> cateTuple = new DefaultTypedTuple<>(k, v.doubleValue());
                        cateTuples.add(cateTuple);
                    }
                });
                if(CollectionUtils.isNotEmpty(cateTuples)){
                    //缓存分类商品的排行榜
                    redisService.delete(RedisKeyConstant.HIT_LIST+cateId);
                    redisService.batchAddZset(RedisKeyConstant.HIT_LIST+cateId,cateTuples);
                    XxlJobLogger.log("===================>"+RedisKeyConstant.HIT_LIST+cateId+"：缓存成功");
                }else{
                    redisService.delete(RedisKeyConstant.HIT_LIST+cateId);
                    //如果分类商品排行为空缓存 总排行榜
                    redisService.batchAddZset(RedisKeyConstant.HIT_LIST+cateId,tuples);
                    XxlJobLogger.log("===================>"+RedisKeyConstant.HIT_LIST+cateId+"：缓存成功==》allTop");
                }
            } else {
                redisService.delete(RedisKeyConstant.HIT_LIST+cateId);
                //如果分类商品排行为空缓存 总排行榜
                redisService.batchAddZset(RedisKeyConstant.HIT_LIST+cateId,tuples);
                XxlJobLogger.log("===================>"+RedisKeyConstant.HIT_LIST+cateId+"：缓存成功==》allTop");
            }
        });

        XxlJobLogger.log("HitListJobHandler热销排行榜缓存redis定时任务执行结束： " + LocalDateTime.now());
        log.info("HitListJobHandler热销排行榜缓存redis定时任务执行结束：{}",LocalDateTime.now());
        return SUCCESS;
    }
}
