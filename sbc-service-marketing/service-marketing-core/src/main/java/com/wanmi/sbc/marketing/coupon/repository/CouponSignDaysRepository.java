package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CouponSignDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 签到表
 */
@Repository
public interface CouponSignDaysRepository extends JpaRepository<CouponSignDays, String>, JpaSpecificationExecutor<CouponSignDays> {


    List<CouponSignDays> findByActivityId(String activityId);

    @Query("from CouponSignDays where activityId = ?1 and  signDays = ?2")
    List<CouponSignDays> findByActivityIdAndSignDays(String activityId,Integer signDays);

    @Modifying
    @Query("delete from CouponSignDays where activityId = ?1")
    void deleteByActivityId(String activityId);
}
