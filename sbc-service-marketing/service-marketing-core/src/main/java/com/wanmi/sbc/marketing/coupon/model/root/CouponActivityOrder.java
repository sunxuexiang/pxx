package com.wanmi.sbc.marketing.coupon.model.root;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Description: 订单满额赠券配置表
 * @author: jiangxin
 * @create: 2021-09-09 20:14
 */
@Entity
@Table(name = "coupon_activity_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityOrder {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "coupon_activity_order_id")
    private String couponActivityOrderId;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 订单满额配置金额
     */
    @Column(name = "full_order_price")
    private BigDecimal fullOrderPrice;

    /**
     * 优惠券剩余组数
     */
    @Column(name = "left_group_num")
    private Long leftGroupNum;
}
