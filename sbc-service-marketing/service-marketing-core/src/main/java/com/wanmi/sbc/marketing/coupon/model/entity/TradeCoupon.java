package com.wanmi.sbc.marketing.coupon.model.entity;

import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.FullBuyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午3:33 2018/9/29
 * @Description: 订单优惠券
 */
@Data
@Builder
public class TradeCoupon {

    /**
     * 优惠券码id
     */
    private String couponCodeId;

    /**
     * 优惠券码值
     */
    private String couponCode;

    /**
     * 优惠券关联的商品
     */
    private List<String> goodsInfoIds;

    /**
     * 优惠券类型
     */
    private CouponType couponType;

    /**
     * 优惠金额
     */
    private BigDecimal discountsAmount;

    /**
     * 购满多少钱
     */
    private BigDecimal fullBuyPrice;

    /**
     * '购满类型 0：无门槛，1：满N元可使用',
     */
    private FullBuyType fullbuyType;

}
