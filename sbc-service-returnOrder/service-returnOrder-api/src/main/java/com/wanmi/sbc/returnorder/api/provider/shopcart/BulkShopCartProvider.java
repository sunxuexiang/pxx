package com.wanmi.sbc.returnorder.api.provider.shopcart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseCalcMarketingResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseListResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseMarketingResponse;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.returnorder.api.request.purchase.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>采购单操作接口</p>
 * author: sunkun
 * Date: 2018-11-30
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnBulkShopCartProvider")
public interface BulkShopCartProvider {

    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/update-check-staues")
    BaseResponse updateCheckStaues(@RequestBody @Valid CheckedBulkCartRequest request);

    //***********



    /**
     * 新增采购单
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/newsave")
    BaseResponse newsave(@RequestBody @Valid PurchaseSaveRequest request);



    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/new-datch-save")
    BaseResponse batchSaveNewDevanning(@RequestBody @Valid PurchaseBatchSaveRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/update-num")
    BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request);



    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/update-devanning-num")
    BaseResponse updateDevanningNum(@RequestBody @Valid PurchaseUpdateNumRequest request);


    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/update-devanning-numnew")
    BaseResponse updateDevanningNumNew(@RequestBody @Valid PurchaseUpdateNumRequest request);


    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/delete")
    BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/devanningdelete")
    BaseResponse devanningDelete(@RequestBody @Valid PurchaseDeleteRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/devanningdeleteCache")
    BaseResponse devanningDeleteCache(@RequestBody @Valid PurchaseDeleteRequest request);


    /**
     * 采购单商品移入收藏夹
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/add-follow")
    BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request);

    /**
     * 清除失效商品
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/clear-lose-goods")
    BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request);

    /**
     * 计算采购单金额
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/calc-amount")
    BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request);

    /**
     * 计算采购单中参加同种营销的商品列表/总额/优惠
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return {@link PurchaseCalcMarketingResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/calc-marketing-by-marketing-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 计算采购单中参加同种赠券活动的商品列表/总额/优惠
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/calc-coupon-activity-by-activity-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcCouponActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 同步商品使用的营销
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/sync-goods-marketings")
    BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request);

    /**
     * 修改商品使用的营销
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/modify-goods-marketing")
    BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request);


    /**
     * 填充客户购买数
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return {@link PurchaseFillBuyCountResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/fill-buy-count")
    BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request);

    /**
     * 获取采购营销信息及同步商品营销
     * @param request 获取采购营销信息及同步商品营销请求结构 {@link PurchaseMarketingRequest}
     * @return {@link PurchaseMarketingResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/get-purchases-marketing")
    BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/update-num-list")
    BaseResponse updateNumList(@RequestBody @Valid PurchaseUpdateNumRequest request);

    /**
     * 查询sku购物车里的数量
     */
    @PostMapping("/returnOrder/${application.order.version}/bulk/shopcart/get-sku-purchase-num")
    BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request);


}

