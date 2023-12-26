package com.wanmi.sbc.marketing.api.request.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 删除优惠券券码
 * @author: XinJiang
 * @time: 2022/2/22 9:44
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCodeDeleteRequest implements Serializable {
    private static final long serialVersionUID = 2515811029416164270L;

    @ApiModelProperty(value = "客户id")
    @NotBlank
    private String customerId;

    @ApiModelProperty(value = "优惠券id")
    @NotNull
    private List<String> couponCodeIds;
}
