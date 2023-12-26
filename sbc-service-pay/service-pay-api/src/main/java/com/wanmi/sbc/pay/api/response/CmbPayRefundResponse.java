package com.wanmi.sbc.pay.api.response;

import com.wanmi.sbc.common.base.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmbPayRefundResponse implements Serializable {


    @ApiModelProperty(value = "接口版本号,固定为2.0")
    private String version;

    @ApiModelProperty(value = "参数编码,固定为UTF-8")
    private String charset;

    @ApiModelProperty(value = "报文签名,使用商户支付密钥对rspData内的数据进行验签")
    private String sign;

    @ApiModelProperty(value = "签名算法,固定为SHA-256")
    private String signType;

    @ApiModelProperty(value = "请求数据")
    private CmbPayRefundDataResponse rspData;

}
