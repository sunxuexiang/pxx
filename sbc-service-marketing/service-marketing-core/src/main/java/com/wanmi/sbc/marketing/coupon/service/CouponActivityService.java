package com.wanmi.sbc.marketing.coupon.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerIdsListRequest;
import com.wanmi.sbc.customer.api.request.storelevel.CustomerLevelRequest;
import com.wanmi.sbc.customer.api.response.storelevel.CustomerLevelInfoResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.*;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.DistributionRewardCouponDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.enums.RangeDayType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import com.wanmi.sbc.marketing.coupon.model.root.*;
import com.wanmi.sbc.marketing.coupon.repository.*;
import com.wanmi.sbc.marketing.coupon.response.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.coupon.response.CouponActivityFullOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 11:43 AM 2018/9/12
 * @Description: 优惠券活动Service
 */
@Service
@Slf4j
public class CouponActivityService {

    @Autowired
    private CouponActivityRepository couponActivityRepository;

    @Autowired
    private CouponActivityConfigRepository couponActivityConfigRepository;

    @Autowired
    private CouponActivityGoodsRepository couponActivityGoodsRepository;

    @Autowired
    private CouponActivityOrderRepository couponActivityOrderRepository;

    @Autowired
    private CouponActivityLevelRepository couponActivityLevelRepository;

    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private StoreLevelQueryProvider storeLevelQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CouponCacheService couponCacheService;

    @Autowired
    private CouponActivityConfigService couponActivityConfigService;

    @Autowired
    private CouponCodeService couponCodeService;

    @Autowired
    private CouponMarketingCustomerScopeRepository couponMarketingCustomerScopeRepository;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CouponSignDaysRepository couponSignDaysRepository;

    @Autowired
    private CouponSignDaysService couponSignDaysService;


    @Autowired
    private SignReceiveCouponService signReceiveCouponService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;


    /**
     * 创建活动
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public CouponActivityDetailResponse addCouponActivity(CouponActivityAddRequest couponActivityAddRequest) {
        // 校验 活动结束时间必须大于已选优惠券结束时间
        List<CouponActivityConfig> couponActivityConfigs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(couponActivityAddRequest.getCouponActivityConfigs())) {
            if (CouponActivityFullType.FULL_COUNT.equals(couponActivityAddRequest.getCouponActivityFullType())
                    || CouponActivityFullType.FULL_AMOUNT.equals(couponActivityAddRequest.getCouponActivityFullType())) {
                List<CouponActivityConfigVO> couponActivityConfigVOS = new ArrayList<>();
                couponActivityAddRequest.getCouponActivityLevelVOS().forEach(level -> couponActivityConfigVOS.addAll(level.getCouponActivityConfigs()));
                couponActivityConfigs = KsBeanUtil.copyListProperties(couponActivityConfigVOS,CouponActivityConfig.class);
            } else {
                couponActivityConfigs = KsBeanUtil.copyListProperties(couponActivityAddRequest.getCouponActivityConfigs(), CouponActivityConfig.class);
            }

            List<String> errorIds = this.checkCoupon(couponActivityAddRequest.getEndTime(), couponActivityConfigs);
            if (!errorIds.isEmpty()) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
            }
        }else if(CollectionUtils.isNotEmpty(couponActivityAddRequest.getSignDaysSaveRequests()) && couponActivityAddRequest.getCouponActivityType().equals(CouponActivityType.SIGN_GIVE)){
            List<CouponActivitySignDaysSaveRequest> signDaysSaveRequests = couponActivityAddRequest.getSignDaysSaveRequests();
            for (CouponActivitySignDaysSaveRequest request: signDaysSaveRequests) {
                if(request.getConfigSaveRequest() != null){
                    CouponActivityConfig config = new CouponActivityConfig();
                    BeanUtils.copyProperties(request.getConfigSaveRequest(),config);
                    couponActivityConfigs.add(config);
                }
            }
            List<String> errorIds = this.checkCoupon(couponActivityAddRequest.getEndTime(), couponActivityConfigs);
            if (!errorIds.isEmpty()) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
            }
        } else if (CollectionUtils.isNotEmpty(couponActivityAddRequest.getCouponActivityLevelVOS())) {
            List<CouponActivityConfig> list = new ArrayList<>();
            couponActivityAddRequest.getCouponActivityLevelVOS().forEach(level ->
                    list.addAll(KsBeanUtil.convertList(level.getCouponActivityConfigs(),CouponActivityConfig.class)));
            List<String> errorIds = this.checkCoupon(couponActivityAddRequest.getEndTime(),list);
            if (!errorIds.isEmpty()) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
            }
        }

        //保存活动
        CouponActivity couponActivity = new CouponActivity();
        KsBeanUtil.copyPropertiesThird(couponActivityAddRequest, couponActivity);
        couponActivity.setCreateTime(LocalDateTime.now());
        couponActivity.setDelFlag(DeleteFlag.NO);
        if (couponActivityAddRequest.getPauseFlag() == null) {
            couponActivity.setPauseFlag(DefaultFlag.NO);
        }
        if (CouponActivityType.REGISTERED_COUPON == couponActivityAddRequest.getCouponActivityType()
                || CouponActivityType.STORE_COUPONS == couponActivityAddRequest.getCouponActivityType()
                || CouponActivityType.ENTERPRISE_REGISTERED_COUPON == couponActivityAddRequest.getCouponActivityType()
                || CouponActivityType.BUY_ASSIGN_GOODS_COUPON == couponActivityAddRequest.getCouponActivityType()) {
            couponActivity.setLeftGroupNum(couponActivity.getReceiveCount());
        }
        /* 后期有并发问题，可以对这部分内容加分布式锁   start*/
        // 校验 进店赠券活动、注册赠券活动、企业注册赠券活动，签到活动，购买指定商品赠券，同一时间段内只能有1个！
        if (this.checkActivity(couponActivityAddRequest.getStartTime(),
                couponActivityAddRequest.getEndTime(), couponActivityAddRequest.getCouponActivityType(),
                couponActivityAddRequest.getStoreId(), null)) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_TIME_CHANGE);
        }
        if(Objects.isNull(couponActivityAddRequest.getSendType())){
            couponActivity.setSendType(0);// 0是普通券
        } else {
            couponActivity.setSendType(couponActivityAddRequest.getSendType());// 0是普通券
        }
        couponActivity = couponActivityRepository.saveAndFlush(couponActivity);
        String activityId = couponActivity.getActivityId();

        //如果是赠送签到优惠券,需要保存签到天数信息,并且保存关联的优惠券信息,关联的优惠券信息可以没有,签到信息不能没有
        if(couponActivityAddRequest.getCouponActivityType().equals(CouponActivityType.SIGN_GIVE)){
            couponActivityConfigs = new ArrayList<>();
            List<CouponActivitySignDaysSaveRequest> signDaysSaveRequests = couponActivityAddRequest.getSignDaysSaveRequests();
            if(CollectionUtils.isEmpty(signDaysSaveRequests)){
                throw new SbcRuntimeException("选择签到天数", CouponErrorCode.ACTIVITY_GOING);
            }
            Integer size = signDaysSaveRequests.size();
            if(!size.equals(7)){
                throw new SbcRuntimeException("签到天数需选择七天", CouponErrorCode.ACTIVITY_GOING);
            }
            for (CouponActivitySignDaysSaveRequest request: signDaysSaveRequests) {
                CouponSignDays days = new CouponSignDays();
                //签到天数
                days.setSignDays(request.getSignDays());
                days.setActivityId(couponActivity.getActivityId());
                days = couponSignDaysRepository.save(days);
                CouponActivityConfigSaveRequest configSaveRequest = request.getConfigSaveRequest();
                //当配置信息不为空的时候,根据签到天数id保存配置信息
                if(configSaveRequest != null){
                    CouponActivityConfig couponActivityConfig = new CouponActivityConfig();
                    couponActivityConfig.setCouponId(configSaveRequest.getCouponId());
                    couponActivityConfig.setTotalCount(configSaveRequest.getTotalCount());
                    couponActivityConfig.setActivityId(couponActivity.getActivityId());
                    couponActivityConfig.setCouponSignDaysId(days.getCouponSignDaysId());
                    couponActivityConfig.setHasLeft(DefaultFlag.YES);
                    couponActivityConfig = couponActivityConfigRepository.save(couponActivityConfig);
                    couponActivityConfigs.add(couponActivityConfig);
                }
            }
        //如果是订单满额赠券，需要配置订单满额信息（级联配置 1组活动->多组订单满额配置，1组订单满额配置->多组优惠券信息）
        }else if (CouponActivityType.FULL_ORDER.equals(couponActivityAddRequest.getCouponActivityType())){
            //订单满额优惠券配置信息
            List<CouponActivityFullOrderRequest> couponActivityFullOrders = couponActivityAddRequest.getCouponActivityFullOrders();
            couponActivityFullOrders.forEach(item ->{
                //保存订单满额配置信息
                CouponActivityOrder couponActivityOrder = new CouponActivityOrder();
                couponActivityOrder.setActivityId(activityId);
                couponActivityOrder.setFullOrderPrice(item.getFullOrderPrice());
                couponActivityOrder.setLeftGroupNum(item.getLeftGroupNum());
                CouponActivityOrder orderResult = couponActivityOrderRepository.saveAndFlush(couponActivityOrder);

                //保存优惠券配置信息
                List<CouponActivityConfig> configList = new ArrayList<>();
                configList.addAll(KsBeanUtil.convertList(item.getCouponActivityConfigs(),CouponActivityConfig.class));
                // 校验 活动结束时间必须大于已选优惠券结束时间
                List<String> errorIds = this.checkCoupon(couponActivityAddRequest.getEndTime(), configList);
                if (!errorIds.isEmpty()) {
                    throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
                }
                configList.forEach(i->{
                    i.setActivityId(activityId);
                    i.setCouponActivityOrderId(orderResult.getCouponActivityOrderId());
                    i.setHasLeft(DefaultFlag.YES);
                });
                couponActivityConfigRepository.saveAll(configList);
            });
        }else if (CouponActivityType.BUY_ASSIGN_GOODS_COUPON.equals(couponActivityAddRequest.getCouponActivityType())){
            this.addCouponActivityOtherInfos(couponActivityAddRequest,couponActivity);
        }else{
            //保存活动关联的优惠券
            this.saveCouponActivityConfig(couponActivityConfigs,couponActivity);
        }

        //保存活动关联的目标客户作用范围
        List<CouponMarketingCustomerScope> couponMarketingCustomerScope = saveMarketingCustomerScope
                (couponActivityAddRequest.getCustomerScopeIds(),
                        couponActivity);

        CouponActivityDetailResponse response = new CouponActivityDetailResponse();
        response.setCouponActivity(couponActivity);
        response.setCouponActivityConfigList(couponActivityConfigs);
        response.setCouponMarketingCustomerScope(couponMarketingCustomerScope);
        //刷新优惠券缓存
        if (couponActivity.getCouponActivityType().equals(CouponActivityType.ALL_COUPONS)){
            couponCacheService.refreshCachePart(Arrays.asList(couponActivity.getActivityId()));
        }
        return response;
    }

    /**
     * 新增购买指定商品赠券 保存相关设置信息
     * @param request
     * @param couponActivity
     */
    private void addCouponActivityOtherInfos(CouponActivityAddRequest request,CouponActivity couponActivity){
        if (Objects.nonNull(request.getCouponActivityFullType())
                && (request.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)
                || request.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT))) {
            //保存购买指定商品赠券配置信息
            this.saveCouponActivityGoods(request.getGoodsInfoIds(),couponActivity.getActivityId());
            //保存活动关联优惠券等级促销配置信息
            this.saveCouponActivityLevel(KsBeanUtil.convert(request.getCouponActivityLevelVOS(),CouponActivityLevel.class),couponActivity);
        } else {
            //保存购买指定商品赠券配置信息
            this.saveCouponActivityGoods(request.getGoodsInfoIds(),couponActivity.getActivityId());
            //保存活动关联优惠券配置信息
            this.saveCouponActivityConfig(KsBeanUtil.convert(request.getCouponActivityConfigs(),CouponActivityConfig.class),couponActivity);
        }
    }

    /**
     * 修改购买指定商品赠券 保存相关设置信息
     * @param request
     * @param couponActivity
     */
    private void modiyCouponActivityOtherInfos(CouponActivityModifyRequest request,CouponActivity couponActivity){
        if (Objects.nonNull(request.getCouponActivityFullType())
                && (request.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)
                || request.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT))) {
            //删除优惠券活动关联促销等级信息
            couponActivityLevelRepository.deleteByActivityId(couponActivity.getActivityId());
            //保存购买指定商品赠券配置信息
            this.saveCouponActivityGoods(request.getGoodsInfoIds(),couponActivity.getActivityId());
            //保存活动关联优惠券等级促销配置信息
            this.saveCouponActivityLevel(KsBeanUtil.convert(request.getCouponActivityLevelVOS(),CouponActivityLevel.class),couponActivity);
        } else {
            //保存购买指定商品赠券配置信息
            this.saveCouponActivityGoods(request.getGoodsInfoIds(),couponActivity.getActivityId());
            //保存活动关联优惠券配置信息
            this.saveCouponActivityConfig(KsBeanUtil.convert(request.getCouponActivityConfigs(),CouponActivityConfig.class),couponActivity);
        }
    }

    /**
     * 编辑活动
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void modifyCouponActivity(CouponActivityModifyRequest couponActivityModifyRequest) {
        // 校验，如果活动未开始才可以编辑
        CouponActivity couponActivity = couponActivityRepository.findById(couponActivityModifyRequest.getActivityId()).get();
        if (!couponActivityModifyRequest.getCouponActivityType().equals(CouponActivityType.POINTS_COUPON)) {
            if (couponActivity.getStartTime() != null && couponActivity.getStartTime().isBefore(LocalDateTime.now())) {
                // 活动已经开始，不可以编辑，删除
                throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_GOING);
            }
        }

        //如果是签到规则,判断是否时间重复
        if(couponActivityModifyRequest.getCouponActivityType().equals(CouponActivityType.SIGN_GIVE)){
            LocalDateTime startTime = couponActivityModifyRequest.getStartTime();
            LocalDateTime endTime = couponActivityModifyRequest.getEndTime();
            List<CouponActivity> couponActivities = couponActivityRepository.queryActivityByStartTime(startTime, endTime, CouponActivityType.POINTS_COUPON);
            if(couponActivities != null && couponActivities.size()>0){
                throw new SbcRuntimeException("以有开始的签到活动", CouponErrorCode.ACTIVITY_GOING);

            }
            List<CouponActivity> couponActivities1 = couponActivityRepository.queryActivityByEndTime(startTime, endTime, CouponActivityType.POINTS_COUPON);
            if(couponActivities1 != null && couponActivities1.size()>0){
                throw new SbcRuntimeException("以有开始的签到活动", CouponErrorCode.ACTIVITY_GOING);
            }
        }

        List<CouponActivityConfig> couponActivityConfigs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(couponActivityModifyRequest.getCouponActivityConfigs())) {
            if (CouponActivityFullType.FULL_COUNT.equals(couponActivityModifyRequest.getCouponActivityFullType())
                    || CouponActivityFullType.FULL_AMOUNT.equals(couponActivityModifyRequest.getCouponActivityFullType())){
                List<CouponActivityConfigVO> couponActivityConfigVOS = new ArrayList<>();
                couponActivityModifyRequest.getCouponActivityLevelVOS().forEach(level -> couponActivityConfigVOS.addAll(level.getCouponActivityConfigs()));
                couponActivityConfigs = KsBeanUtil.copyListProperties(couponActivityConfigVOS,CouponActivityConfig.class);
            } else {
                couponActivityConfigs = KsBeanUtil.copyListProperties(couponActivityModifyRequest.getCouponActivityConfigs(), CouponActivityConfig.class);
            }

            // 校验 活动结束时间必须大于已选优惠券结束时间
            List<String> errorIds = this.checkCoupon(couponActivityModifyRequest.getEndTime(), couponActivityConfigs);
            if (!errorIds.isEmpty()) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
            }
        }else if(CollectionUtils.isNotEmpty(couponActivityModifyRequest.getSignDaysSaveRequests()) && couponActivityModifyRequest.getCouponActivityType().equals(CouponActivityType.SIGN_GIVE)){
            List<CouponActivitySignDaysSaveRequest> signDaysSaveRequests = couponActivityModifyRequest.getSignDaysSaveRequests();
            for (CouponActivitySignDaysSaveRequest request: signDaysSaveRequests) {
                if(request.getConfigSaveRequest() != null){
                    CouponActivityConfig config = new CouponActivityConfig();
                    BeanUtils.copyProperties(request.getConfigSaveRequest(),config);
                    couponActivityConfigs.add(config);
                }
            }
            List<String> errorIds = this.checkCoupon(couponActivityModifyRequest.getEndTime(), couponActivityConfigs);
            if (!errorIds.isEmpty()) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
            }
            //删除全部关联的天数信息
            couponSignDaysRepository.deleteByActivityId(couponActivityModifyRequest.getActivityId());
        }

        //全部删除活动关联的优惠券
        couponActivityConfigRepository.deleteByActivityId(couponActivityModifyRequest.getActivityId());
        //保存活动
        KsBeanUtil.copyProperties(couponActivityModifyRequest, couponActivity);
        couponActivity.setUpdateTime(LocalDateTime.now());
        couponActivityRepository.save(couponActivity);
        if (CouponActivityType.REGISTERED_COUPON == couponActivityModifyRequest.getCouponActivityType()
                || CouponActivityType.STORE_COUPONS == couponActivityModifyRequest.getCouponActivityType()
                || CouponActivityType.BUY_ASSIGN_GOODS_COUPON == couponActivityModifyRequest.getCouponActivityType()) {
            couponActivity.setLeftGroupNum(couponActivity.getReceiveCount());
        }
        /* 后期有并发问题，可以对这部分内容加分布式锁   start*/
        // 校验 进店赠券活动、注册赠券活动，同一时间段内只能各有1个！
        if (this.checkActivity(couponActivityModifyRequest.getStartTime(),
                couponActivityModifyRequest.getEndTime(), couponActivityModifyRequest.getCouponActivityType(),
                couponActivityModifyRequest.getStoreId(), couponActivityModifyRequest.getActivityId())) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_TIME_CHANGE);
        }
        //保存活动
        couponActivityRepository.save(couponActivity);
        String activityId = couponActivity.getActivityId();
        /* 后期有并发问题，可以对这部分内容加分布式锁   end*/
        //全部删除活动关联的优惠券
        couponActivityConfigRepository.deleteByActivityId(couponActivityModifyRequest.getActivityId());
        //删除活动关联商品信息
        couponActivityGoodsRepository.deleteByActivityId(couponActivity.getActivityId());

        //如果是赠送签到优惠券,需要保存签到天数信息,并且保存关联的优惠券信息,关联的优惠券信息可以没有,签到信息不能没有
        if(couponActivityModifyRequest.getCouponActivityType().equals(CouponActivityType.SIGN_GIVE)){
            couponActivityConfigs = new ArrayList<>();
            List<CouponActivitySignDaysSaveRequest> signDaysSaveRequests = couponActivityModifyRequest.getSignDaysSaveRequests();
            if(CollectionUtils.isEmpty(signDaysSaveRequests)){
                throw new SbcRuntimeException("选择签到天数", CouponErrorCode.ACTIVITY_GOING);
            }
            Integer size = signDaysSaveRequests.size();
            if(!size.equals(7)){
                throw new SbcRuntimeException("签到天数需选择七天", CouponErrorCode.ACTIVITY_GOING);
            }
            for (CouponActivitySignDaysSaveRequest request: signDaysSaveRequests) {
                CouponSignDays days = new CouponSignDays();
                //签到天数
                days.setSignDays(request.getSignDays());
                days.setActivityId(couponActivity.getActivityId());
                days = couponSignDaysRepository.save(days);
                CouponActivityConfigSaveRequest configSaveRequest = request.getConfigSaveRequest();
                //当配置信息不为空的时候,根据签到天数id保存配置信息
                if(configSaveRequest != null){
                    CouponActivityConfig couponActivityConfig = new CouponActivityConfig();
                    couponActivityConfig.setCouponId(configSaveRequest.getCouponId());
                    couponActivityConfig.setTotalCount(configSaveRequest.getTotalCount());
                    couponActivityConfig.setActivityId(couponActivity.getActivityId());
                    couponActivityConfig.setCouponSignDaysId(days.getCouponSignDaysId());
                    couponActivityConfig.setHasLeft(DefaultFlag.YES);
                    couponActivityConfig = couponActivityConfigRepository.save(couponActivityConfig);
                    couponActivityConfigs.add(couponActivityConfig);
                }
            }
        }else if(CouponActivityType.BUY_ASSIGN_GOODS_COUPON.equals(couponActivityModifyRequest.getCouponActivityType())){
            this.modiyCouponActivityOtherInfos(couponActivityModifyRequest,couponActivity);
        }else if(CouponActivityType.FULL_ORDER.equals(couponActivityModifyRequest.getCouponActivityType())){
            //删除活动关联订单满额配置信息
            couponActivityOrderRepository.deleteByActivityId(activityId);
            //订单满额优惠券配置信息
            List<CouponActivityFullOrderRequest> couponActivityFullOrders = couponActivityModifyRequest.getCouponActivityFullOrders();
            couponActivityFullOrders.forEach(item ->{
                //保存订单满额配置信息
                CouponActivityOrder couponActivityOrder = new CouponActivityOrder();
                couponActivityOrder.setActivityId(activityId);
                couponActivityOrder.setFullOrderPrice(item.getFullOrderPrice());
                couponActivityOrder.setLeftGroupNum(item.getLeftGroupNum());
                CouponActivityOrder orderResult = couponActivityOrderRepository.saveAndFlush(couponActivityOrder);

                //保存优惠券配置信息
                List<CouponActivityConfig> configList = new ArrayList<>();
                configList.addAll(KsBeanUtil.convertList(item.getCouponActivityConfigs(),CouponActivityConfig.class));
                // 校验 活动结束时间必须大于已选优惠券结束时间
                List<String> errorIds = this.checkCoupon(couponActivityModifyRequest.getEndTime(), configList);
                if (!errorIds.isEmpty()) {
                    throw new SbcRuntimeException(errorIds, CouponErrorCode.ACTIVITY_ERROR_COUPON);
                }
                configList.forEach(i->{
                    i.setActivityId(activityId);
                    i.setCouponActivityOrderId(orderResult.getCouponActivityOrderId());
                    i.setHasLeft(DefaultFlag.YES);
                });
                couponActivityConfigRepository.saveAll(configList);
            });
        }else{
            //保存活动关联的优惠券
            this.saveCouponActivityConfig(couponActivityConfigs,couponActivity);
//            if (CollectionUtils.isNotEmpty(couponActivityConfigs)) {
//                for (CouponActivityConfig item : couponActivityConfigs) {
//                    item.setActivityId(couponActivity.getActivityId());
//                }
//                couponActivityConfigs.forEach(item -> item.setHasLeft(DefaultFlag.YES));
//                couponActivityConfigRepository.saveAll(couponActivityConfigs);
//            }
        }

        //保存活动关联的目标客户作用范围
        saveMarketingCustomerScope(couponActivityModifyRequest.getCustomerScopeIds(), couponActivity);
        //刷新优惠券缓存
        if (couponActivity.getCouponActivityType().equals(CouponActivityType.ALL_COUPONS)){
            couponCacheService.refreshCachePart(Arrays.asList(couponActivity.getActivityId()));
        }
    }

    /**
     * 保存优惠券活动关联等级促销及优惠券信息
     * @param couponActivityLevels
     * @param couponActivity
     */
    private void saveCouponActivityLevel(List<CouponActivityLevel> couponActivityLevels,CouponActivity couponActivity){
        List<CouponActivityConfig> couponActivityConfigs = new ArrayList<>();
        couponActivityLevels.forEach(level -> level.setActivityId(couponActivity.getActivityId()));
        //保存优惠券活动关联等级促销信息
        couponActivityLevels = couponActivityLevelRepository.saveAll(couponActivityLevels);
        couponActivityLevels.forEach(level -> {
            level.getCouponActivityConfigs().forEach(config -> {
                config.setActivityLevelId(level.getActivityLevelId());
                config.setActivityId(couponActivity.getActivityId());
            });
            couponActivityConfigs.addAll(KsBeanUtil.convert(level.getCouponActivityConfigs(),CouponActivityConfig.class));
        });

        //保存活动关联优惠券配置信息
        this.saveCouponActivityConfig(couponActivityConfigs,couponActivity);
    }

    /**
     * 保存活动关联商品信息
     * @param skuIds
     * @param activityId
     */
    private void saveCouponActivityGoods(List<String> skuIds,String activityId) {
        List<CouponActivityGoods> couponActivityGoodsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(skuIds)) {
            skuIds.forEach(skuId -> {
                //保存活动关联商品信息
                couponActivityGoodsList.add(CouponActivityGoods.builder()
                        .activityId(activityId).goodsInfoId(skuId).build());
            });
        } else {
            throw new SbcRuntimeException("活动关联商品信息不能为空",CouponErrorCode.ACTIVITY_GOODS_ERROR);
        }
        couponActivityGoodsRepository.saveAll(couponActivityGoodsList);
    }

    /**
     * 保存活动关联的优惠券
     * @param couponActivityConfigs
     * @param couponActivity
     */
    private void saveCouponActivityConfig(List<CouponActivityConfig> couponActivityConfigs,CouponActivity couponActivity){
        //保存活动关联的优惠券
        if (CollectionUtils.isNotEmpty(couponActivityConfigs)) {
            for (CouponActivityConfig item : couponActivityConfigs) {
                item.setActivityId(couponActivity.getActivityId());
            }
            couponActivityConfigs.forEach(item -> item.setHasLeft(DefaultFlag.YES));
            couponActivityConfigRepository.saveAll(couponActivityConfigs);
        }
    }

    /**
     * 开始活动
     * @param id
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void startActivity(String id) {
        //当活动暂停时才可以开始
        CouponActivity couponActivity = couponActivityRepository.findById(id).get();
        if (CouponActivityType.SPECIFY_COUPON == couponActivity.getCouponActivityType()) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (couponActivity.getStartTime().isAfter(LocalDateTime.now()) && couponActivity.getEndTime().isBefore
                (LocalDateTime.now())) {
            //非进行中的活动
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_START);
        }
        couponActivityRepository.startActivity(id);
        couponCacheService.refreshCachePart(Arrays.asList(couponActivity.getActivityId()));
    }

    /**
     * 暂停活动
     * @param id
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void pauseActivity(String id) {
        // 只有进行中的活动才可以暂停
        CouponActivity couponActivity = couponActivityRepository.findById(id).get();
        if (CouponActivityType.SPECIFY_COUPON == couponActivity.getCouponActivityType()) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (couponActivity.getStartTime().isAfter(LocalDateTime.now())) {
            //活动还未开始不能暂停
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_START);
        }
        if (couponActivity.getEndTime().isBefore(LocalDateTime.now())) {
            //活动已经结束
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_FINISH);
        }
        couponActivityRepository.pauseActivity(id,LocalDateTime.now());
        couponCacheService.refreshCachePart(Arrays.asList(couponActivity.getActivityId()));
    }

    /**
     * 删除活动
     * @param id
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(String id, String operatorId) {
        //只有未开始的活动才可以删除
        CouponActivity couponActivity = couponActivityRepository.findById(id).get();
        if (couponActivity.getStartTime().isBefore(LocalDateTime.now())) {
            //活动已开始不可以删除
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_GOING);
        }
        couponActivityRepository.deleteActivity(id, operatorId);
        couponActivityConfigRepository.deleteByActivityId(id);
        if (CouponActivityType.BUY_ASSIGN_GOODS_COUPON == couponActivity.getCouponActivityType()){
            couponActivityGoodsRepository.deleteByActivityId(id);
        }else if (CouponActivityType.FULL_ORDER == couponActivity.getCouponActivityType()){
            couponActivityOrderRepository.deleteByActivityId(id);
        }
        //删除未开始并缓存了的优惠券活动信息
        List<CouponCache> couponCacheList = couponCacheService.getCouponCacheByCouponActivityId(id);
        if (CollectionUtils.isNotEmpty(couponCacheList)){
            couponCacheService.deleteByCouponActivityId(id);
        }
    }

    /**
     * 查询活动详情
     * @param id
     */
    public CouponActivityDetailResponse getActivityDetail(String id, Long storeId) {
        // 1、查询活动基本信息
        Optional<CouponActivity> couponActivityOptional = couponActivityRepository.findById(id);
        if (!couponActivityOptional.isPresent()) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_EXIST);
        }
        CouponActivity couponActivity = couponActivityOptional.get();
        log.info("优惠卷活动返回信息------------->{}",couponActivity);
        CouponActivityDetailResponse response = new CouponActivityDetailResponse();
        //  2、查询关联优惠券信息
        List<CouponActivityConfig> couponActivityConfigs = couponActivityConfigRepository.findByActivityId(id);
        List<String> ids = new ArrayList<>();
        couponActivityConfigs.forEach(item -> ids.add(item.getCouponId()));
        List<CouponInfo> couponInfos = couponInfoRepository.queryByIds(ids);
        //  3、查询客户等级信息
        List<CustomerLevelVO> customerLevels = customerLevels(couponActivity);

        //4.查询指定用户
        List<CouponMarketingCustomerScope> couponMarketingCustomerScope = couponMarketingCustomerScopeRepository
                .findByActivityId(couponActivity.getActivityId());
        if (CollectionUtils.isNotEmpty(couponMarketingCustomerScope)) {
            List<CustomerVO> detailResponseList = getCouponMarketingCustomers(couponMarketingCustomerScope,
                    couponActivity);
            response.setCustomerDetailVOS(detailResponseList);
        }

        //查询指定商品赠券商品信息
        if (couponActivity.getCouponActivityType().equals(CouponActivityType.BUY_ASSIGN_GOODS_COUPON)) {
            List<String> skuIds = couponActivityGoodsRepository.findGoodsInfoIdsByActivityId(couponActivity.getActivityId());
            BaseResponse<GoodsInfoViewByIdsResponse> goodsInfo = goodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder().goodsInfoIds(skuIds).build());
            response.setGoodsInfoVOS(goodsInfo.getContext().getGoodsInfos());
            if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)
                    || couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                List<CouponActivityLevel> couponActivityLevels = couponActivityLevelRepository.findByActivityId(couponActivity.getActivityId());
                couponActivityLevels.forEach(level -> {
                    List<CouponActivityConfig> configs = couponActivityConfigRepository.findByActivityIdAndActivityLevelId(couponActivity.getActivityId(), level.getActivityLevelId());
                    level.setCouponActivityConfigs(KsBeanUtil.convert(configs, CouponActivityConfigVO.class));
                });
                response.setCouponActivityLevelVOS(KsBeanUtil.convert(couponActivityLevels, CouponActivityLevelVO.class));
            }
        }else if(couponActivity.getCouponActivityType().equals(CouponActivityType.FULL_ORDER)){
            List<CouponActivityFullOrderResponse> couponActivityFullOrders = new ArrayList<>();
            //查询订单满额配置信息
            List<CouponActivityOrder> CouponActivityOrderList = couponActivityOrderRepository.findCouponActivityOrdersByActivityId(couponActivity.getActivityId());
            if(CollectionUtils.isNotEmpty(CouponActivityOrderList)){
                couponActivityFullOrders = KsBeanUtil.convertList(CouponActivityOrderList, CouponActivityFullOrderResponse.class);
                couponActivityFullOrders.forEach(item -> {
                    //获取所有订单满额优惠券配置信息并返回
                    List<CouponActivityConfig> configList = couponActivityConfigRepository.findByCouponActivityOrderId(item.getCouponActivityOrderId());
                    item.setCouponActivityConfigs(configList);

                    //获取所有优惠券信息并返回
                    List<String> couponIds = new ArrayList<>();
                    configList.forEach(i -> {couponIds.add(i.getCouponId());});
                    List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponIds);
                    item.setCouponInfoList(couponInfoList);
                });
            }
            response.setCouponActivityFullOrders(couponActivityFullOrders);
        }

        response.setCouponActivity(couponActivity);
        log.info("优惠卷活动返回信息------------->{}",couponActivity);
        log.info("实际返回信息------------->{}",response);
        //订单满额赠送联级查询不需要返回以下信息
        if(!CouponActivityType.FULL_ORDER.equals(couponActivity.getCouponActivityType())){
            response.setCouponMarketingCustomerScope(couponMarketingCustomerScope);
            response.setCouponActivityConfigList(couponActivityConfigs);
            response.setCouponInfoList(couponInfos);
            response.setCustomerLevelList(customerLevels);
        }
        return response;
    }

    /**
     * 通过主键获取优惠券活动
     * @param id
     * @return
     */
    public CouponActivity getCouponActivityByPk(String id) {
        return couponActivityRepository.findById(id).orElse(null);
    }

    public CouponActivity getCouponActivityByActivityType(CouponActivityType activityType){
        //当前时间
        LocalDateTime now = LocalDateTime.now();
        List<CouponActivity> couponActivityByActivityType = couponActivityRepository.getCouponActivityByActivityType(activityType, now);
        return CollectionUtils.isNotEmpty(couponActivityByActivityType) ? couponActivityByActivityType.get(0) : null;
    }

    /**
     * 通过活动类型获取优惠券活动
     * @param activityType
     * @return
     */
    public CouponActivity getRegisteredCouponActivity(CouponActivityType activityType){
        LocalDateTime now = LocalDateTime.now();
        List<CouponActivity> couponActivities = couponActivityRepository.getRegisteredCouponActivity(activityType,now);
        return couponActivities.stream().findFirst().orElse(null);
    }

    /**
     * 查询活动列表
     * @param request
     */
    public Page<CouponActivity> pageActivityInfo(CouponActivityPageRequest request, Long storeId) {

        //查询列表
        String sql = "SELECT t.* FROM coupon_activity t ";
        //条件查询
        StringBuilder whereSql = new StringBuilder("WHERE t.del_flag = 0");

        if (storeId != null) {
        	whereSql.append(" AND t.platform_flag = 0");
        	// -1为查询所有商家
        	if (storeId != -1) {
        		whereSql.append(" AND t.store_id = " + storeId);
			}
        } else {
            whereSql.append(" AND t.platform_flag = 1");
        }
        //活动名称查找
        if (StringUtils.isNotBlank(request.getActivityName())) {
            whereSql.append(" AND t.activity_name LIKE '%" + request.getActivityName() + "%'");
        }

        //仓库id筛选
        if (Objects.nonNull(request.getWareId())) {
            whereSql.append(" AND t.ware_id = " + request.getWareId());
        }

        //赠送类型
        if (Objects.nonNull(request.getSendType())) {
            whereSql.append(" AND t.send_type = " + request.getSendType());
        }

        //活动类型筛选
        if (CouponActivityType.ALL_COUPONS.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.ALL_COUPONS.toValue());
        } else if (CouponActivityType.SPECIFY_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.SPECIFY_COUPON.toValue());
        } else if (CouponActivityType.STORE_COUPONS.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.STORE_COUPONS.toValue());
        } else if (CouponActivityType.REGISTERED_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.REGISTERED_COUPON.toValue());
        } else if (CouponActivityType.RIGHTS_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.RIGHTS_COUPON.toValue());
        } else if (CouponActivityType.DISTRIBUTE_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.DISTRIBUTE_COUPON.toValue());
        } else if (CouponActivityType.POINTS_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.POINTS_COUPON.toValue());
        } else if (CouponActivityType.ENTERPRISE_REGISTERED_COUPON.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " + CouponActivityType.ENTERPRISE_REGISTERED_COUPON.toValue());
        } else if (CouponActivityType.SIGN_GIVE.equals(request.getCouponActivityType())) {
            whereSql.append(" AND t.activity_type = " +CouponActivityType.SIGN_GIVE.toValue());
        } else if (CouponActivityType.BUY_ASSIGN_GOODS_COUPON.equals(request.getCouponActivityType())){
            whereSql.append(" AND t.activity_type = " +CouponActivityType.BUY_ASSIGN_GOODS_COUPON.toValue());
        }else if (CouponActivityType.LONG_NOT_ORDER.equals(request.getCouponActivityType())){
            whereSql.append(" AND t.activity_type = " +CouponActivityType.LONG_NOT_ORDER.toValue());
        }

        //时间筛选
        if (null != request.getStartTime()) {
            whereSql.append(" AND '" + DateUtil.format(request.getStartTime(), DateUtil.FMT_TIME_1) + "' <= t" +
                    ".start_time");
        }
        if (null != request.getEndTime()) {
            whereSql.append(" AND '" + DateUtil.format(request.getEndTime(), DateUtil.FMT_TIME_1) + "' >= t.end_time");
        }

        switch (request.getQueryTab()) {
            case STARTED://进行中
                whereSql.append(" AND ((now() >= t.start_time AND now() <= t.end_time) or t.activity_type=4) AND t" +
                        ".pause_flag = 0 AND t.activity_type != 1");
                break;
            case PAUSED://暂停中
                whereSql.append(" AND ((now() >= t.start_time AND now() <= t.end_time) or t.activity_type=4) AND t" +
                        ".pause_flag = 1");
                break;
            case NOT_START://未开始
                whereSql.append(" AND now() < t.start_time");
                break;
            case ENDED://已结束
                whereSql.append(" AND now() > t.end_time");
                break;
            default:
                break;
        }

        if (StringUtils.isNotBlank(request.getJoinLevel())) {
            whereSql.append(" AND  find_in_set( '" + request.getJoinLevel() + "' , t.join_level)");
        }
        whereSql.append(" order by t.create_time desc");
        Query query = entityManager.createNativeQuery(sql.concat(whereSql.toString()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", CouponActivity.class);
        List<CouponActivity> responsesList = (List<CouponActivity>) query.getResultList();
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM coupon_activity t ";
        long count = 0;
        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql.toString()));
            count = Long.parseLong(queryCount.getSingleResult().toString());
        }
        log.info("优惠券活动列表返回信息------------------>{}",responsesList);
        return new PageImpl<>(responsesList, request.getPageable(), count);
    }

    /**
     * 保存活动时，1 校验优惠券是否存在 2校验优惠券的结束时间是否都在活动结束时间内
     * 将不符合的优惠券id返回
     * @return
     */
    private List<String> checkCoupon(LocalDateTime activityEndTime, List<CouponActivityConfig> couponActivityConfigs) {
        List<String> ids = new ArrayList<>();
        couponActivityConfigs.forEach(item -> ids.add(item.getCouponId()));
        List<CouponInfo> couponInfos = couponInfoRepository.queryByIds(ids);
        List<String> errorIds = new ArrayList<>();
        if (ids.size() > couponInfos.size()) {
            errorIds = ids.stream().filter(id ->
                    couponInfos.stream().noneMatch(couponInfo -> couponInfo.getCouponId().equals(id))).collect
                    (Collectors.toList());
            if (CollectionUtils.isNotEmpty(errorIds)) {
                throw new SbcRuntimeException(errorIds, CouponErrorCode.COUPON_INFO_NOT_EXIST);
            }
        }

        // 会员权益赠券的活动没有结束时间，直接return
        if (Objects.isNull(activityEndTime)) {
            return errorIds;
        }

        for (CouponInfo item : couponInfos) {
            if (RangeDayType.RANGE_DAY == item.getRangeDayType() && item.getEndTime().isBefore(activityEndTime)) {
                errorIds.add(item.getCouponId());
            }
            continue;
        }
        return errorIds;
    }

    /**
     * 获取目前最后一个开始的优惠券活动
     * @return
     */
    public CouponActivity getLastActivity() {
        List<CouponActivity> activityList = couponActivityRepository.getLastActivity(PageRequest.of(0, 1, Sort
                .Direction.DESC, "startTime"));
        return CollectionUtils.isNotEmpty(activityList) ? activityList.get(0) : null;
    }

    /**
     * 获取目前最后一个开始的优惠券活动（当前时间24小时内即将开始的优惠券活动）
     * @return
     */
    public CouponActivity getFirstActivity() {
        List<CouponActivity> activityList = couponActivityRepository.getFirstActivity(LocalDateTime.now().plusHours(24),PageRequest.of(0, 1, Sort
                .Direction.ASC, "startTime"));
        return CollectionUtils.isNotEmpty(activityList) ? activityList.get(0) : null;
    }

    /**
     * 校验 进店赠券活动、注册赠券活动、企业注册赠券活动，购买指定商品赠券，同一时间段内只能有1个！
     * true 表示 校验失败
     * @param statTime
     * @param endTime
     * @param type
     * @param storeId
     * @param activityId
     * @return
     */
    private Boolean checkActivity(LocalDateTime statTime, LocalDateTime endTime, CouponActivityType type, Long
            storeId, String activityId) {
        Boolean flag = Boolean.FALSE;
        if (CouponActivityType.REGISTERED_COUPON == type || CouponActivityType.STORE_COUPONS == type
                || CouponActivityType.ENTERPRISE_REGISTERED_COUPON == type || CouponActivityType.SIGN_GIVE == type
                /*|| CouponActivityType.BUY_ASSIGN_GOODS_COUPON == type*/ || CouponActivityType.FULL_ORDER == type) {
            List<CouponActivity> activityList = couponActivityRepository.queryActivityByTime(statTime, endTime, type, storeId);
            //过滤当前活动id
            if (StringUtils.isNotBlank(activityId)) {
                activityList = activityList.stream().filter(
                        item -> !item.getActivityId().equals(activityId)).collect(Collectors.toList());
            }
            if (!activityList.isEmpty()) {
                flag = Boolean.TRUE;
            }
        }
        return flag;
    }

    /**
     * 领取一组优惠券
     * 订单满额赠券
     * @param customerId
     * @param type
     * @param storeId
     * @return
     */
    @Transactional
    @LcnTransaction
    public BuyGoodsOrFullOrderSendCouponResponse getOrderSendCouponGroup(String customerId, BigDecimal orderPrice, CouponActivityType type, Long storeId){
        //参数校验
        if (CouponActivityType.BUY_ASSIGN_GOODS_COUPON != type){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (customerId == null || storeId == null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        // 1、查询是否该类型的活动在进行中 并且活动剩余优惠券组数>0；
        List<CouponActivity> couponActivityList = couponActivityRepository.queryCouponActivityByType(type, storeId);
        if (couponActivityList.size() == 0) {
            return null;
        } else if (couponActivityList.size() != 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "查询活动时：活动类型为：" + type.toString() + "的活动数据重复！！！");
        }
        CouponActivity activity = couponActivityList.get(0);
        //暂时没有用户领取限制如果有可以加上
//        boolean flag = this.checkCustomerQualify(customerId, type, activity.getActivityId());
//        if (!flag) {
//            return null;
//        }
        //获取订单满额赠券活动满足的配置信息
        List<CouponActivityOrder> couponActivityOrderList =
                couponActivityOrderRepository.getCouponActivityOrderByOrderPriceAndActivityId(activity.getActivityId(),orderPrice);
        //订单没有满额 直接返回
        if (CollectionUtils.isEmpty(couponActivityList)){
            return null;
        }
        //获取最大优惠的满额配置信息
        CouponActivityOrder couponActivityOrder = couponActivityOrderList.get(0);
        //判断改组活动的剩余优惠券数量，如果<0 返回null
        if (couponActivityOrder.getLeftGroupNum() <= 0){
            return null;
        }

        // 2、未领完则 先领取（并发如果很大，可对这部分加锁）
        int num = couponActivityOrderRepository.getCouponGroup(couponActivityOrder.getCouponActivityOrderId());

        // 3、再生成用户优惠券数据
        List<CouponActivityConfig> couponActivityConfigList =
                couponActivityConfigRepository.findByCouponActivityOrderId(couponActivityOrder.getCouponActivityOrderId());
        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().
                map(CouponActivityConfig::getCouponId).collect(Collectors.toList()));
        List<GetCouponGroupResponse> getCouponGroupResponses = KsBeanUtil.copyListProperties(couponInfoList,GetCouponGroupResponse.class);
        getCouponGroupResponses = getCouponGroupResponses.stream().peek(item -> couponActivityConfigList.forEach(config ->{
            if (item.getCouponId().equals(config.getCouponId())) {
                item.setTotalCount(config.getTotalCount());
            }
        })).collect(Collectors.toList());
        couponCodeService.sendBatchCouponCodeByCustomer(getCouponGroupResponses, customerId, activity.getActivityId());

        //4. 按金额大小 从大到小排序
        getCouponGroupResponses.sort(Comparator.comparing(GetCouponGroupResponse::getDenomination).reversed());

        BuyGoodsOrFullOrderSendCouponResponse response = new BuyGoodsOrFullOrderSendCouponResponse();
        response.setCouponList(getCouponGroupResponses);
        response.setTitle(activity.getActivityName());

        return response;
    }

    /**
     * 1.久未下单送券判断
     * 2.送券
     * @param customerId
     * @param activitys
     */
    @Transactional
    @LcnTransaction
    public List<LongNotOrderSendCouponGroupResponse> getLongNotOrderSendCouponGroup(String customerId,List<CouponActivityVO> activitys){
        List<LongNotOrderSendCouponGroupResponse> list = new ArrayList<>();
        activitys.forEach(var->{
            //对应活动的优惠券全部已过期且未使用过再发一次
            if(activityCouponCodes(customerId,var.getActivityId())){
                //生成用户优惠券数据

                //得到活动对应优惠券关系
                List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigService.queryByActivityId(var.getActivityId());
                //得到对应优惠券信息
                List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                        CouponActivityConfig::getCouponId).collect(Collectors.toList()));
                //组装请求结构
                List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList,
                        GetCouponGroupResponse.class);
                getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config -> {
                    if (item.getCouponId().equals(config.getCouponId())) {
                        item.setTotalCount(config.getTotalCount());
                    }
                })).collect(Collectors.toList());

                //发送优惠券
                List<CouponCode> couponCodes = couponCodeService.sendBatchCouponCodeByCustomerLongNotOrder(getCouponGroupResponse, customerId, var.getActivityId());
                log.info("fang---------->couponCodes :" + JSON.toJSONString(couponCodes));
                //排序
                getCouponGroupResponse.sort(Comparator.comparing(GetCouponGroupResponse::getDenomination).reversed());

                LongNotOrderSendCouponGroupResponse response = new LongNotOrderSendCouponGroupResponse();
                response.setTitle(var.getActivityName());
                response.setCouponList(getCouponGroupResponse);

                list.add(response);
            }
        });
        return list;
    }

    /**
     * 创建活动立即发券--->针对精准发券
     * @param request
     */
    @Transactional
    @LcnTransaction
    public BaseResponse createActivitySendNow(CouponActivityAddRequest request){
        log.info("createActivitySendNow.request----------->>"+ JSONObject.toJSONString(request));
        //得到对应优惠券信息
        List<CouponActivityConfig> couponActivityConfigs = KsBeanUtil.copyListProperties(request.getCouponActivityConfigs(), CouponActivityConfig.class);
        log.info("createActivitySendNow.couponActivityConfigs----------->>"+ JSONObject.toJSONString(couponActivityConfigs));
        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigs.stream().map(
                CouponActivityConfig::getCouponId).collect(Collectors.toList()));
        //组装请求结构
        List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList,
                GetCouponGroupResponse.class);

        getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> request.getCouponActivityConfigs().forEach(config -> {
            if (item.getCouponId().equals(config.getCouponId())) {
                item.setTotalCount(config.getTotalCount());
            }
        })).collect(Collectors.toList());
        //发送优惠券
        List<GetCouponGroupResponse> finalGetCouponGroupResponse = getCouponGroupResponse;
        log.info("createActivitySendNow.finalGetCouponGroupResponse----------->>"+ JSONObject.toJSONString(finalGetCouponGroupResponse));
        request.getCustomerScopeIds().forEach(var ->{
            List<CouponCode> couponCodes = couponCodeService.sendBatchCouponCodeByCustomer(finalGetCouponGroupResponse, var,request.getActivityId());
            log.info("fang---------->券结果createActivitySendNow :" + JSON.toJSONString(couponCodes));
        });
        return BaseResponse.SUCCESSFUL();
    }

    private Boolean activityCouponCodes(String customerId,String aciivityId){

        //得到用户对应活动领券信息
        CouponCodeQueryRequest request = new CouponCodeQueryRequest();
        request.setActivityId(aciivityId);
        request.setCustomerId(customerId);
        List<CouponCode> couponCodes = couponCodeService.listCouponCodeByCondition(request);
        //检查是否使用过本次活动的优惠券
        AtomicReference<Boolean> res = new AtomicReference<>(false);
        if(CollectionUtils.isNotEmpty(couponCodes)){
            couponCodes.forEach(couponVar->{
                log.info("******:-------"+couponVar.getEndTime().isBefore(LocalDateTime.now()));
                //券未使用并且已过期则补发
                if(couponVar.getUseStatus().equals(DefaultFlag.NO) && couponVar.getEndTime().isBefore(LocalDateTime.now())){
                    res.set(true);
                }
            });
        }else{
            res.set(true);
        }
        return res.get();
    }

    /**
     * 校验是否满足领取N组优惠券
     * 购买指定商品赠券
     * @param customerId
     * @param type
     * @param tradeItemInfoDTOS
     * @return
     */
    public BuyGoodsOrFullOrderSendCouponResponse checkGoodsSendCoupon(String customerId,CouponActivityType type,List<TradeItemInfoDTO> tradeItemInfoDTOS,Long wareId) {
        BuyGoodsOrFullOrderSendCouponResponse response = new BuyGoodsOrFullOrderSendCouponResponse();
        List<GetCouponGroupResponse> sendCoupons = new ArrayList<>();
        //购买的商品skuIds
        List<String> buySkuIds = tradeItemInfoDTOS.stream().map(TradeItemInfoDTO::getSkuId).collect(Collectors.toList());
        if (customerId == null || CollectionUtils.isEmpty(buySkuIds)){
            log.info("===========》：：：参数错误，无客户id或者商品信息");
            return response;
        }
        // 1、查询是否该类型的活动在进行中 并且活动剩余优惠券组数>0；
        List<CouponActivity> couponActivityList = couponActivityRepository.queryGoingActivityByGoodsByWareId(type.toValue(), buySkuIds,wareId);
        if (couponActivityList.size() == 0) {
            log.info("===========》：：：无进行中的优惠券活动，结束购买指定商品赠券逻辑！");
            return response;
        }

        couponActivityList.forEach(activity -> {
            //获取参与活动的skuIds
            List<CouponActivityGoods> couponActivityGoods = couponActivityGoodsRepository.findByActivityId(activity.getActivityId());
            List<String> couponSkuIds = couponActivityGoods.stream().map(CouponActivityGoods::getGoodsInfoId).collect(Collectors.toList());
            //获取购买商品在赠券活动关联的skuIds
            List<String> intersectionSkuIds = couponSkuIds.stream().filter(item -> buySkuIds.contains(item)).collect(Collectors.toList());
            //如果购买商品没有在赠券活动中直接返回null
            if (CollectionUtils.isEmpty(intersectionSkuIds)) {
                return;
            }
            //如果剩下的数量<0,返回空
            if (activity.getLeftGroupNum() <= 0) {
                return;
            }

            List<CouponActivityLevel> levels = new ArrayList<>();
            if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)
                    || activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                //获取参与活动的关联促销等级信息（满数量及满金额赠）
                levels = couponActivityLevelRepository.findByActivityId(activity.getActivityId());
            }

            //参与指定商品赠券活动的购买商品信息
            List<TradeItemInfoDTO> intersectionTradeItems = tradeItemInfoDTOS.stream()
                    .filter(tradeItem -> intersectionSkuIds.contains(tradeItem.getSkuId())).collect(Collectors.toList());

            List<GetCouponGroupResponse> couponList = new ArrayList<>();
            //满足购买全部商品赠券
            if (activity.getCouponActivityFullType().equals(CouponActivityFullType.ALL)) {
                if (!buySkuIds.containsAll(couponSkuIds)) {
                    log.info("不满足购买全部商品赠券条件，跳出本次赠券循环逻辑：：：buySkuIds:{},couponSkuIds:{}",buySkuIds,couponSkuIds);
                    return;
                }
                Long receiveNum = 1L;
                if (activity.getIsOverlap().equals(DefaultFlag.YES)) {
                    //获取参与购买商品中的最小购买量
                    receiveNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).min().getAsLong();
                }
                //赠券
                couponList = this.getSendCoupon(activity,receiveNum,customerId,type,null);
                //满足购买任一商品赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.ANY_ONE)){
                Long receiveNum = 1L;
                if (activity.getIsOverlap().equals(DefaultFlag.YES)) {
                    //优惠券领取套数=购买活动商品总件数相加
                    receiveNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).sum();
                }
                //判断用户领取资格，每个用户只能领取一次 -->暂时没有用户领取限制如果有可以加上
//                boolean flag = this.checkCustomerQualify(customerId, type, activity.getActivityId());
//                if (!flag) {
//                    return null;
//                }
                couponList = this.getSendCoupon(activity, receiveNum, customerId, type,null);
                //满金额赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)) {
                if (CollectionUtils.isNotEmpty(levels)) {
                    intersectionTradeItems.forEach(item -> {
                        GoodsInfoVO goodsInfoVO = goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(item.getSkuId()).build()).getContext().getGoodsInfo();
                        item.setPrice(goodsInfoVO.getMarketPrice());
                    });
                    //参与指定商品赠券活动的购买商品总金额
                    BigDecimal intersectionTotalPrice = intersectionTradeItems.stream()
                            .reduce(BigDecimal.ZERO,(x,y) -> x.add(y.getPrice().multiply(new BigDecimal(y.getNum()))),BigDecimal::add);
                    levels.sort(Comparator.comparing(CouponActivityLevel::getFullAmount).reversed());
                    for (CouponActivityLevel level : levels) {
                        if (level.getFullAmount().compareTo(intersectionTotalPrice) != 1) {
                            couponList = this.getSendCoupon(activity,1L, customerId, type, level.getActivityLevelId());
                            break;
                        }
                    }
                }
                //满数量赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                if (CollectionUtils.isNotEmpty(levels)) {
                    //参与指定商品赠券活动的购买商品总数量
                    Long intersectionBuyNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).sum();

                    levels.sort(Comparator.comparing(CouponActivityLevel::getFullCount).reversed());
                    for (CouponActivityLevel level : levels) {
                        if (level.getFullCount().compareTo(intersectionBuyNum) != 1) {
                            couponList = this.getSendCoupon(activity, 1L, customerId, type, level.getActivityLevelId());
                            break;
                        }
                    }
                }
            } else {
                log.info("不合符购买指定商品赠券活动条件：：：fullType:{}",activity.getCouponActivityFullType());
                return;
            }
            if (CollectionUtils.isNotEmpty(couponList)) {
                sendCoupons.addAll(couponList);
            }
        });

        if (CollectionUtils.isNotEmpty(sendCoupons)) {
            response.setIsMeetSendCoupon(true);
            response.setCouponList(sendCoupons);
        }
        return response;
    }

    /**
     * 领取N组优惠券
     * 购买指定商品赠券
     * @param customerId
     * @param type
     * @param tradeItemInfoDTOS
     * @param storeId
     * @return
     */
    @Transactional
    @LcnTransaction
    public List<BuyGoodsOrFullOrderSendCouponResponse> getGoodsSendCouponGroup(String customerId,
                                                                               CouponActivityType type,
                                                                               List<TradeItemInfoDTO> tradeItemInfoDTOS,
                                                                               Long storeId){
        List<BuyGoodsOrFullOrderSendCouponResponse> listResponse = new ArrayList<>();
        //购买的商品skuIds
        List<String> buySkuIds = tradeItemInfoDTOS.stream().map(TradeItemInfoDTO::getSkuId).collect(Collectors.toList());
        //参数校验
        if (CouponActivityType.BUY_ASSIGN_GOODS_COUPON != type){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (customerId == null || CollectionUtils.isEmpty(buySkuIds) || storeId == null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        // 1、查询是否该类型的活动在进行中 并且活动剩余优惠券组数>0；
        List<CouponActivity> couponActivityList = couponActivityRepository.queryGoingActivityByGoods(type.toValue(), buySkuIds);
        if (couponActivityList.size() == 0) {
            log.info("===========》：：：无进行中的优惠券活动，结束购买指定商品赠券逻辑！");
            return listResponse;
        } /*else if (couponActivityList.size() != 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "查询活动时：活动类型为：" + type.toString() + "的活动数据重复！！！");
        }*/
        log.info("开始循环赠券逻辑，couponActivityList:::{}",couponActivityList);
        couponActivityList.forEach(activity -> {
            BuyGoodsOrFullOrderSendCouponResponse response = new BuyGoodsOrFullOrderSendCouponResponse();
            //获取参与活动的skuIds
            List<CouponActivityGoods> couponActivityGoods = couponActivityGoodsRepository.findByActivityId(activity.getActivityId());
            List<String> couponSkuIds = couponActivityGoods.stream().map(CouponActivityGoods::getGoodsInfoId).collect(Collectors.toList());
            //获取购买商品在赠券活动关联的skuIds
            List<String> intersectionSkuIds = couponSkuIds.stream().filter(item -> buySkuIds.contains(item)).collect(Collectors.toList());
            //如果购买商品没有在赠券活动中直接返回null
            if (CollectionUtils.isEmpty(intersectionSkuIds)) {
                log.info("购买商品不在活动中，结束本次赠券循环，activityId:::{}",activity.getActivityId());
                return;
            }
            //如果剩下的数量<0,返回空
            if (activity.getLeftGroupNum() <= 0) {
                log.info("本次赠券活动剩余数量不足,结束本次赠券循环，activityId:::{}",activity.getActivityId());
                return;
            }

            List<CouponActivityLevel> levels = new ArrayList<>();
            if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)
                    || activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                //获取参与活动的关联促销等级信息（满数量及满金额赠）
                levels = couponActivityLevelRepository.findByActivityId(activity.getActivityId());
            }

            //参与指定商品赠券活动的购买商品信息
            List<TradeItemInfoDTO> intersectionTradeItems = tradeItemInfoDTOS.stream()
                    .filter(tradeItem -> intersectionSkuIds.contains(tradeItem.getSkuId())).collect(Collectors.toList());
            //满足购买全部商品赠券
            if (activity.getCouponActivityFullType().equals(CouponActivityFullType.ALL)) {
                if (!buySkuIds.containsAll(couponSkuIds)) {
                    log.info("不满足购买全部商品赠券条件，跳出本次赠券循环逻辑：：：buySkuIds:{},couponSkuIds:{}",buySkuIds,couponSkuIds);
                    return;
                }
                Long receiveNum = 1L;
                if (activity.getIsOverlap().equals(DefaultFlag.YES)) {
                    //获取参与购买商品中的最小购买量
                    receiveNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).min().getAsLong();
                }
                //赠券
                response = this.sendCoupon(activity,receiveNum,customerId,type,null);
            //满足购买任一商品赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.ANY_ONE)){
                Long receiveNum = 1L;
                if (activity.getIsOverlap().equals(DefaultFlag.YES)) {
                    //优惠券领取套数=购买活动商品总件数相加
                    receiveNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).sum();
                }
                //判断用户领取资格，每个用户只能领取一次 -->暂时没有用户领取限制如果有可以加上
//                boolean flag = this.checkCustomerQualify(customerId, type, activity.getActivityId());
//                if (!flag) {
//                    return null;
//                }
                response = this.sendCoupon(activity, receiveNum, customerId, type,null);
            //满金额赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)) {
                if (CollectionUtils.isNotEmpty(levels)) {
                    //参与指定商品赠券活动的购买商品总金额
                    BigDecimal intersectionTotalPrice = intersectionTradeItems.stream()
                            .reduce(BigDecimal.ZERO,(x,y) -> x.add(y.getPrice().multiply(new BigDecimal(y.getNum()))),BigDecimal::add);
                    levels.sort(Comparator.comparing(CouponActivityLevel::getFullAmount).reversed());
                    for (CouponActivityLevel level : levels) {
                        if (level.getFullAmount().compareTo(intersectionTotalPrice) != 1) {
                            response = this.sendCoupon(activity,1L, customerId, type, level.getActivityLevelId());
                            break;
                        }
                    }
                }
            //满数量赠券
            } else if (activity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                if (CollectionUtils.isNotEmpty(levels)) {
                    //参与指定商品赠券活动的购买商品总数量
                    Long intersectionBuyNum = intersectionTradeItems.stream().mapToLong(TradeItemInfoDTO::getNum).sum();

                    levels.sort(Comparator.comparing(CouponActivityLevel::getFullCount).reversed());
                    for (CouponActivityLevel level : levels) {
                        if (level.getFullCount().compareTo(intersectionBuyNum) != 1) {
                            response = this.sendCoupon(activity, 1L, customerId, type, level.getActivityLevelId());
                            break;
                        }
                    }
                }
            } else {
                log.info("不合符购买指定商品赠券活动条件：：：fullType:{}",activity.getCouponActivityFullType());
                return;
            }
            if(Objects.nonNull(response)) {
                listResponse.add(response);
            }
        });
//        CouponActivity activity = couponActivityList.get(0);

        return listResponse;
    }

    /**
     * 获取购买指定商品赠券信息
     * @param activity
     * @param receiveNum
     * @param customerId
     * @param type
     * @return
     */
    private List<GetCouponGroupResponse> getSendCoupon(CouponActivity activity,Long receiveNum,
                                                             String customerId,CouponActivityType type,
                                                             String activityLevelId){
        // 1、再生成用户优惠券数据
        List<CouponActivityConfig> couponActivityConfigs = new ArrayList<>();
        if (Objects.isNull(activityLevelId)) {
            couponActivityConfigs = couponActivityConfigService.queryByActivityId(activity.getActivityId());
        } else {
            couponActivityConfigs = couponActivityConfigService
                    .queryByActivityIdAndActivityLevelId(activity.getActivityId(),activityLevelId);
        }
        List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigs;

        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                CouponActivityConfig::getCouponId).collect(Collectors.toList()));
        List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList,
                GetCouponGroupResponse.class);
        getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config -> {
            if (item.getCouponId().equals(config.getCouponId())) {
                item.setTotalCount(config.getTotalCount() * receiveNum);
            }
        })).collect(Collectors.toList());
        //2. 按金额大小 从大到小排序
        getCouponGroupResponse.sort(Comparator.comparing(GetCouponGroupResponse::getDenomination).reversed());

        return getCouponGroupResponse;
    }

    /**
     * 购买指定商品赠券
     * @param activity
     * @param receiveNum
     * @param customerId
     * @param type
     * @return
     */
    private BuyGoodsOrFullOrderSendCouponResponse sendCoupon(CouponActivity activity,Long receiveNum,
                                                             String customerId,CouponActivityType type,
                                                             String activityLevelId){
        BuyGoodsOrFullOrderSendCouponResponse response = new BuyGoodsOrFullOrderSendCouponResponse();
        // 2、未领完则 先领取（并发如果很大，可对这部分加锁）
        int num = couponActivityRepository.getCouponGroup(activity.getActivityId(), receiveNum.intValue());
        if (num == 0) {
            return response;
        }
        if (num != 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "更新剩余数量时：活动类型为：" + type.toString() + "的活动数据重复！！！");
        }
        log.info("购买指定商品开始赠券，customerId:::{}==========>>CouponActivity:::{}",customerId,activity);
        // 3、再生成用户优惠券数据
        List<CouponActivityConfig> couponActivityConfigs = new ArrayList<>();
        if (Objects.isNull(activityLevelId)) {
            couponActivityConfigs = couponActivityConfigService.queryByActivityId(activity.getActivityId());
        } else {
            couponActivityConfigs = couponActivityConfigService
                    .queryByActivityIdAndActivityLevelId(activity.getActivityId(),activityLevelId);
        }
        List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigs;

        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                CouponActivityConfig::getCouponId).collect(Collectors.toList()));
        List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList,
                GetCouponGroupResponse.class);
        getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config -> {
            if (item.getCouponId().equals(config.getCouponId())) {
                item.setTotalCount(config.getTotalCount() * receiveNum);
            }
        })).collect(Collectors.toList());
        List<CouponCode> couponCodes = couponCodeService.sendBatchCouponCodeByCustomer(getCouponGroupResponse, customerId, activity.getActivityId());
        //4. 按金额大小 从大到小排序
        getCouponGroupResponse.sort(Comparator.comparing(GetCouponGroupResponse::getDenomination).reversed());

        response.setCouponCodeVOS(KsBeanUtil.convert(couponCodes,CouponCodeVO.class));
        response.setCouponList(getCouponGroupResponse);
        response.setTitle(activity.getActivityName());
        return response;
    }

    /**
     * 领取一组优惠券 （注册活动或者进店活动）
     * 用户注册成功或者进店后，发放赠券
     * @param customerId
     * @param type
     * @param storeId
     * @return
     */
    @Transactional
    @LcnTransaction
    public GetRegisterOrStoreCouponResponse getCouponGroup(String customerId, CouponActivityType type, Long storeId) {
        //参数校验
        if (CouponActivityType.REGISTERED_COUPON != type && CouponActivityType.STORE_COUPONS != type && CouponActivityType.ENTERPRISE_REGISTERED_COUPON != type) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (customerId == null || storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        // 1、查询是否该类型的活动在进行中 并且活动剩余优惠券组数>0；
        List<CouponActivity> couponActivityList = couponActivityRepository.queryGoingActivityByType(type, storeId);
        //企业会员注册-企业会员的注册赠券活动，优先级高于全平台客户。
        //企业会员注册赠券活动不存在或者优惠券组数没有了，继续注册赠券的逻辑
        if(CouponActivityType.ENTERPRISE_REGISTERED_COUPON == type && CollectionUtils.isEmpty(couponActivityList)){
            couponActivityList=couponActivityRepository.queryGoingActivityByType(CouponActivityType.REGISTERED_COUPON , storeId);
        }

        if (couponActivityList.size() == 0) {
            return null;
        } else if (couponActivityList.size() != 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "查询活动时：活动类型为：" + type.toString() + "的活动数据重复！！！");
        }
        CouponActivity activity = couponActivityList.get(0);
        GetRegisterOrStoreCouponResponse response = new GetRegisterOrStoreCouponResponse();
        boolean flag = this.checkCustomerQualify(customerId, type, activity.getActivityId());
        if (!flag) {
            return null;
        }
        //如果剩下的数量<0,返回空
        if (activity.getLeftGroupNum() == 0) {
            return null;
        }
        response.setDesc(activity.getActivityDesc());
        response.setTitle(activity.getActivityTitle());
        // 2、未领完则 先领取（并发如果很大，可对这部分加锁）
        int num = couponActivityRepository.getCouponGroup(activity.getActivityId());
        if (num == 0) {
            return null;
        }
        if (num != 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "更新剩余数量时：活动类型为：" + type.toString() + "的活动数据重复！！！");
        }
        // 3、再生成用户优惠券数据
        List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigService.queryByActivityId(activity
                .getActivityId());
        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                CouponActivityConfig::getCouponId).collect(Collectors.toList()));
        List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList,
                GetCouponGroupResponse.class);
        getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config
                -> {
            if (item.getCouponId().equals(config.getCouponId())) {
                item.setTotalCount(config.getTotalCount());
            }
        })).collect(Collectors.toList());
        couponCodeService.sendBatchCouponCodeByCustomer(getCouponGroupResponse, customerId, activity.getActivityId());
        //4. 按金额大小 从大到小排序
        getCouponGroupResponse.sort(Comparator.comparing(GetCouponGroupResponse::getDenomination).reversed());
        response.setCouponList(getCouponGroupResponse);
        return response;
    }

    /**
     * 查询活动（注册赠券活动、进店赠券活动）不可用的时间范围
     * @param request
     * @return
     */
    public CouponActivityDisabledTimeResponse queryActivityEnableTime(CouponActivityDisabledTimeRequest request) {
        if (CouponActivityType.ALL_COUPONS == request.getCouponActivityType()
                || CouponActivityType.SPECIFY_COUPON == request.getCouponActivityType()) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<CouponActivity> couponActivityList = couponActivityRepository.queryActivityDisableTime(request
                .getCouponActivityType(), request.getStoreId());
        if (StringUtils.isNotBlank(request.getActivityId()) && !couponActivityList.isEmpty()) {
            couponActivityList = couponActivityList.stream().filter(
                    item -> !item.getActivityId().equals(request.getActivityId())).collect(Collectors.toList());
        }
        CouponActivityDisabledTimeResponse disabledTimeResponses = new CouponActivityDisabledTimeResponse();
        disabledTimeResponses.setCouponActivityDisabledTimeVOList(
                couponActivityList.stream().map(item -> {
                    CouponActivityDisabledTimeVO disabledTime = new CouponActivityDisabledTimeVO();
                    disabledTime.setStartTime(item.getStartTime());
                    disabledTime.setEndTime(item.getEndTime());
                    return disabledTime;
                }).collect(Collectors.toList()));
        return disabledTimeResponses;
    }

    /**
     * 判断用户领券资格
     * @param customerId
     * @param type
     * @param activityId
     * @return
     */
    private boolean checkCustomerQualify(String customerId, CouponActivityType type, String activityId) {
        boolean flag = Boolean.FALSE;
        if (CouponActivityType.REGISTERED_COUPON == type ||CouponActivityType.ENTERPRISE_REGISTERED_COUPON== type) {
            //注册赠券(注册赠券、企业会员注册赠券)，校验当前用户是否有券，如果有券了就不可用领注册券
            Integer num = couponCodeService.countByCustomerId(customerId);
            if (num == 0) {
                flag = true;
            }
        } else if (CouponActivityType.STORE_COUPONS == type || CouponActivityType.BUY_ASSIGN_GOODS_COUPON == type
                || CouponActivityType.FULL_ORDER == type) {
            //进店赠券，根据customerId和activityId判断当前用户在本次活动中是否已经有券
            Integer num = couponCodeService.countByCustomerIdAndActivityId(customerId, activityId);
            if (num == 0) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 指定活动赠券
     * @param request
     * @return
     */
    @Transactional
    public SendCouponResponse sendCouponGroup(SendCouponGroupRequest request) {
        SendCouponResponse response = new SendCouponResponse();
        couponCodeService.sendBatchCouponCodeByCustomer(request.getCouponInfos(), request.getCustomerId(), request
                .getActivityId());
        response.setCouponList(request.getCouponInfos());
        return response;
    }

    /**
     * 邀新注册-发放优惠券
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean sendCouponGroup(CouponGroupAddRequest request) {

        String requestCustomerId = request.getRequestCustomerId();

        List<DistributionRewardCouponDTO> list = request.getDistributionRewardCouponDTOList();

        if (StringUtils.isBlank(requestCustomerId) || CollectionUtils.isEmpty(list)) {
            return Boolean.FALSE;
        }

        List<String> couponIdList = list.stream().map(DistributionRewardCouponDTO::getCouponId).collect(Collectors
                .toList());

        Map<String, Integer> map = list.stream().collect(Collectors.toMap(DistributionRewardCouponDTO::getCouponId,
                DistributionRewardCouponDTO::getCount));

        Integer sum = list.stream().map(DistributionRewardCouponDTO::getCount).reduce(Integer::sum).orElse
                (NumberUtils.INTEGER_ZERO);

        List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponIdList);

        List<GetCouponGroupResponse> couponGroupResponseList = couponInfoList.stream().map(couponInfo -> {
            GetCouponGroupResponse getCouponGroupResponse = KsBeanUtil.convert(couponInfo, GetCouponGroupResponse
                    .class);
            getCouponGroupResponse.setTotalCount(map.get(couponInfo.getCouponId()).longValue());
            return getCouponGroupResponse;
        }).collect(Collectors.toList());

        CouponActivity couponActivity = findDistributeCouponActivity();

        List<CouponCode> codeList = couponCodeService.sendBatchCouponCodeByCustomer(couponGroupResponseList,
                requestCustomerId, couponActivity.getActivityId());

        return sum == codeList.size() ? Boolean.TRUE : Boolean.FALSE;
    }


    @Transactional
    @LcnTransaction
    public SendCouponResponse giveCouponByDays(@RequestBody @Valid CouponActivitySignGiveRequest request){
        SendCouponResponse response = new SendCouponResponse();
        //签到天数
        Integer signDays = request.getSignDays();
        //赠送用户
        String customerId = request.getCustomerId();
        //活动id
        String activityId = request.getActivityId();
        //根据活动id查询天数配置,如果配置了天数,查询优惠券配置,再通过优惠券配置查询优惠券
        CouponActivity couponActivity = this.getCouponActivityByPk(activityId);
        if(Objects.isNull(couponActivity)){
            throw new SbcRuntimeException("240002","签到活动不存在");
        }
        if(couponActivity.getPauseFlag().equals(DefaultFlag.YES)){
            throw new SbcRuntimeException("240003","签到活动已暂停");
        }
        List<CouponSignDays> couponSignDaysList = couponSignDaysRepository.findByActivityIdAndSignDays(activityId, signDays);
        if(CollectionUtils.isNotEmpty(couponSignDaysList)){
            //配置的天数奖励信息
            CouponSignDays couponSignDays = couponSignDaysList.get(0);
            String couponSignDaysId = couponSignDays.getCouponSignDaysId();
            //根据天数id查询优惠券
            List<CouponActivityConfig> byCouponSignDaysId = couponActivityConfigRepository.findByCouponSignDaysId(couponSignDaysId);
            if(CollectionUtils.isNotEmpty(byCouponSignDaysId)){
                CouponActivityConfig couponActivityConfig = byCouponSignDaysId.get(0);
                String couponId = couponActivityConfig.getCouponId();
                //需要赠送的张数
                Long totalCount = couponActivityConfig.getTotalCount();
                CouponInfo couponInfo = couponInfoRepository.queryById(couponId);
                List<GetCouponGroupResponse> couponInfos = new ArrayList<>();
                GetCouponGroupResponse getCouponGroupResponse = new GetCouponGroupResponse();
                getCouponGroupResponse.setTotalCount(totalCount);
                BeanUtils.copyProperties(couponInfo,getCouponGroupResponse);
                couponInfos.add(getCouponGroupResponse);
                couponCodeService.sendBatchCouponCodeByCustomer(couponInfos, customerId, request
                        .getActivityId());
                response.setCouponList(couponInfos);
                LocalDateTime now = LocalDateTime.now();

                List<SignReceiveCoupon> collect = couponInfos.stream().map(getCouponGroupResponse1 -> {
                    SignReceiveCoupon signReceiveCoupon = new SignReceiveCoupon();
                    signReceiveCoupon.setActivityId(activityId);
                    signReceiveCoupon.setSignDays(signDays);
                    signReceiveCoupon.setCustomerId(customerId);
                    signReceiveCoupon.setCouponId(getCouponGroupResponse1.getCouponId());
                    signReceiveCoupon.setTotalCount(getCouponGroupResponse1.getTotalCount());
                    signReceiveCoupon.setReceiveTime(now);
                    return signReceiveCoupon;
                }).collect(Collectors.toList());

                //保存优惠券赠送信息
                signReceiveCouponService.saveAll(collect);
                return response;
            }
        }
        return response;
    }

    /**
     * 充值赠送优惠券
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public SendCouponResponse giveRechargeCoupon(SendCouponRechargeRequest request){
        SendCouponResponse response = new SendCouponResponse();
        String activityId = request.getActivityId();
        String customerId = request.getCustomerId();
        //查询活动信息
        CouponActivity activity = this.getCouponActivityByPk(activityId);
        if(Objects.nonNull(activity)){
            //查询出所有的优惠券信息
            List<CouponActivityConfig> activityConfigs = couponActivityConfigRepository.findByActivityId(activityId);
            //优惠券id集合
            List<String> couponIds = activityConfigs.stream().map(CouponActivityConfig::getCouponId).collect(Collectors.toList());

            Map<String, Long> map = activityConfigs.stream().collect(Collectors.toMap(CouponActivityConfig::getCouponId,
                    CouponActivityConfig::getTotalCount));

            List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponIds);
            List<GetCouponGroupResponse> couponGroupResponseList = couponInfoList.stream().map(couponInfo -> {
                GetCouponGroupResponse getCouponGroupResponse = KsBeanUtil.convert(couponInfo, GetCouponGroupResponse
                        .class);
                getCouponGroupResponse.setTotalCount(map.get(couponInfo.getCouponId()));
                return getCouponGroupResponse;
            }).collect(Collectors.toList());
            response.setCouponList(couponGroupResponseList);
            List<CouponCode> codeList = couponCodeService.sendBatchCouponCodeByCustomer(couponGroupResponseList,
                    customerId, activityId);
        }
        return response;
    }


    /**
     * 查询分销邀新赠券活动
     * @return
     */
    public CouponActivity findDistributeCouponActivity() {
        return couponActivityRepository.findDistributeCouponActivity();
    }

    /**
     * 保存优惠券活动目标客户作用范围
     * @param customerScopeIds
     * @param couponActivity
     */
    private List<CouponMarketingCustomerScope> saveMarketingCustomerScope(List<String> customerScopeIds, CouponActivity
            couponActivity) {
        //全部删除活动关联的目标客户作用范围
        couponMarketingCustomerScopeRepository.deleteByActivityId(couponActivity.getActivityId());
        //保存优惠券活动目标客户作用范围
        if (CollectionUtils.isNotEmpty(customerScopeIds)) {
            List<CouponMarketingCustomerScope> couponMarketingCustomerScopes = new ArrayList<>();
            for (String item : customerScopeIds) {
                CouponMarketingCustomerScope temp = new CouponMarketingCustomerScope();
                temp.setActivityId(couponActivity.getActivityId());
                temp.setCustomerId(item);
                couponMarketingCustomerScopes.add(temp);
            }
            couponMarketingCustomerScopeRepository.saveAll(couponMarketingCustomerScopes);
            return couponMarketingCustomerScopes;
        }
        return null;
    }

    /**
     * 查询客户等级
     * @param couponActivity
     * @return
     */
    private List<CustomerLevelVO> customerLevels(CouponActivity couponActivity) {
        List<CustomerLevelVO> customerLevels = null;
        MarketingJoinLevel marketingJoinLevel = getMarketingJoinLevel(couponActivity.getJoinLevel());
        //其他等级
        if (Objects.equals(MarketingJoinLevel.LEVEL_LIST, marketingJoinLevel)) {

            CustomerLevelRequest customerLevelRequest = CustomerLevelRequest.builder().storeId(couponActivity
                    .getStoreId())
                    .levelType(couponActivity.getJoinLevelType()).build();
            BaseResponse<CustomerLevelInfoResponse> customerLevelInfoResponse = storeLevelQueryProvider
                    .queryCustomerLevelInfo(customerLevelRequest);
            customerLevels = customerLevelInfoResponse.getContext().getCustomerLevelVOList();
        }
        return customerLevels;
    }

    /**
     * 活动关联客户信息
     * @param couponMarketingCustomerScope
     * @param couponActivity
     * @return
     */
    private List<CustomerVO> getCouponMarketingCustomers(List<CouponMarketingCustomerScope> couponMarketingCustomerScope,
                                                         CouponActivity
                                                                 couponActivity) {
        if (CollectionUtils.isNotEmpty(couponMarketingCustomerScope)) {
            List<String> customerIds = couponMarketingCustomerScope.stream().map
                    (CouponMarketingCustomerScope::getCustomerId).collect(Collectors.toList());
            CustomerIdsListRequest request = new CustomerIdsListRequest();
            request.setCustomerIds(customerIds);
            List<CustomerVO> detailResponseList = customerQueryProvider.getCustomerListByIds(request)
                    .getContext().getCustomerVOList();
            return detailResponseList;

        }
        return null;
    }

    public MarketingJoinLevel getMarketingJoinLevel(String joinLevel) {
        if (joinLevel.equals("0")) {
            return MarketingJoinLevel.ALL_LEVEL;
        } else if (joinLevel.equals("-1")) {
            return MarketingJoinLevel.ALL_CUSTOMER;
        } else if (joinLevel.equals("-2")) {
            return MarketingJoinLevel.SPECIFY_CUSTOMER;
        } else {
            return MarketingJoinLevel.LEVEL_LIST;
        }
    }

    /**
     * 通过活动类型获取进行中&未开始的活动
     * @param couponActivityType
     * @param StoreId
     * @return
     */
    public List<CouponActivityVO> listByActivityType(CouponActivityType couponActivityType,Long StoreId) {
        List<CouponActivity> couponActivities = couponActivityRepository.querySNActivityByType(couponActivityType, StoreId);
        if (CollectionUtils.isEmpty(couponActivities)) {
            return Collections.emptyList();
        }
        return KsBeanUtil.convert(couponActivities,CouponActivityVO.class);
    }

    public List<CouponActivityGoodsVO> listByActivityIds(List<String> activityIds) {
        List<CouponActivityGoods> couponActivityGoodsList = couponActivityGoodsRepository.findByActivityIds(activityIds);
        if (CollectionUtils.isEmpty(couponActivityGoodsList)) {
            return Collections.emptyList();
        }
        return KsBeanUtil.convert(couponActivityGoodsList,CouponActivityGoodsVO.class);
    }

}
