package com.wanmi.sbc.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.RetailGoodsBaseController;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNoRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.request.GoodsByParentCateIdQueryRequest;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @description: 散批广告位API
 * @author: XinJiang
 * @time: 2022/4/19 14:11
 */
@Api(description = "散批广告位API", tags = "AdvertisingRetailBaseController")
@RestController
@RequestMapping(value = "/retail/advertising")
public class AdvertisingRetailBaseController {

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private AdvertisingRetailQueryProvider advertisingRetailQueryProvider;

    @Autowired
    private RetailGoodsBaseController retailGoodsBaseController;

    @ApiOperation(value = "获取散批广告信息（缓存级）")
    @PostMapping("/list-by-cache")
    public BaseResponse<AdvertisingRetailListResponse> listByCache() {
        List<AdvertisingRetailVO> advertisingRetailVOList = advertisingRetailQueryProvider.listByCache().getContext().getAdvertisingRetailVOList();
        if (CollectionUtils.isNotEmpty(advertisingRetailVOList)) {
            advertisingRetailVOList.forEach(advertisingRetailVO -> {
                if (AdvertisingType.BANNER.equals(advertisingRetailVO.getAdvertisingType())) {
                    advertisingRetailVO.getAdvertisingRetailConfigs().forEach(config -> {
                        if (AdvertisingRetailJumpType.GOODS.equals(config.getJumpType())) {
                            GoodsInfoVO goodsInfoVO = retailGoodsInfoQueryProvider.getGoodsInfoByErpNo(GoodsInfoByErpNoRequest.builder()
                                    .erpNo(config.getJumpCode()).build()).getContext().getGoodsInfo();
                            config.setJumpSkuId(goodsInfoVO.getGoodsInfoId());
                        }
                    });
                }
            });
        }
        return BaseResponse.success(AdvertisingRetailListResponse
                .builder()
                .advertisingRetailVOList(advertisingRetailVOList)
                .build());
    }

    @ApiOperation(value = "获取散批推荐商品分类（缓存级）")
    @PostMapping("/retail-goods-recommend")
    public BaseResponse<GoodsCateListByConditionResponse> getRetailGoodsRecommendByCache(HttpServletRequest httpRequest){
        GoodsCateListByConditionResponse context = goodsCateQueryProvider.getRetailRecommendByCache().getContext();
        Iterator<GoodsCateVO> iterator = context.getGoodsCateVOList().iterator();
        // 隐藏没有商品的分类
        while (iterator.hasNext()) {
            GoodsCateVO next = iterator.next();
            GoodsByParentCateIdQueryRequest goods = new GoodsByParentCateIdQueryRequest();
            goods.setCateIds(Collections.singletonList(next.getCateId()));
            goods.setBrandIds(new ArrayList<>());
            goods.setMatchWareHouseFlag(true);
            BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse = retailGoodsBaseController.cateBrandSortGoodslist(goods, httpRequest);
            if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse.getContext().getEsGoods().getContent())){
                iterator.remove();
            }
        }
        return BaseResponse.success(context);
    }
}
