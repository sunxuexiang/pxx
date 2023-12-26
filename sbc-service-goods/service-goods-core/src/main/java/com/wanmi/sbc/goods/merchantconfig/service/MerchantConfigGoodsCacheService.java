package com.wanmi.sbc.goods.merchantconfig.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsResponse;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品推荐配置业务逻辑</p>
 *
 * @author sgy
 * @date 2023-06-07 10:24:37
 */
@Service("merchantConfigGoodsCacheService")
public class MerchantConfigGoodsCacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MerchantConfigGoodsService goodsRecommendGoodsService;

    //保存
    public void saveRecommendGoods(MerchantConfigGoodsResponse setting){
        redisService.setString(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS+String.valueOf(setting.getCompanyInfoId()).concat(setting.getCompanyInfoId().toString()), JSONObject.toJSONString(setting));
    }

    /**
     * 查询设置缓存
     */
    public GoodsRecommendGoodsResponse queryRecommendGoodsCache(Long storeId) {
        boolean hasKey = redisService.hasKey(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS+String.valueOf(storeId).concat(storeId.toString()));
        if (hasKey) {
            return JSONObject.parseObject(redisService.getString(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS+String.valueOf(storeId).concat(storeId.toString())), GoodsRecommendGoodsResponse.class);
        } else {
            GoodsRecommendGoodsResponse setting = new GoodsRecommendGoodsResponse();
            List<String> goodsInfoIds = new ArrayList<>();
            List<MerchantRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.findByCompanyInfoId(storeId);
            if(CollectionUtils.isNotEmpty(goodsRecommendGoodsList)){
                goodsInfoIds =  goodsRecommendGoodsList.stream().map(MerchantRecommendGoods::getGoodsInfoId).collect(Collectors.toList());
            }

            setting.setGoodsInfoIds(goodsInfoIds);
            redisService.setString(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS+String.valueOf(storeId).concat(storeId.toString()), JSONObject.toJSONString(setting));
            return setting;
        }
    }

    public void deleteRecommendGoodsCache(Long storeId){
        redisService.delete(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS+String.valueOf(storeId).concat(storeId.toString()));
        redisService.delete(RedisKeyConstant.MERCHANT_RECOMMEND_PAGE_SETTING+String.valueOf(storeId).concat(storeId.toString()));
    }
}
