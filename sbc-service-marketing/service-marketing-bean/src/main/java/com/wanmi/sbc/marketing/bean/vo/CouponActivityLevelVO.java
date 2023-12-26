package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 优惠券满赠多级促销
 * @author: XinJiang
 * @time: 2022/2/19 15:29
 */
@ApiModel
@Data
public class CouponActivityLevelVO implements Serializable {

    private static final long serialVersionUID = -5334714366009247757L;

    /**
     * 满赠多级促销id
     */
    @ApiModelProperty(value = "满赠多级促销id")
    private String activityLevelId;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 满金额赠
     */
    @ApiModelProperty(value = "满金额赠")
    private BigDecimal fullAmount;

    /**
     * 满数量赠
     */
    @ApiModelProperty(value = "满数量赠")
    private Long fullCount;

    /**
     * 优惠券配置信息
     */
    @ApiModelProperty(value = "优惠券配置信息")
    private List<CouponActivityConfigVO> couponActivityConfigs;
}
