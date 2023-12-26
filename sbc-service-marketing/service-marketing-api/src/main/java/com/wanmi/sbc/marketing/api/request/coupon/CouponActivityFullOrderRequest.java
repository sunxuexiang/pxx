package com.wanmi.sbc.marketing.api.request.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 订单满额赠券配置请求类
 * @author: jiangxin
 * @create: 2021-09-09 19:46
 */
@ApiModel
@Data
public class CouponActivityFullOrderRequest implements Serializable {

    private static final long serialVersionUID = -220488990862487124L;

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
     * 优惠券活动配置信息
     */
    @ApiModelProperty(value = "优惠券活动配置信息")
    @Size(max = 10)
    private List<CouponActivityConfigSaveRequest> couponActivityConfigs;
}
