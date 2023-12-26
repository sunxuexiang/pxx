package com.wanmi.sbc.marketing.api.request.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponsByCodeIdsRequest implements Serializable {
    private static final long serialVersionUID = -1560164812533945491L;

    /**
     * couponCodeId列表
     */
    @ApiModelProperty(value = "couponCodeId列表")
    private List<String> couponCodeIds;
}
