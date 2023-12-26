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
public class StatisticPickUpLogResponse implements Serializable {

    @ApiModelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @ApiModelProperty(value = "提货数量")
    private BigDecimal num;

    @ApiModelProperty(value = "提货金额")
    private BigDecimal price;
}
