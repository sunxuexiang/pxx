package com.wanmi.sbc.pay.api.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CmbPayRefundDataRequest implements Serializable {

    /**
     *商户发起该请求的当前时间，精确到秒。 格式：yyyyMMddHHmmss
     */
    private String dateTime;

    /**
     *商户分行号，4位数字
     */
    private String branchNo;

    /**
     *商户号，6位数字
     */
    private String merchantNo;

    /**
     *商户订单日期，支付时的订单日期
     * 格式：yyyyMMdd
     */
    private String date;

    /**
     *商户订单号，支付时的订单号
     */
    private String orderNo;

    /**
     *退款流水号,商户生成，同一笔订单内，同一退款流水号只能退款一次。可用于防重复退款。
     */
    private String refundSerialNo;

    /**
     *退款金额,格式xxxx.xx
     */
    private String amount;

    /**
     *退款描述
     */
    private String desc;

//    /**
//     *商户结账系统的操作员号，选填，若填了则会对操作员号和密码进行校验，若不填则不校验。
//     * 注意：2019.01.01以后接入的商户默认“不填”
//     */
//    private String operatorNo;
//
//    /**
//     *操作员密码加密算法,RC4：使用RC4算法对操作员密码进行加密，加密密钥为支付密钥。DES：使用DES算法对操作员密码进行加密，加密密钥为商户支付密钥的前8位，不足8位则右补0。空：默认不加密。
//     * 注意：2019.01.01以后接入的商户默认“不填
//     */
//    private String encrypType;
//
//    /**
//     *旧结账系统的操作员登录密码。使用encrypType算法加密后的密码，加密后的密文必须转换为16进制字符串表示。
//     * 注意：2019.01.01以后接入的商户默认“不填
//     */
//    private String pwd;
//
//    /**
//     *退款标识字段
//     * 空/“A”：按照订单金额发起退款（适用于自动清算/手工清算优惠交易）
//     */
//    private String refundMode;
}
