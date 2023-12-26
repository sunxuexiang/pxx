package com.wanmi.sbc.marketing.provider.impl.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.*;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityConfigVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.marketing.bean.vo.CouponSignDaysVO;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivity;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import com.wanmi.sbc.marketing.coupon.model.root.CouponSignDays;
import com.wanmi.sbc.marketing.coupon.service.CouponActivityConfigService;
import com.wanmi.sbc.marketing.coupon.service.CouponActivityService;
import com.wanmi.sbc.marketing.coupon.service.CouponInfoService;
import com.wanmi.sbc.marketing.coupon.service.CouponSignDaysService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-24
 */
@Validated
@RestController
@Slf4j
public class CouponActivityQueryController implements CouponActivityQueryProvider {

    @Autowired
    private CouponActivityService couponActivityService;

    @Autowired
    private CouponActivityConfigService couponActivityConfigService;

    @Autowired
    private CouponInfoService couponInfoService;
    
    @Autowired
    private CouponSignDaysService couponSignDaysService;




    /**
     * 查询活动详情
     *
     * @param request 查询活动详情请求结构 {@link CouponActivityGetDetailByIdAndStoreIdRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponActivityDetailResponse> getDetailByIdAndStoreId(@RequestBody @Valid CouponActivityGetDetailByIdAndStoreIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(couponActivityService.getActivityDetail(request.getId(), request.getStoreId()),
                CouponActivityDetailResponse.class));
    }

    /**
     * 通过主键获取优惠券活动
     *
     * @param request 通过主键获取优惠券活动请求结构 {@link CouponActivityGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponActivityGetByIdResponse> getById(@RequestBody @Valid CouponActivityGetByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(couponActivityService.getCouponActivityByPk(request.getId()),
                CouponActivityGetByIdResponse.class));
    }

    /**
     * 通过活动类型获取进行中的优惠券活动
     * @return
     */
    @Override
    public BaseResponse<CouponActivityResponse> get(CouponActivityGetRegisteredRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(couponActivityService.getRegisteredCouponActivity(request.getActivityType()),
                CouponActivityResponse.class));
    }

    /**
     * 查询活动列表
     *
     * @param request 查询活动列表请求结构 {@link CouponActivityPageRequest}
     * @return
     */
    @Override
    public BaseResponse<CouponActivityPageResponse> page(@RequestBody @Valid CouponActivityPageRequest request) {
        Page<CouponActivity> couponActivities = couponActivityService.pageActivityInfo(KsBeanUtil.convert(request,
                CouponActivityPageRequest.class), request.getStoreId());
        log.info("优惠券活动列表返回信息Controller------------------>{}",couponActivities);
        return BaseResponse.success(new CouponActivityPageResponse(KsBeanUtil.convertPage(couponActivities, CouponActivityVO.class)));
    }

    /**
     * 获取目前最后一个开始的优惠券活动
     *
     * @return
     */
    @Override
    public BaseResponse<CouponActivityGetLastResponse> getLast() {
        return BaseResponse.success(KsBeanUtil.convert(couponActivityService.getLastActivity(), CouponActivityGetLastResponse.class));
    }

    /**
     * 查询活动（注册赠券活动、进店赠券活动）不可用的时间范围
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<CouponActivityDisabledTimeResponse> queryActivityEnableTime(@RequestBody @Valid CouponActivityDisabledTimeRequest request) {
        return BaseResponse.success(couponActivityService.queryActivityEnableTime(request));
    }

    /**
     * 查询分销邀新赠券活动
     * @return 活动ID
     */
    @Override
    public BaseResponse getDistributeCouponActivity(){
        return BaseResponse.success(couponActivityService.findDistributeCouponActivity().getActivityId());
    }

    @Override
    public BaseResponse<CouponActivityDetailByActivityIdResponse> getByActivityId(@RequestBody @Valid CouponActivityGetByActivityIdRequest request) {
        String activityId = request.getActivityId();
        CouponActivityVO couponActivityVO = null;
        List<CouponActivityConfigVO> couponActivityConfigList = null;
        List<CouponInfoVO> couponInfoVOList = null;
        // 1、查询活动基本信息
        CouponActivity couponActivity = couponActivityService.getCouponActivityByPk(activityId);
        if (Objects.nonNull(couponActivity)){
            couponActivityVO = KsBeanUtil.convert(couponActivity,CouponActivityVO.class);
        }
        //2、查询关联优惠券信息
        List<CouponActivityConfig> couponActivityConfigs = couponActivityConfigService.queryByActivityId(activityId);
        if (CollectionUtils.isNotEmpty(couponActivityConfigs)){
            couponActivityConfigList = KsBeanUtil.convert(couponActivityConfigs,CouponActivityConfigVO.class);
            //3、优惠券信息
            List<String> couponIds = couponActivityConfigs.stream().map(CouponActivityConfig::getCouponId).collect(Collectors.toList());
            List<CouponInfo> couponInfoList = couponInfoService.queryByIds(couponIds);
            if (CollectionUtils.isNotEmpty(couponInfoList)){
                couponInfoVOList = KsBeanUtil.convert(couponInfoList,CouponInfoVO.class);
            }
        }
        return BaseResponse.success(new CouponActivityDetailByActivityIdResponse(couponActivityVO,couponActivityConfigList,couponInfoVOList));
    }

    @Override
    public BaseResponse<CouponActivityDetailByActivityTypeResponse> getByActivitySign() {
        CouponActivityType activityType = CouponActivityType.SIGN_GIVE;
        CouponActivityVO couponActivityVO = null;
        List<CouponActivityConfigVO> couponActivityConfigList = null;
        List<CouponInfoVO> couponInfoVOList = null;
        List<CouponSignDaysVO> couponSignDaysList = null;
        CouponActivity activity = couponActivityService.getCouponActivityByActivityType(activityType);
        //赋值活动信息
        if(Objects.nonNull(activity)){
            couponActivityVO = KsBeanUtil.convert(activity,CouponActivityVO.class);
        }else {
            return BaseResponse.success(new CouponActivityDetailByActivityTypeResponse(couponActivityVO,couponActivityConfigList,couponInfoVOList,couponSignDaysList));
        }
        //2、查询关联优惠券信息
        List<CouponActivityConfig> couponActivityConfigs = couponActivityConfigService.queryByActivityId(couponActivityVO.getActivityId());
        if (CollectionUtils.isNotEmpty(couponActivityConfigs)){
            couponActivityConfigList = KsBeanUtil.convert(couponActivityConfigs,CouponActivityConfigVO.class);
            //3、优惠券信息
            List<String> couponIds = couponActivityConfigs.stream().map(CouponActivityConfig::getCouponId).collect(Collectors.toList());
            List<CouponInfo> couponInfoList = couponInfoService.queryByIds(couponIds);
            if (CollectionUtils.isNotEmpty(couponInfoList)){
                couponInfoVOList = KsBeanUtil.convert(couponInfoList,CouponInfoVO.class);
            }
        }
        //3.查询相关的签到配置信息
        List<CouponSignDays> couponSignDays = couponSignDaysService.queryByActivityId(couponActivityVO.getActivityId());
        if(CollectionUtils.isNotEmpty(couponSignDays)){
            couponSignDaysList = KsBeanUtil.convert(couponSignDays,CouponSignDaysVO.class);
        }
        return BaseResponse.success(new CouponActivityDetailByActivityTypeResponse(couponActivityVO,couponActivityConfigList,couponInfoVOList,couponSignDaysList));
    }

    @Override
    public BaseResponse<List<CouponActivityResponse>> listByActivityType(CouponActivityPageRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(couponActivityService.listByActivityType(request.getCouponActivityType(),request.getStoreId()),CouponActivityResponse.class));
    }

    @Override
    public BaseResponse<CouponActivityGoodsResponse> listCouponActivityGoods(CouponActivityGoodsRequest request) {
        return BaseResponse.success(CouponActivityGoodsResponse.builder()
                .couponActivityGoodsVOS(couponActivityService.listByActivityIds(request.getActivityIds()))
                .build());
    }

    @Override
    public BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> checkSendCoupon(BuyGoodsOrFullOrderSendCouponRequest request) {
        return BaseResponse.success(couponActivityService.checkGoodsSendCoupon(request.getCustomerId(),request.getType(), request.getTradeItemInfoDTOS(),request.getWareId()));
    }
}
