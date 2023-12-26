package com.wanmi.sbc.returnorder.api.provider.shopcart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.purchase.*;
import com.wanmi.sbc.returnorder.api.response.purchase.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>采购单查询接口</p>
 * author: sunkun
 * Date: 2018-11-30
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnBulkShopCartQueryProvider")
public interface BulkShopCartQueryProvider {

    /**
     * 查询购物车信息
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcar/purchase-cache-info")
    BaseResponse<MarketingGroupCardResponse> purchaseInfoCache(@RequestBody @Valid PurchaseListRequest request);



    //***************
    /**
     * 查询采购单
     *
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return {@link PurchaseQueryResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/query")
    BaseResponse<PurchaseQueryResponse> query(@RequestBody @Valid PurchaseQueryRequest request);



    /**
     * 查询采购单
     *
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return {@link PurchaseQueryResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/queryandcache")
    BaseResponse<PurchaseQueryResponse> queryShopCarExit(@RequestBody @Valid PurchaseQueryCacheRequest request);


    /**
     * 获取店铺下，是否有优惠券营销，展示优惠券标签
     *
     * @param request 获取店铺下，是否有优惠券营销，展示优惠券标签请求结构 {@link PurchaseGetStoreCouponExistRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/get-store-coupon-exist")
    BaseResponse<PurchaseGetStoreCouponExistResponse> getStoreCouponExist(@RequestBody @Valid PurchaseGetStoreCouponExistRequest request);

    /**
     * 获取店铺营销信息
     *
     * @param request 获取店铺营销信息请求结构 {@link PurchaseGetStoreMarketingRequest}
     * @return {@link PurchaseGetStoreMarketingResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/get-store-marketing")
    BaseResponse<PurchaseGetStoreMarketingResponse> getStoreMarketing(@RequestBody @Valid PurchaseGetStoreMarketingRequest request);

    /**
     * 获取采购单商品选择的营销
     *
     * @param request 获取采购单商品选择的营销请求结构 {@link PurchaseQueryGoodsMarketingListRequest}
     * @return {@link PurchaseQueryGoodsMarketingListResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/query-goods-marketing-list")
    BaseResponse<PurchaseQueryGoodsMarketingListResponse> queryGoodsMarketingList(@RequestBody @Valid PurchaseQueryGoodsMarketingListRequest request);

    /**
     * 获取商品营销信息
     *
     * @param request 获取商品营销信息请求结构 {@link PurchaseGetGoodsMarketingRequest}
     * @return {@link PurchaseGetGoodsMarketingResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/get-goods-marketing")
    BaseResponse<PurchaseGetGoodsMarketingResponse> getGoodsMarketing(@RequestBody @Valid PurchaseGetGoodsMarketingRequest request);

    /**
     * 获取采购单商品数量
     *
     * @param request 获取采购单商品数量请求结构 {@link PurchaseCountGoodsRequest}
     * @return {@link PurchaseCountGoodsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/count-goods")
    BaseResponse<PurchaseCountGoodsResponse> countGoods(@RequestBody @Valid PurchaseCountGoodsRequest request);

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/validate-and-set-goods-marketings")
    BaseResponse<PurchaseResponse> validateAndSetGoodsMarketings(@RequestBody @Valid ValidateAndSetGoodsMarketingsRequest request);


    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/check-purchas-num")
    BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody @Valid CheckPurchaseNumRequest request);

    /**
     * 查询采购车配置
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/query-procurement-config")
    BaseResponse<ProcurementConfigResponse> queryProcurementConfig();
}
