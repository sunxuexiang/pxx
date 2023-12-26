package com.wanmi.sbc.goods.api.provider.brand;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandRecommendRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandRecommendResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", contextId = "GoodsBrandRecommendProvider")
public interface GoodsBrandRecommendProvider {

    /**
     * 添加商品品牌推荐
     *
     * @param request
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/addGoodsBrandRecommend")
    BaseResponse addGoodsBrandRecommend(@RequestBody @Valid GoodsBrandRecommendRequest request);

    /**
     * 修改商品品牌推荐上下架
     *
     * @param request
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/updateAddedGoodsBrandRecommend")
    BaseResponse updateAddedGoodsBrandRecommend(@RequestBody @Valid GoodsBrandRecommendRequest request);

    /**
     * 删除商品品牌推荐
     *
     * @param request
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/deleteGoodsBrandRecommend")
    BaseResponse deleteGoodsBrandRecommend(@RequestBody @Valid GoodsBrandRecommendRequest request);

    /**
     * 查询商品品牌推荐列表
     *
     * @param request
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/listGoodsBrandRecommend")
    BaseResponse<GoodsBrandRecommendResponse> listGoodsBrandRecommend(@RequestBody @Valid GoodsBrandRecommendRequest request);

    /**
     * 查询商品品牌推荐列表
     *
     * @param request
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/listGoodsBrandRecommendDataSupplement")
    BaseResponse<GoodsBrandRecommendResponse> listGoodsBrandRecommendDataSupplement(@RequestBody @Valid GoodsBrandRecommendRequest request);

    /**
     * 修改商品品牌推荐名称是否显示
     * @return
     */
    @PostMapping("/goodsBrandRecommend/${application.goods.version}/updateGoodsBrandRecommendNameStatus")
    BaseResponse updateGoodsBrandRecommendNameStatus(@RequestParam("nameStatus") Integer nameStatus);
}
