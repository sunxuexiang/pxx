package com.wanmi.sbc.wallet.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 支付方式 0 在线支付 1线下支付 2余额支付
 * Created by zhangjin on 2017/4/25.
 */
@ApiEnum(dataType = "java.lang.String")
public enum  PayType {
    @ApiEnumProperty("0：在线支付")
    ONLINE("在线支付"),

    @ApiEnumProperty("1：线下支付")
    OFFLINE("线下支付"),

    @ApiEnumProperty("2：余额支付")
    BALANCER("余额支付");

    private String desc;

    PayType(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static PayType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
