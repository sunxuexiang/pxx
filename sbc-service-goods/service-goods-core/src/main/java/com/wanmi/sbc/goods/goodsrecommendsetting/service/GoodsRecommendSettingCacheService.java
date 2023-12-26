package com.wanmi.sbc.goods.goodsrecommendsetting.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import com.wanmi.sbc.goods.goodsrecommendsetting.repository.GoodsRecommendSettingRepository;
import com.wanmi.sbc.goods.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>商品推荐配置业务逻辑</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@Service("GoodsRecommendSettingCacheService")
public class GoodsRecommendSettingCacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsRecommendSettingService goodsRecommendSettingService;

    //保存
    public void saveSetting(GoodsRecommendSettingResponse setting){
        redisService.setString(RedisKeyConstant.GOODS_RECOMMEND_SETTING, JSONObject.toJSONString(setting));
    }

    /**
     * 查询设置缓存
     */
    public GoodsRecommendSettingResponse querySettingCache() {
        boolean hasKey = redisService.hasKey(RedisKeyConstant.GOODS_RECOMMEND_SETTING);
        if (hasKey) {
            return JSONObject.parseObject(redisService.getString(RedisKeyConstant.GOODS_RECOMMEND_SETTING), GoodsRecommendSettingResponse.class);
        } else {
            List<GoodsRecommendSetting> goodsRecommendSetting = goodsRecommendSettingService.findAll();
            List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = KsBeanUtil.convertList(goodsRecommendSetting,
                    GoodsRecommendSettingVO.class);
            GoodsRecommendSettingResponse setting =
                    GoodsRecommendSettingResponse.builder().goodsRecommendSettingVOS(goodsRecommendSettingVOS).build();

            redisService.setString(RedisKeyConstant.GOODS_RECOMMEND_SETTING, JSONObject.toJSONString(setting));
            return setting;
        }
    }

    /**
     * 删除移动端缓存page信息
     */
    public void delPageSettingCache() {
        redisService.delete(RedisKeyConstant.GOODS_RECOMMEND_GOODS);
        redisService.delete(RedisKeyConstant.RECOMMEND_PAGE_SETTING);
    }

}
