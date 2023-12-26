package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 优惠券活动订单满额配置信息Repository
 * @author: jiangxin
 * @create: 2021-09-09 20:22
 */
@Repository
public interface CouponActivityOrderRepository extends JpaRepository<CouponActivityOrder,String>, JpaSpecificationExecutor<CouponActivityOrder> {

    /**
     * 删除优惠券活动订单满额配置信息
     * @param activityId
     * @return
     */
    int deleteByActivityId(String activityId);

    /**
     * 根据活动id查询订单满额配置信息
     */
    List<CouponActivityOrder> findCouponActivityOrdersByActivityId(String activityId);

    /**
     * 根据订单金额和活动id获取满足订单满额赠券的配置信息
     * @param orderPrice
     * @param activityId
     * @return
     */
    @Query("from CouponActivityOrder where activityId = ?1 and fullOrderPrice <= ?2 order by fullOrderPrice desc")
    List<CouponActivityOrder> getCouponActivityOrderByOrderPriceAndActivityId(String activityId,BigDecimal orderPrice);

    /**
     * 领取一组订单满额优惠券
     * @param id
     * @return
     */
    @Query("update CouponActivityOrder o set o.leftGroupNum = o.leftGroupNum - 1 where o.couponActivityOrderId = ?1")
    int getCouponGroup(String id);
}
