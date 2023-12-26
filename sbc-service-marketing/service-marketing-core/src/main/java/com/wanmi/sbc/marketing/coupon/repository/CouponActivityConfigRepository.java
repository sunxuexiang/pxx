package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠券活动配置表
 */
@Repository
public interface CouponActivityConfigRepository extends JpaRepository<CouponActivityConfig, String>, JpaSpecificationExecutor<CouponActivityConfig> {

    /**
     * 根据优惠券id获取活动配置信息
     *
     * @param couponIds 优惠券id
     * @return
     */
    @Query("from CouponActivityConfig c where c.couponId in ?1")
    List<CouponActivityConfig> findByCouponIds(List<String> couponIds);

    /**
     * 根据优惠券id获取活动配置信息
     *
     * @param couponId 优惠券id
     * @return
     */
    List<CouponActivityConfig> findByCouponId(String couponId);


    /**
     * 根据活动id获取活动配置信息
     *
     * @param activityId 活动id
     * @return
     */
    List<CouponActivityConfig> findByActivityId(String activityId);

    /**
     * 根据活动id及关联等级id获取活动配置信息
     * @param activityId
     * @param activityLevelId
     * @return
     */
    List<CouponActivityConfig> findByActivityIdAndActivityLevelId(String activityId,String activityLevelId);

    /**
     * 删除活动关联的优惠券信息
     *
     * @param activityId
     */
    int deleteByActivityId(String activityId);

    /**
     * 根据活动id和优惠券id，查询具体规则
     *
     * @return
     */
    CouponActivityConfig findByActivityIdAndCouponId(String activityId, String couponId);

    List<CouponActivityConfig> findByCouponSignDaysId(String couponSignDaysId);

    /**
     * 根据订单满额配置信息id查询优惠券活动配置信息
     * @param couponActivityOrderId
     * @return
     */
    List<CouponActivityConfig> findByCouponActivityOrderId(String couponActivityOrderId);

}
