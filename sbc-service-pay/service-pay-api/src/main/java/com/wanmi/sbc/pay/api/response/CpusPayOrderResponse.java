package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CpusPayOrderResponse implements Serializable {


    @ApiModelProperty(value = "商品描述")
    private String body;                //商品描述

    private String totalAmount;

    private String responseTimestamp;

    private String errCode;

    private String merName;

    private String mid;

    private String appPayRequest;

    private String settleRefId;

    private String tid;

    private String qrCode;

    private String targetMid;

    private String targetStatus;

    private String seqId;

    private String merOrderId;

    private String status;

    private String targetSys;

    private String connectSys;

    private String delegatedFlag;

    private String miniPayRequest;

}
