package com.wanmi.sbc.shopcart.api.provider.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.shopcart.api.provider.order.ShopCartApiRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>采购单操作接口</p>
 * author: sunkun
 * Date: 2018-11-30
 */
@FeignClient(value = "${application.shopcart.name}", url="${feign.url.cart:#{null}}", contextId = "CartShopCartProvider")
public interface ShopCartProvider {

    /**
     * 新增采购单
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/save")
    BaseResponse save(@RequestBody @Valid PurchaseSaveRequest request);


    /**
     * 新增采购单
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/newsave")
    BaseResponse newsave(@RequestBody @Valid PurchaseSaveRequest request);

    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/batch-save")
    BaseResponse batchSave(@RequestBody @Valid PurchaseBatchSaveRequest request);


    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/datch-save")
    BaseResponse batchSaveDevanning(@RequestBody @Valid PurchaseBatchSaveRequest request);


    /**
     * 批量加入采购单
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/new-datch-save")
    BaseResponse batchSaveNewDevanning(@RequestBody @Valid PurchaseBatchSaveRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/update-num")
    BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request);



    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/update-devanning-num")
    BaseResponse updateDevanningNum(@RequestBody @Valid PurchaseUpdateNumRequest request);


    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/update-devanning-numnew")
    BaseResponse updateDevanningNumNew(@RequestBody @Valid PurchaseUpdateNumRequest request);


    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/delete")
    BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request);

    @PostMapping("/cart/${application.shopcart.version}/shopcart/deleteByApi")
    BaseResponse delete(@RequestBody ShopCartApiRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/devanningdelete")
    BaseResponse devanningDelete(@RequestBody @Valid PurchaseDeleteRequest request);

    /**
     * 删除采购单
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/devanningdeleteCache")
    BaseResponse devanningDeleteCache(@RequestBody @Valid PurchaseDeleteRequest request);


    /**
     * 采购单商品移入收藏夹
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/add-follow")
    BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request);

    /**
     * 清除失效商品
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/clear-lose-goods")
    BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request);

    /**
     * 计算采购单金额
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/calc-amount")
    BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request);

    /**
     * 计算采购单中参加同种营销的商品列表/总额/优惠
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return {@link PurchaseCalcMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/calc-marketing-by-marketing-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 计算采购单中参加同种赠券活动的商品列表/总额/优惠
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/calc-coupon-activity-by-activity-id")
    BaseResponse<PurchaseCalcMarketingResponse> calcCouponActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    @PostMapping("/cart/${application.shopcart.version}/shopcart/calc-coin-activity-by-activity-id")
    BaseResponse<PurchaseCoinActivityCalcResponse> calcCoinActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request);

    /**
     * 同步商品使用的营销
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/sync-goods-marketings")
    BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request);

    /**
     * 修改商品使用的营销
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/modify-goods-marketing")
    BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request);


    /**
     * 填充客户购买数
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return {@link PurchaseFillBuyCountResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/fill-buy-count")
    BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request);

    /**
     * 合并登录前后采购单
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/merge-purchase")
    BaseResponse mergePurchase(@RequestBody @Valid PurchaseMergeRequest request);

    /**
     * 获取采购营销信息及同步商品营销
     * @param request 获取采购营销信息及同步商品营销请求结构 {@link PurchaseMarketingRequest}
     * @return {@link PurchaseMarketingResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/get-purchases-marketing")
    BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request);

    /**
     * 商品收藏调整商品数量
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/update-num-list")
    BaseResponse updateNumList(@RequestBody @Valid PurchaseUpdateNumRequest request);

    /**
     * 查询sku购物车里的数量
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/get-sku-purchase-num")
    BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request);


    /**
            * copy已有的采购单
     * @param request copy已有的采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/copy-save")
    BaseResponse copySave(@RequestBody @Valid PurchaseSaveRequest request);

    /**
     * copy已有的采购单
     * @param request copy已有的采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/new/copy-save")
    BaseResponse copySaveNew(@RequestBody @Valid PurchaseSaveRequest request);


    /**
     * 通过用户id删除shopcar
     * @param request
     * @return
     */
    @PostMapping("/cart/${application.shopcart.version}/shopcart/del/snapshop-del")
    BaseResponse delFirstSnapShopAndMarkeing(@RequestBody @Valid String request);





    @PostMapping("/cart/${application.shopcart.version}/shopcart/refresh/shopcar")
    BaseResponse refreshShopCarRedis();

}

