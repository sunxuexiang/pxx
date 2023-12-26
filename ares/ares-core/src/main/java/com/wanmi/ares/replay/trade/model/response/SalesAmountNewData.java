package com.wanmi.ares.replay.trade.model.response;

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
public class SalesAmountNewData implements Serializable {

    private static final long serialVersionUID = 858053064336352777L;

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "大白鲸今日销售金额")
    private BigDecimal todaySalesPrice;

    @ApiModelProperty(value = "大白鲸昨日销售金额")
    private BigDecimal yesterdaySalesPrice;

    @ApiModelProperty(value = "大白鲸今昨销售总额")
    private BigDecimal towDayTotalSalesPrice;
}
