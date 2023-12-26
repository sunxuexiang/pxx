package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.SignReceiveCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SignReceiveCouponRepository extends JpaRepository<SignReceiveCoupon, String>, JpaSpecificationExecutor<SignReceiveCoupon> {

}
