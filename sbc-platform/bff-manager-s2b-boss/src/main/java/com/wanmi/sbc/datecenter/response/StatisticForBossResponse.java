package com.wanmi.sbc.datecenter.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/16 17:07
 */
@ApiModel("boss权限查询统计响应数据")
@Data
public class StatisticForBossResponse {

    @ApiModelProperty("负责业务员")
    private String employeeName;

    @ApiModelProperty("管理区域")
    private String manageArea;

    @ApiModelProperty("本月下单用户人数")
    private Integer tradeUserNum;

    @ApiModelProperty("本月下单数量")
    private Integer tradeNum;

    @ApiModelProperty("本月订单箱数")
    private Integer tradeItemNum;

    @ApiModelProperty("本月订单金额")
    private BigDecimal tradePrice;

}