package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CupsPaySignRequest {

    /**
     * 银联支付回调结果
     */
    @ApiModelProperty(value = "银联支付回调结果")
    private String cupsPayCallBackResultStr;

    /**
     * secret key
     */
    private String secret;

    private String appId;

    private String apiKey;
}
