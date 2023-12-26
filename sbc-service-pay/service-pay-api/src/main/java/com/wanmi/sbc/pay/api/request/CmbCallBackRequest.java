package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "支付请求对象")
public class CmbCallBackRequest extends BaseRequest {

    /**
     * 参数编码,固定为”UTF-8”
     */
    private String charset;
    /**
     * 报文签名,使用招行私钥对noticeData内的数据进行签名；商户需使用招行公钥验签。
     */
    private String sign;
    /**
     * 签名算法,固定为”RSA”
     */
    private String signType;
    /**
     * 接口版本号,固定为”1.0”
     */
    private String version;
    /**
     * 应答数据
     */
    private CmbNoticeDataRequest noticeData;
}
