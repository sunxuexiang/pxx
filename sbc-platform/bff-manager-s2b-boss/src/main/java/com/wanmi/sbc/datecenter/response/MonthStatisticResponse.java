package com.wanmi.sbc.datecenter.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 10:06
 */
@ApiModel("月统计数据")
@Data
public class MonthStatisticResponse {


    @ApiModelProperty("下单人数")
    private Integer tradeUser = 0;

    @ApiModelProperty("下单笔数")
    private Integer tradeNum = 0;

    @ApiModelProperty("下单箱数")
    private Integer tradeItemNum = 0;

    @ApiModelProperty("下单总金额")
    private BigDecimal tradePrice = BigDecimal.ZERO;
}
