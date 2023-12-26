package com.wanmi.sbc.shopcart.cart.ChainHandle;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.shopcart.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.shopcart.historytownshiporder.response.TrueStock;
import com.wanmi.sbc.shopcart.historytownshiporder.service.HistoryTownShipOrderService;
import com.wanmi.sbc.shopcart.redis.RedisCache;
import com.wanmi.sbc.shopcart.redis.RedisKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 库存校验器 现在只有redis库存校验 后续可加
 */
@Component
@Slf4j
public class StockCheckNode implements StockAndPureChainNode {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;
    @Autowired
    private HistoryTownShipOrderService historyTownShipOrderService;
    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;
    /**
     * 库存校验器 现在只有redis库存校验 后续可加
     * @param request
     * @return
     */
    @Override
    public StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request) {
        StockAndPureChainNodeRsponse res =new StockAndPureChainNodeRsponse();
        List<DevanningGoodsInfoPureVO> relist =new LinkedList<>();
            if (CollectionUtils.isNotEmpty(request.getCheckPure())){
                Map<String, DevanningGoodsInfoMarketingVO> collect3 = request.getCheckPure().stream().collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, Function.identity(),(a, b)->a));
                request.getCheckPure().forEach(v->{
                    if(2 == v.getSaleType()){ // 2代表散批
                        v.setBigBuyCount(BigDecimal.valueOf(v.getBuyCount()));
                    } else {
                        if (Objects.isNull(v.getDivisorFlag())){
                            DevanningGoodsInfoByIdResponse devanningGoodsInfoById = devanningGoodsInfoQueryProvider.getInfoById(
                                    DevanningGoodsInfoByIdRequest
                                            .builder()
                                            .devanningId(v.getDevanningId())
                                            .build()).getContext();
                            v.setDivisorFlag(devanningGoodsInfoById.getDevanningGoodsInfoVO().getDivisorFlag());
                        }
                        v.setBigBuyCount(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getBuyCount())));
                    }
                });
                //得到goodsinfo:num的数据 因为有拆箱的存在
                Map<String, BigDecimal> collect = request.getCheckPure().stream()
                        .collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, DevanningGoodsInfoMarketingVO::getBigBuyCount, (a, b) -> a.add(b)));
                List<DevanningGoodsInfoMarketingVO> list = new ArrayList();
                KsBeanUtil.copyList(request.getCheckPure(),list);

                List<String> goodsInfoIds = list.stream().map(DevanningGoodsInfoMarketingVO::getGoodsInfoId).collect(Collectors.toList());
                GoodsWareStockByGoodsForIdsRequest goodsWareStockByGoodsForIdsBuilder = GoodsWareStockByGoodsForIdsRequest.builder()
                        .goodsForIdList(goodsInfoIds)
                        .wareId(request.getWareId())
                        .build();
                GoodsWareStockListResponse context = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIds(goodsWareStockByGoodsForIdsBuilder).getContext();
                Map<String, List<GoodsWareStockVO>> collect1 = context.getGoodsWareStockVOList().stream().collect(Collectors.groupingBy(GoodsWareStockVO::getGoodsInfoId));

                list.forEach(q->{
                    //判断库存
                    BigDecimal stock = BigDecimal.ZERO;
                    BigDecimal parseDouble = BigDecimal.ZERO;
                    BigDecimal re = BigDecimal.ZERO;
                    // if (!Boolean.TRUE.equals(request.getIsmysql())){
                       if (false){
                        log.info("库存校验进入reids");
                        String key = RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(q.getGoodsInfoId());
                        if (!redisCache.HashHasKey(key,RedisKeyConstants.GOODS_INFO_WMS_STOCK)){
                            List<String> skuids =  new LinkedList<>();
                            skuids.add(q.getGoodsInfoId());
                            List<TrueStock> getskusstock = new LinkedList<>();
                            if (Objects.nonNull(q.getSaleType()) && Integer.compare(0, q.getSaleType()) == 0){
                                getskusstock = historyTownShipOrderService.getskusstock(skuids);
                            }else {
                                getskusstock = historyTownShipOrderService.getskusstocklingshou(skuids);
                            }
                            re=getskusstock.stream().findAny().get().getStock();
                            log.info("结果========"+re);
                        } else {

                            stock = BigDecimal.valueOf(Double.parseDouble(redisCache.HashGet(key, RedisKeyConstants.GOODS_INFO_WMS_STOCK).toString()));

                            re =stock ;
                            log.info("结果2========"+re);
                        }
                    }
                                          else {
                        log.info("库存校验进入mysql");
                        List<String> skuids =  new LinkedList<>();
                        skuids.add(q.getGoodsInfoId());
//                        List<TrueStock> getskusstock = new LinkedList<>();
//                        if (Objects.nonNull(q.getSaleType()) && Integer.compare(0, q.getSaleType()) == 0){
//                            getskusstock = historyTownShipOrderService.getskusstock(skuids);
//                        }else {
//                            getskusstock = historyTownShipOrderService.getskusstocklingshou(skuids);
//                        }
//                        if (Objects.nonNull(q.getSaleType()) && q.getSaleType().compareTo(1)==0){
//                            getskusstock = historyTownShipOrderService.getskusstocklingshou(skuids);
//                            if (!getskusstock.stream().findAny().isPresent()){
//                                getskusstock = historyTownShipOrderService.getskusstock(skuids);
//                            }
//                        }
//                        else if (Objects.nonNull(q.getSaleType()) && q.getSaleType().compareTo(2)==0){
//                                getskusstock = historyTownShipOrderService.getskusstockbybulk(skuids);
//                        }
//                        else {
//                            getskusstock = historyTownShipOrderService.getskusstock(skuids);
//                            if (!getskusstock.stream().findAny().isPresent()){
//                                getskusstock = historyTownShipOrderService.getskusstocklingshou(skuids);
//                            }
//                        }
//                        Optional<TrueStock> any = getskusstock.stream().findAny();
//                        if(any.isPresent()){
//                            re = any.get().getStock();
//                        }
                       List<GoodsWareStockVO> goodsWareStockVOS = collect1.get(q.getGoodsInfoId());
                       if(CollectionUtils.isNotEmpty(goodsWareStockVOS)){
                           Optional<GoodsWareStockVO> first = goodsWareStockVOS.stream().filter(x -> Objects.equals(x.getWareId(),request.getWareId())).findFirst();
                           if(first.isPresent()){
                               re  = first.get().getStock();
                           } else {
                               // 散批
                               GoodsWareStockVO goodsWareStockVO = goodsWareStockVOS.stream().findFirst().get();
                               re = goodsWareStockVO.getStock();
                           }
                       }
                        log.info("结果3========"+re);
                    }




//                    BigDecimal multiply = BigDecimal.valueOf(q.getBuyCount()).multiply(q.getDivisorFlag());
                    BigDecimal reduce = collect.get(q.getGoodsInfoId());
                    log.info("结果3(购买数量)========{}",reduce);
                    // re是库存数量
                    // reduce是购买数量
                    // <0 re>reduce
                    if (re.compareTo(reduce)<0){
                        log.info("结果3(限购)");
                        DevanningGoodsInfoPureVO convert = KsBeanUtil.convert(q, DevanningGoodsInfoPureVO.class);
                        q.setDivisorFlag(collect3.get(q.getGoodsInfoId()).getDivisorFlag());
                        q.setBuyCount(collect3.get(q.getGoodsInfoId()).getBuyCount());
                        q.setDevanningId(collect3.get(q.getGoodsInfoId()).getDevanningId());
                        q.setGoodsInfoImg(collect3.get(q.getGoodsInfoId()).getGoodsInfoImg());
                        q.setMarketPrice(collect3.get(q.getGoodsInfoId()).getMarketPrice());
                        convert.setType(0);
                        convert.setMaxPurchase(re);
                        convert.setAlreadyNum(BigDecimal.ZERO);
                        relist.add(convert);
                    }
                });
            }
            res.setCheckPure(relist);
            //TODO 测试打印日志上线删除
            log.info("================测试数据"+res.getCheckPure());
            return res;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
