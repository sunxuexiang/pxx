package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.RetailGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.redis.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批推荐商品信息API
 * @author: XinJiang
 * @time: 2022/4/21 10:01
 */
@RestController
@RequestMapping("/retail/goods/recommend")
@Api(tags = "RetailGoodsRecommendSettingBaseController", description = "S2B web公用-散批推荐商品信息API")
@Slf4j
public class RetailGoodsRecommendSettingBaseController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RetailGoodsRecommendSettingProvider retailGoodsRecommendSettingProvider;

    @ApiOperation(value = "获取散批推荐商品信息（缓存级）")
    @PostMapping("/getByCache")
    public BaseResponse<GoodsInfoViewByIdsResponse> getRetailGoodsRecommendByCache() {
        if (redisService.hasKey(CacheKeyConstant.RETAIL_GOODS_RECOMMEND)) {
            return BaseResponse.success(JSONObject.parseObject(redisService.getString(CacheKeyConstant.RETAIL_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class));
        }
        retailGoodsRecommendSettingProvider.fillRedis();
        return BaseResponse.success(JSONObject.parseObject(redisService.getString(CacheKeyConstant.RETAIL_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class));
    }
}
