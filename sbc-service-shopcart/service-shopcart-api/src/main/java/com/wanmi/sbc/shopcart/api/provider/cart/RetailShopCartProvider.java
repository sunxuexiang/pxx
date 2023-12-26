package com.wanmi.sbc.shopcart.api.provider.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.shopcart.api.provider.order.RetailShopCartApiRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 零售采购单操作接口
 * @Author: XinJiang
 * @Date: 2022/3/10 14:11
 */
@FeignClient(value = "${application.shopcart.name}", url="${feign.url.cart:#{null}}", contextId = "CartRetailShopCartProvider")
public interface RetailShopCartProvider {

    /**
     * 新增采购单
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/save")
    BaseResponse save(@RequestBody @Valid PurchaseSaveRequest request);

    /**
     * 新增采购单
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/saveWithCache")
    BaseResponse saveWithCache(@RequestBody @Valid PurchaseSaveRequest request);

    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/batch-save")
    BaseResponse batchSave(@RequestBody @Valid PurchaseBatchSaveRequest request);

    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/batch-save-and-cache")
    BaseResponse batchSaveAndCache(@RequestBody @Valid PurchaseBatchSaveRequest request);


    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/update-num")
    BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/update-num-with-cache")
    BaseResponse updateNumWithCache(@RequestBody @Valid PurchaseUpdateNumRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/delete")
    BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request);

    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/deleteByApi")
    BaseResponse delete(@RequestBody @Valid RetailShopCartApiRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/delete-with-cache")
    BaseResponse deleteWithCache(@RequestBody @Valid PurchaseDeleteRequest request);

    /**
     * 采购单商品移入收藏夹
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/add-follow")
    BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request);

    /**
     * 清除失效商品
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/clear-lose-goods")
    BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request);

    /**
     * 计算采购单金额
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/calc-amount")
    BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request);

    /**
     * 计算采购单中参加同种营销的商品列表/总额/优惠
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return {@link PurchaseCalcMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/calc-marketing-by-marketing-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 计算采购单中参加同种赠券活动的商品列表/总额/优惠
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/calc-coupon-activity-by-activity-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcCouponActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 同步商品使用的营销
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/sync-goods-marketings")
    BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request);

    /**
     * 修改商品使用的营销
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/modify-goods-marketing")
    BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request);


    /**
     * 填充客户购买数
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return {@link PurchaseFillBuyCountResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/fill-buy-count")
    BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request);

    /**
     * 合并登录前后采购单
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/merge-purchase")
    BaseResponse mergePurchase(@RequestBody @Valid PurchaseMergeRequest request);

    /**
     * 获取采购营销信息及同步商品营销
     * @param request 获取采购营销信息及同步商品营销请求结构 {@link PurchaseMarketingRequest}
     * @return {@link PurchaseMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-purchases-marketing")
    BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/update-num-list")
    BaseResponse updateNumList(@RequestBody @Valid PurchaseUpdateNumRequest request);

    /**
     * 查询sku购物车里的数量
     */
    @PostMapping("/cart/${application.shopcart.version}/retail/shopcart/get-sku-purchase-num")
    BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request);

}
