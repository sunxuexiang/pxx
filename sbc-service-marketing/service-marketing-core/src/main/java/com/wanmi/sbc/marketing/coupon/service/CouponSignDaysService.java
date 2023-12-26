package com.wanmi.sbc.marketing.coupon.service;

import com.wanmi.sbc.marketing.coupon.model.root.CouponSignDays;
import com.wanmi.sbc.marketing.coupon.repository.CouponSignDaysRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CouponSignDaysService {

    @Autowired
    CouponSignDaysRepository couponSignDaysRepository;


    public List<CouponSignDays> queryByActivityId(String activityId){
            return couponSignDaysRepository.findByActivityId(activityId);
    }

    @Transactional
    public void deleteByActivityId(String activityId){
         couponSignDaysRepository.deleteByActivityId(activityId);
    }
}
