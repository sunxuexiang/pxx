package com.wanmi.sbc.shopcart.api.provider.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description: 零售采购单查询接口
 * @author: XinJiang
 * @time: 2022/3/10 14:15
 */
@FeignClient(value = "${application.shopcart.name}", url="${feign.url.cart:#{null}}", contextId = "CartRetailShopCartQueryProvider")
public interface RetailShopCartQueryProvider {

    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/update-check-staues")
    BaseResponse updateCheckStaues(@RequestBody @Valid CheckedRetailCartRequest request);
    /**
     * 查询迷你采购单
     *
     * @param request 查询迷你采购单请求结构 {@link PurchaseMiniListRequest}
     * @return {@link PurchaseMiniListResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/mini-list")
    BaseResponse<PurchaseMiniListResponse> minilist(@RequestBody @Valid PurchaseMiniListRequest request);

    /**
     * 采购单列表
     *
     * @param request 采购单列表请求结构 {@link PurchaseListRequest}
     * @return {@link PurchaseListResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/list")
    BaseResponse<PurchaseListResponse> list(@RequestBody @Valid PurchaseListRequest request);

    /**
     * 查询采购单
     *
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return {@link PurchaseQueryResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/query")
    BaseResponse<PurchaseQueryResponse> query(@RequestBody @Valid PurchaseQueryRequest request);

    /**
     * 获取店铺下，是否有优惠券营销，展示优惠券标签
     *
     * @param request 获取店铺下，是否有优惠券营销，展示优惠券标签请求结构 {@link PurchaseGetStoreCouponExistRequest}
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-store-coupon-exist")
    BaseResponse<PurchaseGetStoreCouponExistResponse> getStoreCouponExist(@RequestBody @Valid PurchaseGetStoreCouponExistRequest request);

    /**
     * 获取店铺营销信息
     *
     * @param request 获取店铺营销信息请求结构 {@link PurchaseGetStoreMarketingRequest}
     * @return {@link PurchaseGetStoreMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-store-marketing")
    BaseResponse<PurchaseGetStoreMarketingResponse> getStoreMarketing(@RequestBody @Valid PurchaseGetStoreMarketingRequest request);

    /**
     * 获取采购单商品选择的营销
     *
     * @param request 获取采购单商品选择的营销请求结构 {@link PurchaseQueryGoodsMarketingListRequest}
     * @return {@link PurchaseQueryGoodsMarketingListResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/query-goods-marketing-list")
    BaseResponse<PurchaseQueryGoodsMarketingListResponse> queryGoodsMarketingList(@RequestBody @Valid PurchaseQueryGoodsMarketingListRequest request);

    /**
     * 获取商品营销信息
     *
     * @param request 获取商品营销信息请求结构 {@link PurchaseGetGoodsMarketingRequest}
     * @return {@link PurchaseGetGoodsMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-goods-marketing")
    BaseResponse<PurchaseGetGoodsMarketingResponse> getGoodsMarketing(@RequestBody @Valid PurchaseGetGoodsMarketingRequest request);

    /**
     * 获取采购单商品数量
     *
     * @param request 获取采购单商品数量请求结构 {@link PurchaseCountGoodsRequest}
     * @return {@link PurchaseCountGoodsResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/count-goods")
    BaseResponse<PurchaseCountGoodsResponse> countGoods(@RequestBody @Valid PurchaseCountGoodsRequest request);

    /**
     * 未登录时,根据前端缓存信息查询迷你购物车信息
     *
     * @param frontReq
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/mini-list-front")
    BaseResponse<MiniPurchaseResponse> miniListFront(@RequestBody @Valid PurchaseFrontMiniRequest frontReq);

    /**
     * 未登陆时,根据前端传入的采购单信息,查询组装必要信息
     *
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/list-front")
    BaseResponse<PurchaseResponse> listFront(@RequestBody @Valid PurchaseFrontRequest request);

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/validate-and-set-goods-marketings")
    BaseResponse<PurchaseResponse> validateAndSetGoodsMarketings(@RequestBody @Valid ValidateAndSetGoodsMarketingsRequest request);


    /**
     * 采购单社交分销信息
     *
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/distribution")
    BaseResponse<Purchase4DistributionResponse> distribution(@RequestBody @Valid Purchase4DistributionRequest request);

    /**
     * 查询购物车信息
     *
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/purchase-info")
    BaseResponse<PurchaseListResponse> purchaseInfo(@RequestBody @Valid PurchaseListRequest request);


    /**
     * 查询购物车信息
     *
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/purchase-cache-info")
    BaseResponse<MarketingGroupCardResponse> purchaseInfoAndCache(@RequestBody @Valid PurchaseListRequest request);


    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/check-purchas-num")
    BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody @Valid CheckPurchaseNumRequest request);

    /**
     * 获取商品店铺营销
     *
     * @param request
     * @return
     */

    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/purchase-info-new")
    BaseResponse<PurchaseListResponse> newPurchaseInfo(@RequestBody @Valid PurchaseListNewRequest request);

    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-store-markeings")
    BaseResponse<PurchaseStoreMarketingResponse> getStoreMarketings(@RequestBody @Valid PurchaseStoreMarketingRequest request);


    /**
     * 获取商品店铺营销
     *
     * @param request
     * @return
     */

    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-order-markeings")
    BaseResponse<PurchaseOrderMarketingResponse> getOrderMarketings(@RequestBody @Valid PurchaseOrderMarketingRequest request);

    /**
     * 查询采购车配置
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/query-procurement-config")
    BaseResponse<ProcurementConfigResponse> queryProcurementConfig();

    /**
     * 查询我的囤货
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/query-my-pile-purchase-list")
    BaseResponse<PilePurchaseResponse> queryMyPilePurchaseList(@RequestBody @Valid PilePurchaseRequest request);

    /**
     * 查询客户囤货
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/pile-query")
    BaseResponse<PurchaseQueryResponse> pileQuery(@RequestBody @Valid PurchaseQueryRequest request);

    /**
     * 通过客户id和商品id查询囤货数量
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/pile-query-by-customerId-and-skuId")
    BaseResponse<Long> getGoodsNumByCustomerIdAndSkuId(@RequestBody @Valid PurchaseQueryRequest request);

    /**
     * 通过客户id囤货总数量
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-pile-count-num-by-customerId")
    BaseResponse<Long> getPileCountNumByCustomerId(@RequestBody @Valid PurchaseQueryRequest request);

    /**
     * 获取购物车总商品数量
     *
     * @param request 获取购物车总商品数量请求结构 {@link PurchaseCountGoodsRequest}
     * @return {@link PurchaseCountGoodsResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/and/purchase/count-goods")
    BaseResponse<PurchaseCountGoodsResponse> calShopCartAndPurchaseNum(@RequestBody @Valid PurchaseCountGoodsRequest request);

    /**
     * 查询购物车是否包含套装商品
     *
     * @param request 查询购物车是否包含套装商品 {@link PurchaseQueryRequest}
     * @return {@link PurchaseGetGoodsMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/query/suit-goods-by-purchase")
    BaseResponse<PurchaseGetGoodsMarketingResponse> querySuitGoodsByPurchase(@RequestBody @Valid PurchaseQueryRequest request);

}
