package com.wanmi.sbc.marketing.provider.impl.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCacheProvider;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.*;
import com.wanmi.sbc.marketing.bean.vo.CouponVO;
import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import com.wanmi.sbc.marketing.coupon.model.vo.CouponView;
import com.wanmi.sbc.marketing.coupon.request.CouponCacheCenterRequest;
import com.wanmi.sbc.marketing.coupon.response.*;
import com.wanmi.sbc.marketing.coupon.service.CouponCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-24 13:54
 */
@Validated
@RestController
public class CouponCacheQueryController implements CouponCacheProvider {

    @Autowired
    private CouponCacheService couponCacheService;


    /**
     * @param request 分页请求参数 {@link CouponCacheCenterPageRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponCacheCenterPageResponse> pageCouponStarted(@RequestBody @Valid CouponCacheCenterPageRequest request) {
        CouponCenterPageResponse couponStarted = couponCacheService.getCouponStarted(KsBeanUtil.convert(request,
                CouponCacheCenterRequest.class));
        MicroServicePage<CouponVO> page = KsBeanUtil.convertPage(couponStarted.getCouponViews(), CouponVO.class);
        CouponCacheCenterPageResponse response = KsBeanUtil.convert(couponStarted, CouponCacheCenterPageResponse.class);
        response.setCouponViews(page);
        return BaseResponse.success(response);
    }

    /**
     * @param request 商品列表参数 {@link CouponCacheListForGoodsListRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsList(@RequestBody @Valid CouponCacheListForGoodsListRequest request) {
        CouponListResponse couponListResponse = couponCacheService.listCouponForGoodsList(request.getGoodsInfoIds(),
                request.getCustomerId(), request.getStoreId());
        //优惠券是否可以领用状态，是否已领用状态排序
        List<CouponView> couponVOList = couponListResponse.getCouponViews().stream()
                .sorted(Comparator.comparing(CouponView::getHasFetched))
                .sorted(Comparator.comparing(CouponView::getLeftFlag).reversed())
                .collect(Collectors.toList());
        couponListResponse.setCouponViews(couponVOList);
        return BaseResponse.success(CouponCacheListForGoodsListResponse.builder()
                .couponViews(KsBeanUtil.copyListProperties(couponListResponse.getCouponViews(), CouponVO.class))
                .storeMap(couponListResponse.getStoreMap()).build());
    }

    @Override
    public BaseResponse<CouponCacheListForGoodsDetailResponse> listCouponForGoodsList(@RequestBody @Valid CouponCacheListForGoodsGoodInfoListRequest request) {

        List<CouponCache> caches = couponCacheService.listCouponForGoodsList(request.getGoodsInfoList(),
                request.getCustomer());

        CouponCacheResponse response = CouponCacheResponse.builder().couponCacheVOList(caches).build();

        return BaseResponse.success(KsBeanUtil.convert(response, CouponCacheListForGoodsDetailResponse.class));

    }

    /**
     * @param request 商品信息id {@link CouponCacheListForGoodsDetailRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsDetail(@RequestBody @Valid CouponCacheListForGoodsDetailRequest request) {
        CouponListResponse couponListResponse = couponCacheService.listCouponForGoodsDetail(request.getGoodsInfoId(),
                request.getCustomerId(), request.getStoreId());
        return BaseResponse.success(CouponCacheListForGoodsListResponse.builder()
                .couponViews(KsBeanUtil.copyListProperties(couponListResponse.getCouponViews(), CouponVO.class))
                .storeMap(couponListResponse.getStoreMap()).build());
    }

    /**
     * @param request 封装参数 {@link CouponGoodsListRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponGoodsListResponse> listGoodsByCouponId(@RequestBody @Valid CouponGoodsListRequest request) {
        CouponGoodsQueryResponse couponGoodsQueryResponse =
                couponCacheService.listGoodsByCouponId(request.getCouponId(), request.getActivityId(),
                        request.getCustomerId(), request.getStoreId());
        return BaseResponse.success(KsBeanUtil.convert(couponGoodsQueryResponse, CouponGoodsListResponse.class));
    }

    @Override
    public BaseResponse<CouponCacheCenterResponse> getCouponStartedById(@Valid CouponCacheCenterByIdsPageRequest request) {
        CouponCenterResponse couponStarted = couponCacheService.getCouponStartedById(request.getActivityIds(),request.getCustomerId());
        List<CouponVO> couponVOS = KsBeanUtil.convertList(couponStarted.getCouponViews(), CouponVO.class);
        CouponCacheCenterResponse response =new CouponCacheCenterResponse();
        response.setCouponViews(couponVOS);
        response.setStoreMap(couponStarted.getStoreMap());
        return BaseResponse.success(response);
    }
}
