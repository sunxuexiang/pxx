package com.wanmi.sbc.shopcart.provider.impl.cart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.order.ShopCartApiRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.shopcart.cart.ShopCartService;
import com.wanmi.sbc.shopcart.cart.request.ShopCartRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Validated
@RestController
public class ShopCartController implements ShopCartProvider {

    @Autowired
    private ShopCartService shopCartService;

    /**
     * @param request 新增采购单请求结构 {@link PurchaseSaveRequest}
     * @return
     */
    @Override
    public BaseResponse save(@RequestBody @Valid PurchaseSaveRequest request) {
        shopCartService.save(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse newsave(PurchaseSaveRequest request) {
        shopCartService.saveAndCache(KsBeanUtil.convert(request, ShopCartRequest.class),request.getMarketingid());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 批量加入采购单请求结构 {@link PurchaseBatchSaveRequest}
     * @return
     */
    @Override
    public BaseResponse batchSave(@RequestBody @Valid PurchaseBatchSaveRequest request) {
        shopCartService.batchSave(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchSaveDevanning(@Valid PurchaseBatchSaveRequest request) {
        shopCartService.devanningMergin(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchSaveNewDevanning(PurchaseBatchSaveRequest request) {
          shopCartService.cacheBatchSaveDevanning(KsBeanUtil.convert(request, ShopCartRequest.class));;
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return
     */
    @Override
    public BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        shopCartService.updateNum(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateDevanningNum(@Valid PurchaseUpdateNumRequest request) {
        shopCartService.devanningNumMergin(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateDevanningNumNew(PurchaseUpdateNumRequest request) {
         shopCartService.updateNumDevanningAndCache(KsBeanUtil.convert(request, ShopCartRequest.class),request.getMarketingid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateNumList(@Valid PurchaseUpdateNumRequest request) {
        shopCartService.updateNumByIds(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return
     */
    @Override
    public BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request) {
        shopCartService.delete(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delete(ShopCartApiRequest request) {
        shopCartService.delete(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse devanningDelete(@Valid PurchaseDeleteRequest request) {
        shopCartService.devanningDelete(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse devanningDeleteCache(PurchaseDeleteRequest request) {
        shopCartService.devanningDeleteCache(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return
     */
    @Override
    public BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request) {
        shopCartService.addFollow(KsBeanUtil.convert(request, ShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return
     */
    @Override
    public BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request) {
        shopCartService.clearLoseGoods(request.getUserId(), request.getDistributeChannel());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request) {
        PurchaseListResponse purchaseListResponse = shopCartService.calcAmount(KsBeanUtil.convert(request.getPurchaseCalcAmount(), PurchaseListResponse.class),
                request.getCustomerVO(), request.getGoodsInfoIds());
        return BaseResponse.success(purchaseListResponse);
    }

    /**
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = shopCartService.calcMarketingByMarketingIdBase(
                request.getMarketingId(),
                KsBeanUtil.convert(request.getCustomer(), CustomerVO.class),
                request.getFrontRequest(),
                request.getGoodsInfoIds(),
                request.getIsPurchase(),
                new ArrayList<>(),
                request.getWareId());
        log.info("ShopCartConroller calcMarketingByMarketingId result: {}",purchaseMarketingCalcResponse);
        return BaseResponse.success(KsBeanUtil.convert(purchaseMarketingCalcResponse, PurchaseCalcMarketingResponse.class));
    }

    @Override
    public BaseResponse<PurchaseCalcMarketingResponse> calcCouponActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = shopCartService
                .calcCouponActivityByActivityIdBase(request.getActivityId(),KsBeanUtil.convert(request.getCustomer(), CustomerVO.class),request.getWareId());
        return BaseResponse.success(KsBeanUtil.convert(purchaseMarketingCalcResponse, PurchaseCalcMarketingResponse.class));
    }

    @Override
    public BaseResponse<PurchaseCoinActivityCalcResponse> calcCoinActivityByActivityId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseCoinActivityCalcResponse response = shopCartService.calcCoinActivityByActivityId(request.getActivityId(), KsBeanUtil.convert(request.getCustomer(), CustomerVO.class), request.getWareId());
        return BaseResponse.success(response);
    }

    /**
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return
     */
    @Override
    public BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request) {
        shopCartService.syncGoodsMarketings(request.getGoodsMarketingMap(), request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request) {
        shopCartService.modifyGoodsMarketing(request.getGoodsInfoId(), request.getMarketingId(), KsBeanUtil.convert(request.getCustomer(), CustomerVO.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request) {
        List<GoodsInfoVO> goodsInfoList = shopCartService.fillBuyCount(request.getGoodsInfoList(), request.getCustomerId(), request.getInviteeId());
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
        shopCartService.mergePurchase(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request){
        return BaseResponse.success(shopCartService.getPurchasesMarketing(request.getGoodsInfoIds(),
                request.getGoodsInfos(), request.getCustomer(), request.getWareId()));
    }

    @Override
    public BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request) {
        return BaseResponse.success(shopCartService.getSkuPurchaseNum(request));
    }

    @Override
    public BaseResponse copySave(PurchaseSaveRequest request) {
        shopCartService.copySave(request);
        return BaseResponse.SUCCESSFUL();
    }
    @Override
    public BaseResponse copySaveNew(PurchaseSaveRequest request) {
        shopCartService.copySaveNew(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delFirstSnapShopAndMarkeing(String request) {
        shopCartService.delFirstSnapShopAndMarkeing(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refreshShopCarRedis() {
         shopCartService.refreshShopCarRedis();
         shopCartService.refreshRtailShopCarRedis();
        return BaseResponse.SUCCESSFUL();
    }

}
