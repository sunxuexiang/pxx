package com.wanmi.sbc.goods.api.provider.goodsrecommendgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐商品查询服务Provider</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsRecommendGoodsQueryProvider")
public interface GoodsRecommendGoodsQueryProvider {

    /**
     * 分页查询商品推荐商品API
     *
     * @param goodsRecommendGoodsPageReq 分页请求参数和筛选对象 {@link GoodsRecommendGoodsPageRequest}
     * @return 商品推荐商品分页列表信息 {@link GoodsRecommendGoodsPageResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/page")
    BaseResponse<GoodsRecommendGoodsPageResponse> page(@RequestBody @Valid GoodsRecommendGoodsPageRequest goodsRecommendGoodsPageReq);

    /**
     * 列表查询商品推荐商品API
     *
     * @param goodsRecommendGoodsListReq 列表请求参数和筛选对象 {@link GoodsRecommendGoodsListRequest}
     * @return 商品推荐商品的列表信息 {@link GoodsRecommendGoodsListResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/list")
    BaseResponse<GoodsRecommendGoodsListResponse> list(@RequestBody @Valid GoodsRecommendGoodsListRequest goodsRecommendGoodsListReq);

    /**
     * 单个查询商品推荐商品API
     *
     * @param goodsRecommendGoodsByIdRequest 单个查询商品推荐商品请求参数 {@link GoodsRecommendGoodsByIdRequest}
     * @return 商品推荐商品详情 {@link GoodsRecommendGoodsByIdResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/get-by-id")
    BaseResponse<GoodsRecommendGoodsByIdResponse> getById(@RequestBody @Valid GoodsRecommendGoodsByIdRequest goodsRecommendGoodsByIdRequest);

}

