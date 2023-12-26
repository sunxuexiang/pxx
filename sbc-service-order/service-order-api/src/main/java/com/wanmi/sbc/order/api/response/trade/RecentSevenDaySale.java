package com.wanmi.sbc.order.api.response.trade;

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
public class RecentSevenDaySale implements Serializable {
    private static final long serialVersionUID = -4205293258341328927L;

    @ApiModelProperty(value = "日期")
    private String dateTime;

    @ApiModelProperty(value = "囤货金额")
    private BigDecimal pileTotalPrice;

    @ApiModelProperty(value = "囤货件数")
    private BigDecimal pileTotalNum;

    @ApiModelProperty(value = "提货金额")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "正常下单金额")
    private BigDecimal tradePrice;
}
