package com.wanmi.sbc.pay.api.request;

import lombok.*;

/**
 * @program: service-pay
 * @description: 招商支付退款接口请求参数
 * @create: 2019-02-15 11:10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CmbPayRefundRequest extends  PayBaseRequest{

    /**
     *接口版本号,固定为“2.0”
     */
    private String version;

    /**
     *参数编码,固定为“UTF-8”
     */
    private String charset;

    /**
     *报文签名,对reqData内的数据进行签名
     */
    private String sign;

    /**
     *签名算法,固定为“SHA-256”
     */
    private String signType;

    /**
     *请求数据
     */
    private CmbPayRefundDataRequest reqData;

}
