package com.wanmi.sbc.goods.goodstypeconfig.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsResponse;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
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
@Service("merchantTypeConfigCacheService")
public class MerchantTypeConfigCacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MerchantTypeConfigService goodsRecommendGoodsService;

    //保存
    public void saveRecommendGoods(MerchantTypeConfigResponse setting){
        redisService.setString(RedisKeyConstant.CONFIG_TYPE_RECOMMEND_TYPE+String.valueOf(setting.getCompanyInfoId()).concat(setting.getCompanyInfoId().toString()), JSONObject.toJSONString(setting));
    }

//    /**
//     * 查询设置缓存
//     */
//    public GoodsRecommendGoodsResponse queryRecommendGoodsCache(Long companyInfoId) {
//        boolean hasKey = redisService.hasKey(RedisKeyConstant.CONFIG_TYPE_RECOMMEND_TYPE+String.valueOf(companyInfoId).concat(companyInfoId.toString()));
//        if (hasKey) {
//            return JSONObject.parseObject(redisService.getString(RedisKeyConstant.CONFIG_TYPE_RECOMMEND_TYPE+String.valueOf(companyInfoId).concat(companyInfoId.toString())), GoodsRecommendGoodsResponse.class);
//        } else {
//            GoodsRecommendGoodsResponse setting = new GoodsRecommendGoodsResponse();
//            List<String> goodsInfoIds = new ArrayList<>();
//            List<MerchantRecommendType> goodsRecommendGoodsList = goodsRecommendGoodsService.findByCompanyInfoId(companyInfoId);
//            if(CollectionUtils.isNotEmpty(goodsRecommendGoodsList)){
//                goodsInfoIds =  goodsRecommendGoodsList.stream().map(MerchantRecommendTypeVO::getCompanyInfoId).collect(Collectors.toList());
//            }
//
//            setting.setGoodsInfoIds(goodsInfoIds);
//            redisService.setString(RedisKeyConstant.CONFIG_TYPE_RECOMMEND_TYPE+String.valueOf(companyInfoId).concat(companyInfoId.toString()), JSONObject.toJSONString(setting));
//            return setting;
//        }
//    }

    public void deleteRecommendGoodsCache(Long companyInfoId){
        redisService.delete(RedisKeyConstant.CONFIG_TYPE_RECOMMEND_TYPE+String.valueOf(companyInfoId).concat(companyInfoId.toString()));
        redisService.delete(RedisKeyConstant.CONFIG_RECOMMEND_PAGE_SETTING+String.valueOf(companyInfoId).concat(companyInfoId.toString()));
    }
}
