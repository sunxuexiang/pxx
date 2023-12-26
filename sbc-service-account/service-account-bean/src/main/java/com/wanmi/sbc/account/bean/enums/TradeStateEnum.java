package com.wanmi.sbc.account.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @Description: 钱包交易记录交易状态枚举类
 * @author: jiangxin
 * @create: 2021-11-02 19:36
 * 交易状态 0 未支付 1 待确认 2 已支付
 */
@ApiEnum
public enum TradeStateEnum {
    @ApiEnumProperty("0:未支付")
    NOT_PAID("未支付"),

    @ApiEnumProperty("1:待确认")
    UNCONFIRMED("待确认"),

    @ApiEnumProperty("2:已支付")
    PAID("已支付");

    private String desc;

    TradeStateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static TradeStateEnum fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
