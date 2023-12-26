package com.wanmi.sbc.returnorder.api.request.purchase;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CheckPurchaseNumDTO {
    @ApiModelProperty(value = "商品sku")
    private String goodsInfoId;

    @ApiModelProperty(value = "除数标识")
    private BigDecimal divisorFlag = BigDecimal.ONE;

    @ApiModelProperty(value = "拆箱id")
    private Long devanningId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "营销id")
    private Long marketingId;

    @ApiModelProperty(value = "购买量")
    private Long num;
}
