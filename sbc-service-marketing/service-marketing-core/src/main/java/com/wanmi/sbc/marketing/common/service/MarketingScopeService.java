package com.wanmi.sbc.marketing.common.service;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPurchaseNumDTO;
import com.wanmi.sbc.goods.api.request.info.UpdateGoodsInfoPurchaseNumRequest;
import com.wanmi.sbc.marketing.api.request.market.TerminationMarketingScopeRequest;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.redis.RedisCache;
import com.wanmi.sbc.marketing.redis.RedisKeyConstants;
import com.wanmi.sbc.marketing.util.marketing.PurchaseNumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MarketingScopeService {

    @Autowired
    private MarketingScopeRepository marketingScopeRepository;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private RedisCache redisCache;

    /**
     * 根据营销编号查询营销等级集合
     *
     * @param marketingId
     * @return
     */
    public List<MarketingScope> findByMarketingId(Long marketingId){
        return marketingScopeRepository.findByMarketingId(marketingId);
    }

    @Transactional
    @LcnTransaction
    public int terminationMarketingIdAndScopeId(TerminationMarketingScopeRequest request) {
        MarketingScope marketingScope = marketingScopeRepository.findById(request.getMarketingScopeId()).get();
        if(Objects.nonNull(marketingScope)){
            List<PurchaseNumDTO> purchaseNum = new ArrayList<>();
            PurchaseNumDTO purchaseNumDTO = new PurchaseNumDTO();
            purchaseNumDTO.setGoodsInfoId(marketingScope.getScopeId());
            purchaseNum.add(purchaseNumDTO);
            goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNum, GoodsInfoPurchaseNumDTO.class)).b(false).build());

        }
        return marketingScopeRepository.terminationMarketingIdAndScopeId(request.getMarketingScopeId(),
                request.getTerminationFlag());
    }

    /**
     * 保存营销关联商品信息
     * @param scope
     */
    @Transactional
    public void saveMarketingScope(MarketingScope scope){
        marketingScopeRepository.save(scope);
    }

    /**
     * 根据营销编号、商品skuId查询营销等级集合
     * @param marketingId
     * @param skuIds
     * @return
     */
    public List<MarketingScope> findByMarketingIdAndScopeId(Long marketingId,String skuIds){
        return marketingScopeRepository.findByMarketingIdAndSkuIds(marketingId,skuIds);
    }

    /**
     * 根据营销编号、商品skuId查询营销等级集合 缓存级
     * @param marketingId
     * @param skuIds
     * @return
     */
    public List<MarketingScope> findByMarketingIdAndScopeIdAndCache(Long marketingId,String skuIds){
        List<MarketingScope> re = new LinkedList<>();
        //去redis查询
        if (redisCache.HashHasKey(RedisKeyConstants.MARKETING_SCOPE_HASH.concat(marketingId.toString()),skuIds)){
            String o = redisCache.HashGet(RedisKeyConstants.MARKETING_SCOPE_HASH.concat(marketingId.toString()), skuIds).toString();
            MarketingScope marketingScope = JSON.parseObject(o, MarketingScope.class);
            re.add(marketingScope);
        }else {
            re = marketingScopeRepository.findByMarketingIdAndSkuIds(marketingId, skuIds);
            if (!CollectionUtils.isEmpty(re)){
                List<MarketingScope> collect = re.stream().filter(v -> {
                    if (v.getTerminationFlag().equals(BoolFlag.YES)) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)){
                    redisCache.Hashput(RedisKeyConstants.MARKETING_SCOPE_HASH.concat(marketingId.toString()),skuIds,
                            JSON.toJSONString(collect.stream().findAny().get()));
                }
            }
        }
        return re;
    }

    /**
     * 获取营销对应的商品是否有限购
     * @param tradeMarketingList
     * @return
     */
    public List<TradeMarketingDTO>  getMarketingScopeLimitPurchase(List<TradeMarketingDTO> tradeMarketingList){
        List<TradeMarketingDTO> re = new LinkedList<>();
        if (CollectionUtils.isEmpty(tradeMarketingList)){
            return re;
        }
        tradeMarketingList.forEach(v->{
            Long marketingId = v.getMarketingId();
            Long marketingLevelId = v.getMarketingLevelId();
            v.getSkuIds().forEach(qb->{
                List<MarketingScope> byMarketingIdAndScopeIdAndCache = this.findByMarketingIdAndScopeIdAndCache(marketingId, qb);
                MarketingScope marketingScope = byMarketingIdAndScopeIdAndCache.stream().findAny().orElse(null);
                if (Objects.nonNull(marketingScope)){
                    if (Objects.nonNull(marketingScope.getPurchaseNum()) ||  Objects.nonNull(marketingScope.getPerUserPurchaseNum())){
                        List<Long> collect = re.stream().map(TradeMarketingDTO::getMarketingId).collect(Collectors.toList());
                         if (CollectionUtils.isEmpty(re) || !collect.contains(marketingId)){
                            TradeMarketingDTO tradeMarketingDTO = new TradeMarketingDTO();
                            tradeMarketingDTO.setMarketingId(marketingId);
                            tradeMarketingDTO.setMarketingLevelId(marketingLevelId);
                            tradeMarketingDTO.setSkuIds(Arrays.asList(qb));
                            re.add(tradeMarketingDTO);
                        }else {
                             re.forEach(qd->{
                                 if (Objects.equals(qd.getMarketingId(), marketingId)){
                                     List<String> skuIds = new ArrayList<>(qd.getSkuIds());
                                     skuIds.add(qb);
                                     qd.setSkuIds(skuIds);
                                 }
                             });
                        }
                    }
                }
            });
        });
     return re;
    }



    /**
     * 根据营销编号、商品skuId查询营销等级集合
     * @param marketingId
     * @param skuIds
     * @return
     */
    public List<MarketingScope> findByMarketingIdAndScopeIdNotTermination(Long marketingId,String skuIds){
        return marketingScopeRepository.findByMarketingIdAndScopeId(marketingId,skuIds);
    }
}
