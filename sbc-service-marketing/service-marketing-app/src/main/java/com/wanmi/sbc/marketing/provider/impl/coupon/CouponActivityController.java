package com.wanmi.sbc.marketing.provider.impl.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.*;
import com.wanmi.sbc.marketing.coupon.service.CouponActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-24
 */
@Validated
@RestController
public class CouponActivityController implements CouponActivityProvider {

    @Autowired
    private CouponActivityService couponActivityService;


    /**
     * 创建活动
     *
     * @param request 创建活动请求结构 {@link CouponActivityAddRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponActivityDetailResponse> add(@RequestBody @Valid CouponActivityAddRequest request) {
        com.wanmi.sbc.marketing.coupon.response.CouponActivityDetailResponse response = couponActivityService.addCouponActivity(request);
        return BaseResponse.success(KsBeanUtil.convert(response,CouponActivityDetailResponse.class));
    }

    /**
     * 编辑活动
     *
     * @param request 编辑活动请求结构 {@link CouponActivityModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid CouponActivityModifyRequest request) {
        couponActivityService.modifyCouponActivity(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 开始活动请求结构
     *
     * @param request 开始活动请求结构 {@link CouponActivityStartByIdRequest}
     * @return
     */
    @Override
    public BaseResponse start(@RequestBody @Valid CouponActivityStartByIdRequest request) {
        couponActivityService.startActivity(request.getId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 暂停活动请求结构
     *
     * @param request 暂停活动请求结构 {@link CouponActivityPauseByIdRequest}
     * @return
     */
    @Override
    public BaseResponse pause(@RequestBody @Valid CouponActivityPauseByIdRequest request) {
        couponActivityService.pauseActivity(request.getId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除活动
     *
     * @param request 删除活动请求结构 {@link CouponActivityDeleteByIdAndOperatorIdRequest}
     * @return
     */
    @Override
    public BaseResponse deleteByIdAndOperatorId(@RequestBody @Valid CouponActivityDeleteByIdAndOperatorIdRequest request) {
        couponActivityService.deleteActivity(request.getId(), request.getOperatorId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 领取一组优惠券 （注册活动或者进店活动）
     * 用户注册成功或者进店后，发放赠券
     * @param request
     * @return
     */
    @Override
    public BaseResponse<GetRegisterOrStoreCouponResponse>  getCouponGroup(@RequestBody @Valid GetCouponGroupRequest request) {
        GetRegisterOrStoreCouponResponse response = couponActivityService.getCouponGroup(request.getCustomerId(),request.getType(),request.getStoreId());
        return BaseResponse.success(response);
    }


    /**
     * 领取一组优惠券 （指定优惠券活动）
     * 邀新注册奖励一组优惠券
     * @param request
     * @return
     */
    @Override
    public BaseResponse<SendCouponResponse>  sendCouponGroup(@RequestBody @Valid SendCouponGroupRequest request) {
        SendCouponResponse response = couponActivityService.sendCouponGroup(request);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse addCouponGroup(@RequestBody @Valid CouponGroupAddRequest request) {
        Boolean result = couponActivityService.sendCouponGroup(request);
        return BaseResponse.success(result);
    }


    /**
     * 根据签到天数赠送优惠券
     * @param request
     * @return
     */
    @Override
    public BaseResponse<SendCouponResponse> giveCouponDays(@RequestBody @Valid CouponActivitySignGiveRequest request) {
        SendCouponResponse sendCouponResponse = couponActivityService.giveCouponByDays(request);
        return BaseResponse.success(sendCouponResponse);
    }

    @Override
    public BaseResponse<SendCouponResponse> giveRechargeCoupon(SendCouponRechargeRequest request) {
        SendCouponResponse sendCouponResponse = couponActivityService.giveRechargeCoupon(request);
        return BaseResponse.success(sendCouponResponse);
    }

    /**
     * 领取一组优惠券
     * 购买指定商品赠券
     * @param request
     * @return
     */
    @Override
    public BaseResponse<List<BuyGoodsOrFullOrderSendCouponResponse>> getBuyGoodsSendCouponGroup(BuyGoodsOrFullOrderSendCouponRequest request) {
        List<BuyGoodsOrFullOrderSendCouponResponse> list = couponActivityService.getGoodsSendCouponGroup(request.getCustomerId(), request.getType(),
                request.getTradeItemInfoDTOS(), request.getStoreId());
        return BaseResponse.success(list);
    }

    /**
     * 领取一组优惠券
     * 订单满额赠券
     * @param request
     * @return
     */
    @Override
    public BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> getFullOrderSendCouponGroup(BuyGoodsOrFullOrderSendCouponRequest request) {
        BuyGoodsOrFullOrderSendCouponResponse response = couponActivityService.getOrderSendCouponGroup(request.getCustomerId(),request.getOrderPrice(),
                request.getType(),request.getStoreId());
        return BaseResponse.success(response);
    }

    /**
     * 领取一组优惠券（多活动满足同时送）
     * 久未下单
     * @param request
     * @return
     */
    @Override
    public BaseResponse<List<LongNotOrderSendCouponGroupResponse>> getLongNotOrderSendCouponGroup(@Valid LongNotOrderSendCouponGroupRequest request) {
        List<LongNotOrderSendCouponGroupResponse> longNotOrderSendCouponGroup = couponActivityService.getLongNotOrderSendCouponGroup(request.getCustomerId(), request.getActivitys());
        return BaseResponse.success(longNotOrderSendCouponGroup);
    }

    @Override
    public BaseResponse createActivitySendNow(@Valid CouponActivityAddRequest request) {
       return couponActivityService.createActivitySendNow(request);
    }
}
