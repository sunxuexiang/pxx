package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 优惠券等级促销持久层接口
 * @Author: XinJiang
 * @Date: 2022/2/21 9:02
 */
@Repository
public interface CouponActivityLevelRepository extends JpaRepository<CouponActivityLevel,String>, JpaSpecificationExecutor<CouponActivityLevel> {

    /**
     * 通过活动id获取优惠券促销等级信息
     * @param activityId
     * @return
     */
    List<CouponActivityLevel> findByActivityId(String activityId);

    /**
     * 通过活动id删除关联促销等级信息
     * @param activityId
     * @return
     */
    int deleteByActivityId(String activityId);
}
