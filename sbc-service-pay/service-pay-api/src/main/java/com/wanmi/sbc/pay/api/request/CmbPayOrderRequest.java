package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CmbPayOrderRequest {

    /**
     * 用户id
     */
    private String customerId;

    @ApiModelProperty(value = "商品描述")
    private String body;                //商品描述

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;        //商户订单号

    @ApiModelProperty(value = "总金额")
    private String total_fee;           //总金额

    @ApiModelProperty(value = "终端IP")
    private String spbill_create_ip;    //终端IP

    @ApiModelProperty(value = "交易类型")
    private String trade_type;
    /**
     * 商户id-boss端取默认值
     */
    @ApiModelProperty(value = "店铺id")
    @NotNull
    private Long storeId;

    /**
     * 订单时间
     */
    private String dateTime;

    /**
     * 订单超时时间
     */
    private String outTime;

    /**
     * 支付渠道
     */
    private Long channelId;

    /**
     * 小程序openId
     */
    private String subOpenId;

    /**
     * 支付单号
     */
    private String payOrderNo;
}
