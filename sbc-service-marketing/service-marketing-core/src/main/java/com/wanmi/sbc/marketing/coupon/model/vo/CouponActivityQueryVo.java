package com.wanmi.sbc.marketing.coupon.model.vo;

import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/28 8:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponActivityQueryVo {


    private String activityId;

    /**
     * 优惠券赠券满赠条件[购买指定商品赠券]
     * 0 全部满足赠，1 满足任意一个赠
     */
    private CouponActivityFullType couponActivityFullType;


    private String goodsInfoId;

}
