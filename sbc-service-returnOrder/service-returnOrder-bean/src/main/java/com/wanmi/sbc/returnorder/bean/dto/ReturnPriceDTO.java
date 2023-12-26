package com.wanmi.sbc.returnorder.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退货总金额
 * Created by jinwei on 19/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ReturnPriceDTO implements Serializable {

    private static final long serialVersionUID = 8294753953899559151L;

    /**
     * 申请金额状态，是否启用
     */
    @ApiModelProperty(value = "申请金额状态，是否启用",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean applyStatus;

    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyPrice;

    /**
     * 商品总金额
     */
    @ApiModelProperty(value = "商品总金额")
    private BigDecimal totalPrice;


    @ApiModelProperty(value = "应退金额")
    private BigDecimal shouldReturnPrice;

    @ApiModelProperty(value = "实退金额")
    private BigDecimal actualReturnPrice;

    @ApiModelProperty(value = "应退现金")
    private BigDecimal shouldReturnCash;

    @ApiModelProperty(value = "实退现金")
    private BigDecimal actualReturnCash;

    @ApiModelProperty(value = "应退鲸贴")
    private BigDecimal balanceReturnPrice;

    @ApiModelProperty(value = "实退鲸贴")
    private BigDecimal actualBalanceReturnPrice;

    @ApiModelProperty(value = "实退运费")
    private BigDecimal deliveryPrice;

    @ApiModelProperty(value = "实退包装费")
    private BigDecimal packingPrice;

    @ApiModelProperty(value = "在线退款总金额")
    private BigDecimal totalOnlineRefundAmount;

    @ApiModelProperty(value = "余额退款总金额")
    private BigDecimal totalBalanceRefundAmount;

    /**
     * 供货总额
     */
    @ApiModelProperty(value = "供货总额")
    private BigDecimal providerTotalPrice;

}
