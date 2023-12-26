package com.wanmi.sbc.shopcart.api.provider.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>采购单查询接口</p>
 * author: sunkun
 * Date: 2018-11-30
 */
@FeignClient(value = "${application.shopcart.name}", url="${feign.url.cart:#{null}}", contextId = "CartBulkShopCartQueryProvider")
public interface BulkShopCartQueryProvider {

    /**
     * 查询购物车信息
     *
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcar/purchase-cache-info")
    BaseResponse<MarketingGroupCardResponse> purchaseInfoCache(@RequestBody @Valid PurchaseListRequest request);



    //***************
    /**
     * 查询采购单
     *
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return {@link PurchaseQueryResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/query")
    BaseResponse<PurchaseQueryResponse> query(@RequestBody @Valid PurchaseQueryRequest request);



    /**
     * 查询采购单
     *
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return {@link PurchaseQueryResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/queryandcache")
    BaseResponse<PurchaseQueryResponse> queryShopCarExit(@RequestBody @Valid PurchaseQueryCacheRequest request);


    /**
     * 获取店铺下，是否有优惠券营销，展示优惠券标签
     *
     * @param request 获取店铺下，是否有优惠券营销，展示优惠券标签请求结构 {@link PurchaseGetStoreCouponExistRequest}
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/get-store-coupon-exist")
    BaseResponse<PurchaseGetStoreCouponExistResponse> getStoreCouponExist(@RequestBody @Valid PurchaseGetStoreCouponExistRequest request);

    /**
     * 获取店铺营销信息
     *
     * @param request 获取店铺营销信息请求结构 {@link PurchaseGetStoreMarketingRequest}
     * @return {@link PurchaseGetStoreMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/get-store-marketing")
    BaseResponse<PurchaseGetStoreMarketingResponse> getStoreMarketing(@RequestBody @Valid PurchaseGetStoreMarketingRequest request);

    /**
     * 获取采购单商品选择的营销
     *
     * @param request 获取采购单商品选择的营销请求结构 {@link PurchaseQueryGoodsMarketingListRequest}
     * @return {@link PurchaseQueryGoodsMarketingListResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/query-goods-marketing-list")
    BaseResponse<PurchaseQueryGoodsMarketingListResponse> queryGoodsMarketingList(@RequestBody @Valid PurchaseQueryGoodsMarketingListRequest request);

    /**
     * 获取商品营销信息
     *
     * @param request 获取商品营销信息请求结构 {@link PurchaseGetGoodsMarketingRequest}
     * @return {@link PurchaseGetGoodsMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/get-goods-marketing")
    BaseResponse<PurchaseGetGoodsMarketingResponse> getGoodsMarketing(@RequestBody @Valid PurchaseGetGoodsMarketingRequest request);

    /**
     * 获取采购单商品数量
     *
     * @param request 获取采购单商品数量请求结构 {@link PurchaseCountGoodsRequest}
     * @return {@link PurchaseCountGoodsResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/count-goods")
    BaseResponse<PurchaseCountGoodsResponse> countGoods(@RequestBody @Valid PurchaseCountGoodsRequest request);

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/validate-and-set-goods-marketings")
    BaseResponse<PurchaseResponse> validateAndSetGoodsMarketings(@RequestBody @Valid ValidateAndSetGoodsMarketingsRequest request);


    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/check-purchas-num")
    BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody @Valid CheckPurchaseNumRequest request);

    /**
     * 查询采购车配置
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/bulk/shopcart/query-procurement-config")
    BaseResponse<ProcurementConfigResponse> queryProcurementConfig();
}
