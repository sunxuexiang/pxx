package com.wanmi.sbc.pay.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 对账单分账明细列表 VO
 * @author hudong
 * @date 2023-09-19 11:12:49
 */
@ApiModel
@Data
public class CcbStatementDetVO implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private Date clrgDt;

    /**
     * 分摊手续费
     */
    @ApiModelProperty(value = "分摊手续费")
    private BigDecimal hdcgAmt;

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
