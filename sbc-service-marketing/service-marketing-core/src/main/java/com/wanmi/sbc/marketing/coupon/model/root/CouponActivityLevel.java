package com.wanmi.sbc.marketing.coupon.model.root;

import com.wanmi.sbc.marketing.bean.vo.CouponActivityConfigVO;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 优惠券满赠多级促销
 * @author: XinJiang
 * @time: 2022/2/19 15:20
 */
@Data
@Entity
@Table(name = "coupon_activity_level")
public class CouponActivityLevel implements Serializable {

    private static final long serialVersionUID = 3446499627308895484L;

    /**
     * 满赠多级促销id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "activity_level_id")
    private String activityLevelId;

    /**
     * 优惠券活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 满金额赠
     */
    @Column(name = "full_amount")
    private BigDecimal fullAmount;

    /**
     * 满数量赠
     */
    @Column(name = "full_count")
    private Long fullCount;

    /**
     *优惠券配置信息
     */
    @Transient
    private List<CouponActivityConfigVO> couponActivityConfigs;
}
