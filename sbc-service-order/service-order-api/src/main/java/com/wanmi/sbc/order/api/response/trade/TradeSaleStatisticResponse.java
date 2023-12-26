package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeSaleStatisticResponse implements Serializable {
    private static final long serialVersionUID = 4762913670949211297L;

    @ApiModelProperty(value = "销售额")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "下单用户数")
    private Integer buyNum;

    @ApiModelProperty(value = "订单数")
    private Integer orderNum;

    @ApiModelProperty(value = "下单箱数")
    private Long orderItemNum;

    @ApiModelProperty(value = "今日囤货金额")
    private BigDecimal todayPilePrice;

    @ApiModelProperty(value = "今日提货金额")
    private BigDecimal todayTakePrice;

    @ApiModelProperty(value = "昨日囤货金额")
    private BigDecimal yesterdayPilePrice;

    @ApiModelProperty(value = "昨日提货金额")
    private BigDecimal yesterdayTakePrice;

    @ApiModelProperty(value = "今日订单金额")
    private BigDecimal todayTradePrice;

    @ApiModelProperty(value = "昨日订单金额")
    private BigDecimal yesterdayTradePrice;

    @ApiModelProperty(value = "近七日销售额统计")
    private List<RecentSevenDaySale> recentSevenDaySaleTotalPriceList;
}
