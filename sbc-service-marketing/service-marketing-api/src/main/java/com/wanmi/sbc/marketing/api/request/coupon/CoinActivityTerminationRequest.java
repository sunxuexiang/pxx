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

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/23 11:09
 */
@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinActivityTerminationRequest implements Serializable {
    private static final long serialVersionUID = 7762842113079750248L;

    @ApiModelProperty(value = "活动ID")
    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @ApiModelProperty(value = "活动商品ID")
    @NotNull(message = "活动商品ID不能为空")
    private Long activityGoodsId;

    private String operatorId;
}
