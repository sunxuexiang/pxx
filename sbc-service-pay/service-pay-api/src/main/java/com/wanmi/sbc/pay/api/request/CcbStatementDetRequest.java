package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 建行对账单分账明细请求类
 * @author hudong
 * 2023-09-19
 */
@Data
@ApiModel
public class CcbStatementDetRequest {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;


    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String sn;

    /**
     * 支付流水号
     */
    @ApiModelProperty(value = "支付流水号")
    private String pyOrdrNo;

    /**
     * 收款账号
     */
    @ApiModelProperty(value = "收款账号")
    private String rcvpymtAccNo;

    /**
     * 收款方商家编号
     */
    @ApiModelProperty(value = "收款方商家编号")
    private String rcvprtMktMrchId;

    /**
     * 收款方商家名称
     */
    @ApiModelProperty(value = "收款方商家名称")
    private String rcvprtMktMrchNm;

    /**
     * 分账金额
     */
    @ApiModelProperty(value = "分账金额")
    private BigDecimal clrgAmt;

    /**
     * 分账状态代码 2-分账成功  4-分账异常
     */
    @ApiModelProperty(value = "分账状态代码")
    private String clrgStcd;

    /**
     * 分账日期
     */
    @ApiModelProperty(value = "分账日期")
    private String clrgDt;

    /**
     * 分摊手续费
     */
    @ApiModelProperty(value = "分摊手续费")
    private BigDecimal hdcgAmt;

    /**
     * 子订单编号
     */
    @ApiModelProperty(value = "子订单编号")
    private String subOrdrNo;

    /**
     * 原始分账金额
     */
    @ApiModelProperty(value = "原始分账金额")
    private BigDecimal shldSubaccAmt;

    /**
     * 商家自定义名称
     */
    @ApiModelProperty(value = "商家自定义名称")
    private String udfId;


    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String mainOrdrNo;
}
