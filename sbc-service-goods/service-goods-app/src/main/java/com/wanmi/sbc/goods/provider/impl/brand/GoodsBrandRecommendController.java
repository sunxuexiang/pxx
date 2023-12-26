package com.wanmi.sbc.goods.provider.impl.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandRecommendProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandRecommendRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandRecommendResponse;
import com.wanmi.sbc.goods.brand.service.GoodsBrandRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
public class GoodsBrandRecommendController implements GoodsBrandRecommendProvider {

    @Autowired
    private GoodsBrandRecommendService goodsBrandRecommendService;

    /**
     * 添加商品品牌推荐
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse addGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        goodsBrandRecommendService.addGoodsBrandRecommend(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品品牌推荐上下架
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateAddedGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        goodsBrandRecommendService.updateAddedGoodsBrandRecommend(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除商品品牌推荐
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse deleteGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        goodsBrandRecommendService.deleteGoodsBrandRecommend(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询商品品牌推荐列表
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<GoodsBrandRecommendResponse> listGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        GoodsBrandRecommendResponse goodsBrandRecommendResponse = goodsBrandRecommendService.listGoodsBrandRecommend(request);
        return BaseResponse.success(goodsBrandRecommendResponse);
    }

    /**
     * 查询商品品牌推荐列表
     * -- 数据进行了补充
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<GoodsBrandRecommendResponse> listGoodsBrandRecommendDataSupplement(GoodsBrandRecommendRequest request) {
        GoodsBrandRecommendResponse goodsBrandRecommendResponse = goodsBrandRecommendService.listGoodsBrandRecommendDataSupplement(request);
        return BaseResponse.success(goodsBrandRecommendResponse);
    }

    /**
     * 修改商品品牌推荐名称是否显示
     *
     * @param nameStatus
     * @return
     */
    @Override
    public BaseResponse updateGoodsBrandRecommendNameStatus(Integer nameStatus) {
        goodsBrandRecommendService.updateGoodsBrandRecommendNameStatus(nameStatus);
        return BaseResponse.SUCCESSFUL();
    }
}
