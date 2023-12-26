package com.wanmi.sbc.pay.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * <p>支付网关枚举</p>
 * Created by of628-wenzhi on 2017-08-04-下午10:25.
 */
@ApiEnum
public enum PayGatewayEnum {
    /**
     * 银联 0
     */
    @ApiEnumProperty("银联")
    UNIONPAY,

    /**
     * 微信 1
     */
    @ApiEnumProperty("微信")
    WECHAT,

    /**
     * 支付宝 2
     */
    @ApiEnumProperty("支付宝")
    ALIPAY,

    /**
     * 银联b2b 3
     */
    @ApiEnumProperty("银联b2b")
    UNIONB2B,

    /**
     * 拼++ 4
     */
    @ApiEnumProperty("拼++")
    PING,

    /**
     * 微信代付 5
     */
    @ApiEnumProperty("微信代付")
    REPLACEWECHAT,

    /**
     * 支付宝代付 6
     */
    @ApiEnumProperty("支付宝代付")
    REPLACEALIPAY,

    /**
     * 余额支付 7
     */
    @ApiEnumProperty("余额支付")
    BALANCE,
    /**
     * 招商 8
     */
    @ApiEnumProperty("招商")
    CMB,
    /**
     * 银联支付宝 9
     */
    @ApiEnumProperty("银联支付宝")
    CUPSALI,
    /**
     * 银联微信 10
     */
    @ApiEnumProperty("银联微信")
    CUPSWECHAT,

    /**
     * 建行支付 11
     */
    @ApiEnumProperty("建行支付")
    CCB;

    @JsonCreator
    public PayGatewayEnum fromValue(String name) {
        return PayGatewayEnum.valueOf(name);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
