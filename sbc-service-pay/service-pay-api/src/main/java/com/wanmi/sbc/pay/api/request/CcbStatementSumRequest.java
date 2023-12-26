package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 建行对账单汇总查询请求类
 * @author hudong
 * 2023-09-04
 */
@Data
@ApiModel
public class CcbStatementSumRequest {

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
    private String  clrgDt;

    /**
     * 商家自定义名称
     */
    @ApiModelProperty(value = "商家自定义名称")
    private String udfId;


}
