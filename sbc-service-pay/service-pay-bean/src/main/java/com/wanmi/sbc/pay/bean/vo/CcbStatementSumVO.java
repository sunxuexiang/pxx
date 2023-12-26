package com.wanmi.sbc.pay.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 对账单列表 VO
 * @author hudong
 * @date 2023-09-11 11:12:49
 */
@ApiModel
@Data
public class CcbStatementSumVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 分账交易流水号
     */
    @ApiModelProperty(value = "分账交易流水号")
    private String clrgTxnsrlno;

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
     * 分账金额
     */
    @ApiModelProperty(value = "分账金额")
    private BigDecimal toClrgMmt;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refMmt;

    /**
     * 平台手续费
     */
    @ApiModelProperty(value = "平台手续费")
    private BigDecimal platHf;

    /**
     * 分账失败原因
     */
    @ApiModelProperty(value = "分账失败原因")
    private String rspInf;

    /**
     * 分账状态代码
     */
    @ApiModelProperty(value = "分账状态代码")
    private String clrgStcd;

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
    private BigDecimal tradeMmt;

    /**
     * 商家自定义名称
     */
    @ApiModelProperty(value = "商家自定义名称")
    private String udfId;

    /**
     * 分账状态
     */
    @ApiModelProperty(value = "分账状态")
    private Integer clrgStatus;

    /**
     * 分账账户
     */
    @ApiModelProperty(value = "分账账户")
    private String clrgAccout;

}
