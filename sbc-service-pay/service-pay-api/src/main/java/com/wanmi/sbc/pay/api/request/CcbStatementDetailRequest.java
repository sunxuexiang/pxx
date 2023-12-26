package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 建行对账单明细查询请求类
 * @author hudong
 * 2023-09-04
 */
@Data
@ApiModel
public class CcbStatementDetailRequest {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;
    /**
     * 市场商户编号
     */
    @ApiModelProperty(value = "市场商户编号")
    private String mktMrchId;
    /**
     * 市场商户名称
     */
    @ApiModelProperty(value = "市场商户名称")
    private String mktMrchNm;
    /**
     * 交易完成时间
     */
    @ApiModelProperty(value = "交易完成时间")
    private String txnDt;
    /**
     * 支付流水号
     */
    @ApiModelProperty(value = "支付流水号")
    private String ordrNo;
    /**
     * 订单类型代码 1支付 2退款
     */
    @ApiModelProperty(value = "订单类型代码 1支付 2退款")
    private String ordrTpcd;
    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal txnAmt;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal hdCg;

    /**
     * 收款状态代码 00-初始，01-对平，03-交易金额不一致，04-平台多，05-收单多
     */
    @ApiModelProperty(value = "收款状态代码 ")
    private String rcnclRsltStcd;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    private String txnTpDsc;

    /**
     * 发卡行
     */
    @ApiModelProperty(value = "发卡行")
    private String lssubnkDsc;

    /**
     * 卡种
     */
    @ApiModelProperty(value = "卡种")
    private String pyCrdtpDsc;

    /**
     * 卡种
     */
    @ApiModelProperty(value = "卡号")
    private String pyrAccNo;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private String txnTm;

    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String mainOrdrNo;

    /**
     * 客户方退款流水号
     */
    @ApiModelProperty(value = "客户方退款流水号")
    private String custRfndTrcno;

    /**
     * 付款人账号匹配结果
     */
    @ApiModelProperty(value = "付款人账号匹配结果")
    private String matchPayerAcctRslt;

    /**
     * 付款人户名匹配结果
     */
    @ApiModelProperty(value = "付款人户名匹配结果")
    private String matchPayerNameRslt;
}
