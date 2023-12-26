package com.wanmi.sbc.goods.api.provider.merchantconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsPageResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐商品查询服务Provider</p>
 *
 * @author sgy
 * @date 2013-06-07 10:53:36
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "merchantConfigGoodsQueryProvider")
public interface MerchantConfigGoodsQueryProvider {

    /**
     * 分页查询商品推荐商品API
     *
     * @param goodsRecommendGoodsPageReq 分页请求参数和筛选对象 {@link GoodsRecommendGoodsPageRequest}
     * @return 商品推荐商品分页列表信息 {@link GoodsRecommendGoodsPageResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/merchantConfigGoods/page")
    BaseResponse<MerchantConfigGoodsPageResponse> page(@RequestBody @Valid MerchantConfigGoodsPageRequest goodsRecommendGoodsPageReq);

    /**
     * 列表查询商品推荐商品API
     *
     * @param merchantConfigGoodsListRequest 列表请求参数和筛选对象 {@link GoodsRecommendGoodsListRequest}
     * @return 商品推荐商品的列表信息 {@link GoodsRecommendGoodsListResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/merchantConfigGoods/list")
    BaseResponse<MerchantConfigGoodsListResponse> list(@RequestBody @Valid MerchantConfigGoodsQueryRequest merchantConfigGoodsListRequest  );
    /**
     * 列表查询商品推荐商品API
     *
     * @param merchantConfigGoodsListRequest 列表请求参数和筛选对象 {@link GoodsRecommendGoodsListRequest}
     * @return 商品推荐商品的列表信息 {@link GoodsRecommendGoodsListResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/merchantConfigGoods/notList")
    BaseResponse<MerchantConfigGoodsListResponse> notList(@RequestBody @Valid MerchantConfigGoodsQueryRequest merchantConfigGoodsListRequest  );

    /**
     * 单个查询商品推荐商品API
     *
     * @param goodsRecommendGoodsByIdRequest 单个查询商品推荐商品请求参数 {@link GoodsRecommendGoodsByIdRequest}
     * @return 商品推荐商品详情 {@link GoodsRecommendGoodsByIdResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/merchantConfigGoods/get-by-id")
    BaseResponse<MerchantConfigGoodsByIdResponse> getById(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsByIdRequest);

    /**
     * 提供给app的推荐商品
     *
     * @param appGoodsQueryRequest 提供给app的推荐商品 {@link GoodsRecommendGoodsByIdRequest}
     * @return 提供给app的推荐商品 {@link GoodsRecommendGoodsByIdResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/merchantConfigGoods/get-app-goods-info")
    BaseResponse<GoodsRecommendSettingVO> getAppGoodsInfo(@RequestBody @Valid AppGoodsQueryRequest appGoodsQueryRequest);


}

