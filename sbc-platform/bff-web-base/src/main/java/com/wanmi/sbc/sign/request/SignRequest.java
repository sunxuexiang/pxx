package com.wanmi.sbc.sign.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class SignRequest {

    @ApiModelProperty(value = "登录用户id")
    private String customerId;

    @ApiModelProperty(value = "优惠券配置id")
    private String activityConfigId;

    @ApiModelProperty(value = "签到时间 yyyy-MM-dd")
    private String date;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "签到时间 yyyyMM")
    private Date getYears;

}