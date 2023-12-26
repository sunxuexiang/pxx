package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.dto.DistributionRewardCouponDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivitySignGiveRequest implements Serializable {

    private static final long serialVersionUID = 819958208302570886L;

    @ApiModelProperty(value = "签到天数")
    @NotNull
    private Integer signDays;

    @ApiModelProperty(value = "领取人id")
    @NotBlank
    private String customerId;

    @ApiModelProperty(value = "活动id")
    @NotBlank
    private String activityId;
//    /**
//     * 优惠券ID和组数集合
//     */
//    @ApiModelProperty(value = "优惠券ID和组数集合")
//    @NotNull
//    private List<DistributionRewardCouponDTO> distributionRewardCouponDTOList;

}
