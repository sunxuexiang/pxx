package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CupsPayRefundResponse implements Serializable {


    @ApiModelProperty(value = "商户号")
    private String mid;

    @ApiModelProperty(value = "退款状态")
    private String refundStatus;

    @ApiModelProperty(value = "")
    private String billDate;

    @ApiModelProperty(value = "终端号")
    private String tid;

    @ApiModelProperty(value = "")
    private String instMid;

    @ApiModelProperty(value = "退货订单号")
    private String refundOrderId;

    @ApiModelProperty(value = "目标系统退货订单号")
    private String refundTargetOrderId;

    @ApiModelProperty(value = "报文应答时间")
    private String responseTimestamp;

    @ApiModelProperty(value = "平台错误码")
    private String errCode;

    @ApiModelProperty(value = "交易状态")
    private String status;

    @ApiModelProperty(value = "商户订单号")
    private String merOrderId;

    @ApiModelProperty(value = "总退款金额")
    private String refundAmount;

    @ApiModelProperty(value = "平台错误信息")
    private String errMsg;

}
