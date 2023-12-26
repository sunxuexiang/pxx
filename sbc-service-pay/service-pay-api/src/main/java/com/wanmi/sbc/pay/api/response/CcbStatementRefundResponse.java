package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 建行对账单退款返回
 * @author hudong
 * 2023-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbStatementRefundResponse implements Serializable {


    /**
     * 分账日期
     */
    @ApiModelProperty(value = "分账日期")
    private String  clrgDt;

    /**
     * 交易笔数
     */
    @ApiModelProperty(value = "交易笔数")
    private Integer  tradeSum;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal txnAmt;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refAmt;



}
