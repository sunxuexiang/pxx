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
public class SalesOrderData extends BaseData implements Serializable {

    @ApiModelProperty(value = "今日订单数")
    private Long todayOrderCount;

    @ApiModelProperty(value = "昨日订单数")
    private Long yesterdayOrderCount;
}
