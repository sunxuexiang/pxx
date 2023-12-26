package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CmbPayOrderResponse implements Serializable {

    @ApiModelProperty(value = "商品描述")
    private String body;                //商品描述

    /**
     * 用Base64编码的招行公钥
     */
    private String fbPubKey;

    /**
     * 参数编码
     */
    private String charset;

    /**
     * 接口版本号
     */
    private String version;

    /**
     * 报文签名
     */
    private String sign;

    /**
     * signType
     */
    private String signType;

    /**
     * 请求数据
     */
    private String reqData;
}
