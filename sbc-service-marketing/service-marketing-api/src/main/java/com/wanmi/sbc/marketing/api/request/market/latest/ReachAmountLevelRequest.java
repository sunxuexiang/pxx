package com.wanmi.sbc.marketing.api.request.market.latest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReachAmountLevelRequest implements Serializable {

    private static final long serialVersionUID = 156334637525070130L;

    @ApiModelProperty(value = "满减梯度阈值")
    private BigDecimal threshold;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal reduceAmount;
}
