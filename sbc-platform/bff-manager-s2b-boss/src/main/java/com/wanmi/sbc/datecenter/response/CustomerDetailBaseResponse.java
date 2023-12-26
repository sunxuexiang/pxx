package com.wanmi.sbc.datecenter.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 10:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("客户详细基本数据统计")
public class CustomerDetailBaseResponse {

    /*客户名称*/
    @ApiModelProperty("客户名称")
    private String customerName;

    /*客户账号*/
    @ApiModelProperty("客户账号")
    private String customerAccount;

    /*下单数量*/
    @ApiModelProperty("下单数量")
    private Integer tradeNum = 0;

    /*下单总箱数*/
    @ApiModelProperty("下单总箱数")
    private Integer tradeItemNum = 0;

    /*总金额*/
    @ApiModelProperty("总金额")
    private BigDecimal tradePrice = BigDecimal.ZERO;
}
