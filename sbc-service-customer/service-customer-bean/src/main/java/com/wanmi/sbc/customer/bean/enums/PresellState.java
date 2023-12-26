package com.wanmi.sbc.customer.bean.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商家预售状态枚举类
 */
@ApiEnum
public enum PresellState {

    @ApiEnumProperty("0：关闭")
    CLOSE,
    @ApiEnumProperty("1：开启")
    OPEN;

    @JsonCreator
    public PresellState fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
