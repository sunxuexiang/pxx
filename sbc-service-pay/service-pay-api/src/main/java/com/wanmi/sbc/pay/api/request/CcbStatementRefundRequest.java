package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 建行对账单退款查询请求类
 * @author hudong
 * 2023-09-04
 */
@Data
@ApiModel
public class CcbStatementRefundRequest {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;


    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String pyOrdrNo;

    /**
     * 扣减金额
     */
    @ApiModelProperty(value = "扣减金额")
    private BigDecimal tfrAmt;

    /**
     * 扣减账号
     */
    @ApiModelProperty(value = "扣减账号")
    private String pyrAccNo;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String prjId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String prjNm;

    /**
     * 扣减状态代码
     */
    @ApiModelProperty(value = "扣减状态代码")
    private String tfrStcd;

    /**
     * 扣减日期
     */
    @ApiModelProperty(value = "扣减日期")
    private String tfrDt;

    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String mainOrdrNo;
}
