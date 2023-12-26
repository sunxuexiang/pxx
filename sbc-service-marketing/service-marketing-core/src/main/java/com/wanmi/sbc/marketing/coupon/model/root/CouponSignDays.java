package com.wanmi.sbc.marketing.coupon.model.root;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "coupon_sign_days")
public class CouponSignDays {

    /**
     * 签到表id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "coupon_sign_days_id")
    private String couponSignDaysId;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 签到天数
     */
    @Column(name = "sign_days")
    private Integer signDays;

}
