package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Builder
public class SendCouponRechargeRequest implements Serializable {

    @ApiModelProperty(value = "客户id")
    @NotNull
    private String customerId;

    @ApiModelProperty(value = "活动id")
    @NotNull
    private String activityId;

}
