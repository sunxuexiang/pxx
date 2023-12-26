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
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "CouponActivityProvider")
public interface CouponActivityProvider {

    /**
     * 创建活动
     * @param request 创建活动请求结构 {@link CouponActivityAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/add")
    BaseResponse<CouponActivityDetailResponse> add(@RequestBody @Valid CouponActivityAddRequest request);


    /**
     * 编辑活动
     * @param request 编辑活动请求结构 {@link CouponActivityModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/modify")
    BaseResponse modify(@RequestBody @Valid CouponActivityModifyRequest request);

    /**
     * 开始活动
     * @param request 开始活动请求结构 {@link CouponActivityStartByIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/start")
    BaseResponse start(@RequestBody @Valid CouponActivityStartByIdRequest request);

    /**
     * 暂停活动
     * @param request 暂停活动请求结构 {@link CouponActivityPauseByIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/pause")
    BaseResponse pause(@RequestBody @Valid CouponActivityPauseByIdRequest request);

    /**
     * 删除活动
     * @param request 删除活动请求结构 {@link CouponActivityDeleteByIdAndOperatorIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/delete-by-id-and-operator-id")
    BaseResponse deleteByIdAndOperatorId(@RequestBody @Valid CouponActivityDeleteByIdAndOperatorIdRequest request);

    /**
     * 领取一组优惠券 （注册活动或者进店活动）
     * 用户注册成功或者进店后，发放赠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-coupon-group")
    BaseResponse<GetRegisterOrStoreCouponResponse>  getCouponGroup(@RequestBody @Valid GetCouponGroupRequest request);

    /**
     * 领取一组优惠券 （指定优惠券活动）
     * 邀新注册奖励一组优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/send-coupon-group")
    BaseResponse<SendCouponResponse>  sendCouponGroup(@RequestBody @Valid SendCouponGroupRequest request);

    /**
     * 注册邀新-发送优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/add-coupon-group")
    BaseResponse addCouponGroup(@RequestBody @Valid CouponGroupAddRequest request);


    /**
     * 根据签到天数赠送优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/give-coupon-days")
    BaseResponse<SendCouponResponse> giveCouponDays(@RequestBody @Valid CouponActivitySignGiveRequest request);

    /**
     * 充值活动赠送优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/give-recharge-coupon")
    BaseResponse<SendCouponResponse> giveRechargeCoupon(@RequestBody @Valid SendCouponRechargeRequest request);

    /**
     * 领取一组优惠券
     * 购买指定商品赠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-buy-goods-send-coupon-group")
    BaseResponse<List<BuyGoodsOrFullOrderSendCouponResponse>> getBuyGoodsSendCouponGroup(@RequestBody @Valid BuyGoodsOrFullOrderSendCouponRequest request);

    /**
     * 领取一组优惠券
     * 订单满额赠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-full-order-coupon-group")
    BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> getFullOrderSendCouponGroup(@RequestBody @Valid BuyGoodsOrFullOrderSendCouponRequest request);

    /**
     * 领取一组优惠券
     * 久未下单
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/get-long-not-order-send-coupon-group")
    BaseResponse<List<LongNotOrderSendCouponGroupResponse>> getLongNotOrderSendCouponGroup(@RequestBody @Valid LongNotOrderSendCouponGroupRequest request);


    /**
     * 领取一组优惠券
     * 创建活动发送
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/activity/create-activity-send-now")
    BaseResponse createActivitySendNow(@RequestBody @Valid CouponActivityAddRequest request);

}
