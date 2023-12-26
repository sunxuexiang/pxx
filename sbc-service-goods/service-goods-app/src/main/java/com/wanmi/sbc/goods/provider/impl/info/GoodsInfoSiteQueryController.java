package com.wanmi.sbc.goods.provider.impl.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoSiteQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsInfoDetailResponse;
import com.wanmi.sbc.goods.info.service.GoodsInfoSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: wanggang
 * @createDate: 2018/11/1 14:14
 * @version: 1.0
 */
@RestController
@Validated
public class GoodsInfoSiteQueryController implements GoodsInfoSiteQueryProvider {

    @Autowired
    private GoodsInfoSiteService goodsInfoSiteService;

    /**
     * 获取商品详情
     * 计算会员和订货区间
     *
     * @param goodsInfoRequest {@link GoodsInfoRequest }
     * @return 商品详情 {@link GoodsInfoDetailByGoodsInfoResponse }
     */
    
    @Override
    public BaseResponse<GoodsInfoDetailByGoodsInfoResponse> getByGoodsInfo(@RequestBody @Valid GoodsInfoRequest goodsInfoRequest) {
        GoodsInfoDetailResponse goodsInfoDetailResponse = goodsInfoSiteService.detail(goodsInfoRequest);
        GoodsInfoDetailByGoodsInfoResponse goodsInfoDetailByGoodsInfoResponse = GoodsInfoConvert.toGoodsInfoDetailByGoodsInfoResponse(goodsInfoDetailResponse);
        return BaseResponse.success(goodsInfoDetailByGoodsInfoResponse);
    }
}
