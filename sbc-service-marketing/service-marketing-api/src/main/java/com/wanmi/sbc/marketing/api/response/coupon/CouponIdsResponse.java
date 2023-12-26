package com.wanmi.sbc.marketing.api.response.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponIdsResponse {

    /**
     *  couponId集合
     */
    @ApiModelProperty(value = "couponId集合")
    private List<String> couponIds;
}
