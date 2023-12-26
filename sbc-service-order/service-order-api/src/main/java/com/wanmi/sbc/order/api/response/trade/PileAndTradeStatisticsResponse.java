package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PileAndTradeStatisticsResponse  implements Serializable {

    @ApiModelProperty(value = "囤货笔数")
    private String orderCount;

    @ApiModelProperty(value = "提货笔数")
    private String tradeItemCount;

    @ApiModelProperty(value = "囤货箱数")
    private BigDecimal goodsNum;

    @ApiModelProperty(value = "提货箱数")
    private BigDecimal tradeItemNum;

    @ApiModelProperty(value = "囤货金额")
    private BigDecimal splitPrice;
}
