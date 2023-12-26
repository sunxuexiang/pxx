package com.wanmi.sbc.shopcart.provider.impl.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.order.RetailShopCartApiRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.shopcart.cart.RetailShopCartService;
import com.wanmi.sbc.shopcart.cart.request.RetailShopCartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-04
 */
@Validated
@RestController
public class RetailShopCartController implements RetailShopCartProvider {

    @Autowired
    private RetailShopCartService retailShopCartService;

    /**
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return
     */
    @Override
    public BaseResponse save(@RequestBody @Valid PurchaseSaveRequest request) {
        retailShopCartService.save(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saveWithCache(@RequestBody @Valid PurchaseSaveRequest request) {
        retailShopCartService.saveWithCache(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return
     */
    @Override
    public BaseResponse batchSave(@RequestBody @Valid PurchaseBatchSaveRequest request) {
        retailShopCartService.batchSave(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchSaveAndCache(@RequestBody @Valid PurchaseBatchSaveRequest request) {
        retailShopCartService.batchSaveAndCache(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return
     */
    @Override
    public BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        retailShopCartService.updateNum(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateNumWithCache(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        retailShopCartService.updateNumWithCache(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateNumList(@Valid PurchaseUpdateNumRequest request) {
        retailShopCartService.updateNumByIds(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return
     */
    @Override
    public BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request) {
        retailShopCartService.delete(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delete(RetailShopCartApiRequest request) {
        retailShopCartService.delete(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteWithCache(@RequestBody @Valid PurchaseDeleteRequest request) {
        retailShopCartService.deleteWithCache(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return
     */
    @Override
    public BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request) {
        retailShopCartService.addFollow(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return
     */
    @Override
    public BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request) {
        retailShopCartService.clearLoseGoods(request.getUserId(), request.getDistributeChannel());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request) {
        PurchaseListResponse purchaseListResponse = retailShopCartService.calcAmount(KsBeanUtil.convert(request.getPurchaseCalcAmount(), PurchaseListResponse.class),
                request.getCustomerVO(), request.getGoodsInfoIds());
        return BaseResponse.success(purchaseListResponse);
    }

    /**
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = retailShopCartService.calcMarketingByMarketingIdBase(
                request.getMarketingId(),
                KsBeanUtil.convert(request.getCustomer(), CustomerVO.class),
                request.getFrontRequest(),
                request.getGoodsInfoIds(),
                request.getIsPurchase(),
                new ArrayList<>(),
                request.getWareId());
        return BaseResponse.success(KsBeanUtil.convert(purchaseMarketingCalcResponse, PurchaseCalcMarketingResponse.class));
    }

    @Override
    public BaseResponse<PurchaseCalcMarketingResponse> calcCouponActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = retailShopCartService
                .calcCouponActivityByActivityIdBase(request.getActivityId(),KsBeanUtil.convert(request.getCustomer(), CustomerVO.class),request.getWareId());
        return BaseResponse.success(KsBeanUtil.convert(purchaseMarketingCalcResponse, PurchaseCalcMarketingResponse.class));
    }

    /**
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return
     */
    @Override
    public BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request) {
        retailShopCartService.syncGoodsMarketings(request.getGoodsMarketingMap(), request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request) {
        retailShopCartService.modifyGoodsMarketing(request.getGoodsInfoId(), request.getMarketingId(), KsBeanUtil.convert(request.getCustomer(), CustomerVO.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request) {
        List<GoodsInfoVO> goodsInfoList = retailShopCartService.fillBuyCount(request.getGoodsInfoList(), request.getCustomerId(), request.getInviteeId());
        PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = new PurchaseFillBuyCountResponse();
        purchaseFillBuyCountResponse.setGoodsInfoList(goodsInfoList);
        return BaseResponse.success(purchaseFillBuyCountResponse);
    }

    /**
     * 合并登录前后采购单
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse mergePurchase(@RequestBody @Valid PurchaseMergeRequest request) {
        retailShopCartService.mergePurchase(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request){
        return BaseResponse.success(retailShopCartService.getPurchasesMarketing(request.getGoodsInfoIds(),
                request.getGoodsInfos(), request.getCustomer(), request.getWareId()));
    }

    @Override
    public BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request) {
        return BaseResponse.success(retailShopCartService.getSkuPurchaseNum(request));
    }
}
