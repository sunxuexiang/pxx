package com.wanmi.sbc.returnorder.api.response.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SalesCaseData extends BaseData implements Serializable {

    @ApiModelProperty(value = "今日销售箱数")
    private BigDecimal todaySalesCase;

    @ApiModelProperty(value = "昨日销售箱数")
    private BigDecimal yesterdaySalesCase;
}
