package com.wanmi.sbc.marketing.coupon.response;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 订单满额赠券配置响应类
 * @author: jiangxin
 * @create: 2021-09-10 11:18
 */
@ApiModel
@Data
public class CouponActivityFullOrderResponse implements Serializable {

    private static final long serialVersionUID = 1135440838640845771L;

    /**
     * 订单满额赠券配置信息id
     */
    @ApiModelProperty(value = "订单满额赠券配置信息id")
    private String couponActivityOrderId;

    /**
     * 订单满额值
     */
    @ApiModelProperty(value = "订单满额值")
    private BigDecimal fullOrderPrice;

    /**
     * 优惠券剩余组数
     */
    @ApiModelProperty(value = "优惠券剩余组数")
    private Long leftGroupNum;

    /**
     * 优惠券配置活动信息
     */
    @ApiModelProperty(value = "优惠券配置活动信息")
    private List<CouponActivityConfig> couponActivityConfigs;

    /**
     * 优惠券信息
     */
    @ApiModelProperty(value = "优惠券信息")
    private List<CouponInfo> couponInfoList;

}
