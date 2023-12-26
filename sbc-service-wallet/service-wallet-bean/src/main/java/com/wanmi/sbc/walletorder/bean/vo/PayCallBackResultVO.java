package com.wanmi.sbc.walletorder.bean.vo;

import com.wanmi.sbc.wallet.bean.enums.PayWalletCallBackType;
import com.wanmi.sbc.walletorder.bean.enums.PayWalletCallBackResultStatus;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PayCallBackResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String businessId;

    /**
     * 回调结果xml内容
     */
    @ApiModelProperty(value = "回调结果xml内容")
    private String resultXml;

    /**
     * 回调结果内容
     */
    @ApiModelProperty(value = "回调结果内容")
    private String resultContext;

    /**
     * 结果状态，0：待处理；1:处理中 2：处理成功；3：处理失败
     */
    @ApiModelProperty(value = "结果状态，0：待处理；1:处理中 2：处理成功；3：处理失败")
    private PayWalletCallBackResultStatus resultStatus;

    /**
     * 处理失败次数
     */
    @ApiModelProperty(value = "处理失败次数")
    private Integer errorNum;

    /**
     * 支付方式，0：微信；1：支付宝；2：银联
     */
    @ApiModelProperty(value = "支付方式，0：微信；1：支付宝；2：银联")
    private PayWalletCallBackType payType;
}