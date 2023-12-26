package com.wanmi.sbc.marketing.api.request.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class CouponActivitySignDaysSaveRequest {

    @ApiModelProperty(value = "签到天数")
    @NotNull
    private Integer signDays;

    @ApiModelProperty(value = "签到优惠券活动配置信息")
    private CouponActivityConfigSaveRequest configSaveRequest;
}
