package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SalesOrderNewData implements Serializable {

    private static final long serialVersionUID = -6168555750776988855L;

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "大白鲸今日订单数")
    private Long todayOrderCount;

    @ApiModelProperty(value = "大白鲸昨日订单数")
    private Long yesterdayOrderCount;

    @ApiModelProperty(value = "大白鲸今昨订单总数数")
    private Long towDayOrderCount;
}
