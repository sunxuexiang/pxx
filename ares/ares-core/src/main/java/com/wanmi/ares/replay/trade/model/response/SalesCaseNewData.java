package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SalesCaseNewData implements Serializable {

    private static final long serialVersionUID = 3378068824026046418L;

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "大白鲸今日销售箱数")
    private BigDecimal todaySalesCase;

    @ApiModelProperty(value = "大白鲸昨日销售箱数")
    private BigDecimal yesterdaySalesCase;

    @ApiModelProperty(value = "大白鲸今昨销售总箱数")
    private BigDecimal towDaySalesCase;
}
