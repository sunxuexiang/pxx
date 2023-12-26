package com.wanmi.sbc.returnorder.provider.impl.shopcart;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.returnorder.api.provider.shopcart.BulkShopCartProvider;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.returnorder.shopcart.BulkShopCartService;
import com.wanmi.sbc.returnorder.shopcart.request.BulkShopCartRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.*;
import com.wanmi.sbc.returnorder.api.response.purchase.*;
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
public class BulkShopCartController implements BulkShopCartProvider {

    @Autowired
    private BulkShopCartService bulkShopCartService;

    @Override
    public BaseResponse updateCheckStaues(CheckedBulkCartRequest request) {
        bulkShopCartService.updateDevanIsCheckAndCache(request, request.getGoodsInfos(), request.getType());
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse newsave(PurchaseSaveRequest request) {
        bulkShopCartService.saveAndCache(KsBeanUtil.convert(request, BulkShopCartRequest.class),request.getMarketingid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchSaveNewDevanning(PurchaseBatchSaveRequest request) {
        bulkShopCartService.cacheBatchSaveDevanning(KsBeanUtil.convert(request, BulkShopCartRequest.class));;
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 商品收藏调整商品数量请求结构 {@link PurchaseUpdateNumRequest}
     * @return
     */
    @Override
    public BaseResponse updateNum(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        bulkShopCartService.updateNum(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateDevanningNum(@Valid PurchaseUpdateNumRequest request) {
        bulkShopCartService.devanningNumMergin(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateDevanningNumNew(PurchaseUpdateNumRequest request) {
        bulkShopCartService.updateNumDevanningAndCache(KsBeanUtil.convert(request, BulkShopCartRequest.class),request.getMarketingid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateNumList(@Valid PurchaseUpdateNumRequest request) {
        bulkShopCartService.updateNumByIds(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 删除采购单请求结构 {@link PurchaseDeleteRequest}
     * @return
     */
    @Override
    public BaseResponse delete(@RequestBody @Valid PurchaseDeleteRequest request) {
        bulkShopCartService.delete(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse devanningDelete(@Valid PurchaseDeleteRequest request) {
        bulkShopCartService.devanningDelete(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse devanningDeleteCache(PurchaseDeleteRequest request) {
        bulkShopCartService.devanningDeleteCache(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 采购单商品移入收藏夹请求结构 {@link PurchaseAddFollowRequest}
     * @return
     */
    @Override
    public BaseResponse addFollow(@RequestBody @Valid PurchaseAddFollowRequest request) {
        bulkShopCartService.addFollow(KsBeanUtil.convert(request, BulkShopCartRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 清除失效商品请求结构 {@link PurchaseClearLoseGoodsRequest}
     * @return
     */
    @Override
    public BaseResponse clearLoseGoods(@RequestBody @Valid PurchaseClearLoseGoodsRequest request) {
        bulkShopCartService.clearLoseGoods(request.getUserId(), request.getDistributeChannel());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 计算采购单金额请求结构 {@link PurchaseCalcAmountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseListResponse> calcAmount(@RequestBody @Valid PurchaseCalcAmountRequest request) {
        PurchaseListResponse purchaseListResponse = bulkShopCartService.calcAmount(KsBeanUtil.convert(request.getPurchaseCalcAmount(), PurchaseListResponse.class),
                request.getCustomerVO(), request.getGoodsInfoIds());
        return BaseResponse.success(purchaseListResponse);
    }

    /**
     * @param request 计算采购单中参加同种营销的商品列表/总额/优惠请求结构 {@link PurchaseCalcMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@RequestBody @Valid PurchaseCalcMarketingRequest request) {
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = bulkShopCartService.calcMarketingByMarketingIdBase(
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
        PurchaseMarketingCalcResponse purchaseMarketingCalcResponse = bulkShopCartService
                .calcCouponActivityByActivityIdBase(request.getActivityId(),KsBeanUtil.convert(request.getCustomer(), CustomerVO.class),request.getWareId());
        return BaseResponse.success(KsBeanUtil.convert(purchaseMarketingCalcResponse, PurchaseCalcMarketingResponse.class));
    }

    /**
     * @param request 同步商品使用的营销请求结构 {@link PurchaseSyncGoodsMarketingsRequest}
     * @return
     */
    @Override
    public BaseResponse syncGoodsMarketings(@RequestBody @Valid PurchaseSyncGoodsMarketingsRequest request) {
        bulkShopCartService.syncGoodsMarketings(request.getGoodsMarketingMap(), request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 修改商品使用的营销请求结构 {@link PurchaseModifyGoodsMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse modifyGoodsMarketing(@RequestBody @Valid PurchaseModifyGoodsMarketingRequest request) {
        bulkShopCartService.modifyGoodsMarketing(request.getGoodsInfoId(), request.getMarketingId(), KsBeanUtil.convert(request.getCustomer(), CustomerVO.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 填充客户购买数请求结构 {@link PurchaseFillBuyCountRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseFillBuyCountResponse> fillBuyCount(@RequestBody @Valid PurchaseFillBuyCountRequest request) {
        List<GoodsInfoVO> goodsInfoList = bulkShopCartService.fillBuyCount(request.getGoodsInfoList(), request.getCustomerId(), request.getInviteeId());
        PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = new PurchaseFillBuyCountResponse();
        purchaseFillBuyCountResponse.setGoodsInfoList(goodsInfoList);
        return BaseResponse.success(purchaseFillBuyCountResponse);
    }

    @Override
    public BaseResponse<PurchaseMarketingResponse> getPurchasesMarketing(@RequestBody @Valid PurchaseMarketingRequest request){
        return BaseResponse.success(bulkShopCartService.getPurchasesMarketing(request.getGoodsInfoIds(),
                request.getGoodsInfos(), request.getCustomer(), request.getWareId()));
    }

    @Override
    public BaseResponse getSkuPurchaseNum(@RequestBody @Valid PurchaseQueryDTO request) {
        return BaseResponse.success(bulkShopCartService.getSkuPurchaseNum(request));
    }

}
