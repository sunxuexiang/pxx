package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 建行对账单分账汇总请求类
 * @author hudong
 * 2023-09-23
 */
@Data
@ApiModel
public class CcbClrgSummaryRequest {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 分账日期
     */
    @ApiModelProperty(value = "分账日期")
    private Date clrgDt;

    /**
     * 交易笔数
     */
    @ApiModelProperty(value = "交易笔数")
    private Integer tradeNum;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmt;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmt;

    /**
     * 平台手续费
     */
    @ApiModelProperty(value = "平台手续费")
    private BigDecimal hdcgAmt;

    /**
     * 分账金额
     */
    @ApiModelProperty(value = "分账金额")
    private BigDecimal clrgMmt;

    /**
     * 分账账户
     */
    @ApiModelProperty(value = "分账账户")
    private String mktMrchId;

    /**
     * 支付流水号
     */
    @ApiModelProperty(value = "支付流水号")
    private String pyOrdrNo;
}
