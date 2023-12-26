package com.wanmi.sbc.returnorder.api.response.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class StatisticRecordItemPriceNumNoPileUserResponse implements Serializable {

    @ApiModelProperty(value = "囤货未提数量")
    private BigDecimal num;

    @ApiModelProperty(value = "客户手机号")
    private String account;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "业务员名称")
    private String employeeName;
}
