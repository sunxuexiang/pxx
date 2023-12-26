package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityDetailByActivityTypeRequest implements Serializable {

    private static final long serialVersionUID = 819958208302585369L;
    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动类型")
    @NotBlank
    private CouponActivityType activityType;
}
