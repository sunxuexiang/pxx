package com.wanmi.sbc.advertising;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.BulkGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNoRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingRetailQueryProvider;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailListResponse;
import com.wanmi.sbc.setting.bean.enums.AdvertisingRetailJumpType;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 散批广告位API
 * @author: XinJiang
 * @time: 2022/4/19 14:11
 */
@Api(description = "散批广告位API", tags = "AdvertisingBulkBaseController")
@RestController
@RequestMapping(value = "/bulk/advertising")
public class AdvertisingBulkBaseController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private BulkGoodsRecommendSettingProvider bulkGoodsRecommendSettingProvider;

//    @ApiOperation(value = "获取散批广告信息（缓存级）")
//    @PostMapping("/list-by-cache")
//    public BaseResponse<AdvertisingRetailListResponse> listByCache() {
//        List<AdvertisingRetailVO> advertisingRetailVOList = advertisingRetailQueryProvider.listByCache().getContext().getAdvertisingRetailVOList();
//        if (CollectionUtils.isNotEmpty(advertisingRetailVOList)) {
//            advertisingRetailVOList.forEach(advertisingRetailVO -> {
//                if (AdvertisingType.BANNER.equals(advertisingRetailVO.getAdvertisingType())) {
//                    advertisingRetailVO.getAdvertisingRetailConfigs().forEach(config -> {
//                        if (AdvertisingRetailJumpType.GOODS.equals(config.getJumpType())) {
//                            GoodsInfoVO goodsInfoVO = bulkGoodsInfoQueryProvider.getGoodsInfoByErpNo(GoodsInfoByErpNoRequest.builder()
//                                    .erpNo(config.getJumpCode()).build()).getContext().getGoodsInfo();
//                            config.setJumpSkuId(goodsInfoVO.getGoodsInfoId());
//                        }
//                    });
//                }
//            });
//        }
//        return BaseResponse.success(AdvertisingRetailListResponse
//                .builder()
//                .advertisingRetailVOList(advertisingRetailVOList)
//                .build());
//    }
//
//    @ApiOperation(value = "获取散批推荐商品分类（缓存级）")
//    @PostMapping("/bulk-goods-recommend")
//    public BaseResponse<GoodsCateListByConditionResponse> getRetailGoodsRecommendByCache(){
//        return BaseResponse.success(goodsCateQueryProvider.getRetailRecommendByCache().getContext());
//    }
}
