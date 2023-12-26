package com.wanmi.sbc.goods.goodsrecommendgoods.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import com.wanmi.sbc.goods.goodsrecommendsetting.service.GoodsRecommendSettingService;
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
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@Service("GoodsRecommendGoodsCacheService")
public class GoodsRecommendGoodsCacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsRecommendGoodsService goodsRecommendGoodsService;

    //保存
    public void saveRecommendGoods(GoodsRecommendGoodsResponse setting){
        redisService.setString(RedisKeyConstant.GOODS_RECOMMEND_GOODS.concat(setting.getWareId().toString()), JSONObject.toJSONString(setting));
    }

    /**
     * 查询设置缓存
     */
    public GoodsRecommendGoodsResponse queryRecommendGoodsCache(Long wareId) {
        boolean hasKey = redisService.hasKey(RedisKeyConstant.GOODS_RECOMMEND_GOODS.concat(wareId.toString()));
        if (hasKey) {
            return JSONObject.parseObject(redisService.getString(RedisKeyConstant.GOODS_RECOMMEND_GOODS.concat(wareId.toString())), GoodsRecommendGoodsResponse.class);
        } else {
            GoodsRecommendGoodsResponse setting = new GoodsRecommendGoodsResponse();
            List<String> goodsInfoIds = new ArrayList<>();

//            List<GoodsRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.list();
            List<GoodsRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.findByWareId(wareId);
            if(CollectionUtils.isNotEmpty(goodsRecommendGoodsList)){
                goodsInfoIds =  goodsRecommendGoodsList.stream().map(GoodsRecommendGoods::getGoodsInfoId).collect(Collectors.toList());
            }

            setting.setGoodsInfoIds(goodsInfoIds);
            redisService.setString(RedisKeyConstant.GOODS_RECOMMEND_GOODS.concat(wareId.toString()), JSONObject.toJSONString(setting));
            return setting;
        }
    }

    public void deleteRecommendGoodsCache(Long wareId){
        redisService.delete(RedisKeyConstant.GOODS_RECOMMEND_GOODS.concat(wareId.toString()));
        redisService.delete(RedisKeyConstant.RECOMMEND_PAGE_SETTING.concat(wareId.toString()));
    }
}
