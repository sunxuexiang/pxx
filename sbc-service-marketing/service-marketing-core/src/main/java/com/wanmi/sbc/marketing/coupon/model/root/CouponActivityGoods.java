package com.wanmi.sbc.marketing.coupon.model.root;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 购买指定商品赠券商品配置表
 * @author: jiangxin
 * @create: 2021-09-06 15:26
 */
@Entity
@Table(name = "coupon_activity_goods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityGoods {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 优惠券活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 商品信息skuid
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;
}
