package com.wanmi.sbc.marketing.api.provider.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-23
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "CouponActivityQueryProvider")
public interface CouponActivityQueryProvider {

    /**
     * 查询活动详情
     * @param request 查询活动详情请求结构 {@link CouponActivityGetDetailByIdAndStoreIdRequest}
     * @return {@link CouponActivityDetailResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-detail-by-id-and-store-id")
    BaseResponse<CouponActivityDetailResponse> getDetailByIdAndStoreId(@RequestBody @Valid CouponActivityGetDetailByIdAndStoreIdRequest request);

    /**
     * 通过主键获取优惠券活动
     * @param request 通过主键获取优惠券活动请求结构 {@link CouponActivityGetByIdRequest}
     * @return {@link CouponActivityGetByIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-by-id")
    BaseResponse<CouponActivityGetByIdResponse> getById(@RequestBody @Valid CouponActivityGetByIdRequest request);

    /**
     * 获取优惠券活动信息
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get")
    BaseResponse<CouponActivityResponse> get(@RequestBody @Valid CouponActivityGetRegisteredRequest request);

    /**
     * 查询活动列表
     * @param request 查询活动列表请求结构 {@link CouponActivityPageRequest}
     * @return {@link CouponActivityPageResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/page")
    BaseResponse<CouponActivityPageResponse> page(@RequestBody @Valid CouponActivityPageRequest request);


    /**
     * 获取目前最后一个开始的优惠券活动
     * @return {@link CouponActivityGetLastResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-last")
    BaseResponse<CouponActivityGetLastResponse> getLast();

    /**
     * 查询活动（注册赠券活动、进店赠券活动）不可用的时间范围
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/query-activity-enable-time")
    BaseResponse<CouponActivityDisabledTimeResponse> queryActivityEnableTime(@RequestBody @Valid CouponActivityDisabledTimeRequest request);

    /**
     * 查询分销邀新赠券活动
     * @return 活动ID
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-distribute-coupon-activity-id")
    BaseResponse getDistributeCouponActivity();

    /**
     * 查询优惠券信息、关联优惠券信息、优惠券信息
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-by-activity-id")
    BaseResponse<CouponActivityDetailByActivityIdResponse> getByActivityId(@RequestBody @Valid CouponActivityGetByActivityIdRequest request);

    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-by-activity-sign")
    BaseResponse<CouponActivityDetailByActivityTypeResponse> getByActivitySign();

    /**
     * 通过活动类型查询进行中&未开始的活动
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/list-by-activity-type")
    BaseResponse<List<CouponActivityResponse>> listByActivityType(@RequestBody @Valid CouponActivityPageRequest request);

    /**
     * 获取指定商品赠券活动关联商品信息
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/list-coupon-activity-goods")
    BaseResponse<CouponActivityGoodsResponse> listCouponActivityGoods(@RequestBody @Valid CouponActivityGoodsRequest request);

    /**
     * 校验是否满足购物车赠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/check-send-coupon")
    BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> checkSendCoupon(@RequestBody @Valid BuyGoodsOrFullOrderSendCouponRequest request);

}
