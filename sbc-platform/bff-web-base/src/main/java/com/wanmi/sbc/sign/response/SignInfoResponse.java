package com.wanmi.sbc.sign.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInfoResponse {

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "签到日期对应优惠券详情")
    private List<SignInfoDetailsVO> signInfoDetailsVOS;
}
