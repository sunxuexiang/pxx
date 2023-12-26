package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@ApiModel
@Data
public class CouponSignDaysVO implements Serializable {
    private static final long serialVersionUID = 497263293084548631L;

    /**
     * 签到表id
     */
    @ApiModelProperty(name = "签到表id")
    private String couponSignDaysId;

    /**
     * 活动id
     */
    @ApiModelProperty(name = "活动id")
    private String activityId;

    /**
     * 签到天数
     */
    @ApiModelProperty(name = "签到天数")
    private Integer signDays;

    @ApiModelProperty(name = "优惠券配置")
    private CouponActivityConfigVO couponActivityConfigVO;
}
