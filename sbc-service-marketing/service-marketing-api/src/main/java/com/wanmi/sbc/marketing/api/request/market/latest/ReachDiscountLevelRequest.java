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
public class ReachDiscountLevelRequest implements Serializable {

    private static final long serialVersionUID = 7285516152245006570L;

    @ApiModelProperty(value = "满折梯度阈值")
    private BigDecimal threshold;

    @ApiModelProperty(value = "折扣")
    private BigDecimal discountRate;

}
