package com.wanmi.sbc.goods.retailgoodsrecommend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsInfoResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsInfoResponse;
import com.wanmi.sbc.goods.info.request.GoodsInfoRequest;
import com.wanmi.sbc.goods.info.service.BulkGoodsInfoService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.retailgoodsrecommend.model.root.BulkGoodsRecommendSetting;
import com.wanmi.sbc.goods.retailgoodsrecommend.model.root.RetailGoodsRecommendSetting;
import com.wanmi.sbc.goods.retailgoodsrecommend.repository.BulkGoodsRecommendSettingRepository;
import com.wanmi.sbc.goods.retailgoodsrecommend.repository.RetailGoodsRecommendSettingRepository;
import com.wanmi.sbc.goods.retailgoodsrecommend.request.BulkGoodsCommendSortRequest;
import com.wanmi.sbc.goods.retailgoodsrecommend.request.RetailGoodsCommendSortRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 散批鲸喜推荐service
 * @author: XinJiang
 * @time: 2022/4/20 9:26
 */
@Service
public class BulkGoodsRecommendSettingService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private BulkGoodsInfoService bulkGoodsInfoService;

    @Autowired
    private BulkGoodsRecommendSettingRepository bulkGoodsRecommendSettingRepository;

    /**
     * 批量添加散批推荐商品
     * @param skuIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<String> skuIds,Long wareId) {

        List<BulkGoodsRecommendSetting> byGoodsInfoIdIn = bulkGoodsRecommendSettingRepository.findByGoodsInfoIdsAndWareId(skuIds,wareId);

        List<String> newSkuIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(byGoodsInfoIdIn)){
            List<String> goodsInfoIds = byGoodsInfoIdIn.stream().map(o -> o.getGoodsInfoId()).collect(Collectors.toList());
            skuIds.forEach(var->{
                if(!goodsInfoIds.contains(var)){
                    newSkuIds.add(var);
                }
            });
        }else{
            newSkuIds.addAll(skuIds);
        }

        Integer maxSortNum = bulkGoodsRecommendSettingRepository.maxSortNum();
        if (Objects.isNull(maxSortNum)) {
            maxSortNum = 0;
        }

        List<BulkGoodsRecommendSetting> retailGoodsRecommendSettings = new ArrayList<>();
        for (String skuId:newSkuIds) {
            BulkGoodsRecommendSetting retailGoodsRecommendSetting = new BulkGoodsRecommendSetting();
            retailGoodsRecommendSetting.setGoodsInfoId(skuId);
            retailGoodsRecommendSetting.setSortNum(maxSortNum+1);
            retailGoodsRecommendSetting.setWareId(wareId);
            retailGoodsRecommendSettings.add(retailGoodsRecommendSetting);
            maxSortNum++;
        }

        bulkGoodsRecommendSettingRepository.saveAll(retailGoodsRecommendSettings);
        this.fillRedis();
    }

    /**
     * 获取推荐列表
     * @return
     */
    public List<BulkGoodsRecommendSetting> getList(){
        List<BulkGoodsRecommendSetting> retailGoodsRecommendSettings = bulkGoodsRecommendSettingRepository.findAll();
        return retailGoodsRecommendSettings;
    }

    /**
     * 通过id移除推荐商品
     * @param recommendId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(String recommendId) {
        bulkGoodsRecommendSettingRepository.deleteById(recommendId);
    }

    /**
     * 通过id批量移除推荐商品
     * @param recommendIds
     */
    public void delByIds(List<String> recommendIds) {
        recommendIds.forEach(recommendId -> this.delById(recommendId));
        this.fillRedis();
    }

    /**
     * 拖拽排序
     * @param retailGoodsList
     */
    @Transactional(rollbackFor = Exception.class)
    public void dragSort(List<BulkGoodsCommendSortRequest> retailGoodsList) {
        retailGoodsList.forEach(request -> bulkGoodsRecommendSettingRepository.updateSort(request.getRecommendId(),request.getSortNum()));
        this.fillRedis();
    }

    /**
     * 生产redis缓存数据
     */
    public void fillRedis(){
        List<BulkGoodsRecommendSetting> retailGoodsList = this.getList();
        retailGoodsList.sort(Comparator.comparing(BulkGoodsRecommendSetting::getSortNum));
        if (CollectionUtils.isNotEmpty(retailGoodsList)) {
            List<String> skuIds = retailGoodsList.stream().map(BulkGoodsRecommendSetting::getGoodsInfoId).collect(Collectors.toList());
            Map<String,String> recommendIdMap = retailGoodsList.stream()
                    .collect(Collectors.toMap(BulkGoodsRecommendSetting::getGoodsInfoId,BulkGoodsRecommendSetting::getRecommendId,(key1,key2) -> key1));
            Map<String,Integer> sortMap = retailGoodsList.stream()
                    .collect(Collectors.toMap(BulkGoodsRecommendSetting::getGoodsInfoId,BulkGoodsRecommendSetting::getSortNum, (k1,k2) -> k1));
            BulkGoodsInfoResponse response = bulkGoodsInfoService.findSkuByIds(GoodsInfoRequest.builder().goodsInfoIds(skuIds).build());
            GoodsInfoViewByIdsResponse cacheResponse = new GoodsInfoViewByIdsResponse();
            cacheResponse.setGoodsInfos(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoVO.class));
            cacheResponse.setGoodses(KsBeanUtil.convertList(response.getGoodses(), GoodsVO.class));
            cacheResponse.getGoodsInfos().forEach(goodsInfoVO -> {
                String recommendId = recommendIdMap.getOrDefault(goodsInfoVO.getGoodsInfoId(), null);
                goodsInfoVO.setRetailRecommendId(recommendId);
                goodsInfoVO.setRecommendSort(sortMap.getOrDefault(goodsInfoVO.getGoodsInfoId(), 0));
            });
            cacheResponse.getGoodsInfos().sort(Comparator.comparing(GoodsInfoVO::getRecommendSort));
            redisService.setString(CacheKeyConstant.BULK_GOODS_RECOMMEND, JSON.toJSONString(cacheResponse,
                    SerializerFeature.DisableCircularReferenceDetect),120);
        } else {
            redisService.delete(CacheKeyConstant.BULK_GOODS_RECOMMEND);
        }
    }

}
