package com.wanmi.sbc.marketing.coupon.service;

import com.wanmi.sbc.marketing.coupon.model.root.SignReceiveCoupon;
import com.wanmi.sbc.marketing.coupon.repository.SignReceiveCouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SignReceiveCouponService {
    @Autowired
    SignReceiveCouponRepository signReceiveCouponRepository;

    public SignReceiveCoupon save(SignReceiveCoupon signReceiveCoupon){
        return signReceiveCouponRepository.save(signReceiveCoupon);
    }

    public List<SignReceiveCoupon> saveAll(List<SignReceiveCoupon> signReceiveCoupons){
        return signReceiveCouponRepository.saveAll(signReceiveCoupons);
    }
}
