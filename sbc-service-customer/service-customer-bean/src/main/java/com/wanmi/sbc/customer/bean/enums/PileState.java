package com.wanmi.sbc.customer.bean.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商家囤货状态枚举类
 */
@ApiEnum
public enum PileState {

    @ApiEnumProperty("0：未开启")
    NO_OPEN,
    @ApiEnumProperty("1：已开启")
    OPEN,
    @ApiEnumProperty("2：已关闭")
    CLOSE;

    @JsonCreator
    public PileState fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
