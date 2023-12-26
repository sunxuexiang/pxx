package com.wanmi.sbc.marketing.coupon.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author: lq
 * @CreateTime:2018-09-12 09:34
 * @Description:优惠券活动配置
 */
@Data
@Entity
@Table(name = "coupon_activity_config")
public class CouponActivityConfig {

    /**
     * 优惠券活动配置表id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "activity_config_id")
    private String activityConfigId;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private String couponId;

    /**
     * 满赠多级促销id
     */
    @Column(name = "activity_level_id")
    private String activityLevelId;

    /**
     * 订单满额赠券配置信息主键id
     */
    @Column(name = "coupon_activity_order_id")
    private String couponActivityOrderId;

    /**
     * 签到天数表id
     */
    @Column(name = "coupon_sign_days_id")
    private String couponSignDaysId;

    /**
     * 优惠券总张数
     */
    @Column(name = "total_count")
    private Long totalCount;

    /**
     * 是否有剩余, 1 有，0 没有
     */
    @Column(name = "has_left")
    @Enumerated
    private DefaultFlag hasLeft;
}
