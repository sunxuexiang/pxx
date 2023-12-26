package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 是否默认,0否1是
 * Created by daiyitian on 2017/3/22.
 */
@ApiEnum
public enum DefaultFlag {
    @ApiEnumProperty("否")
    NO,
    @ApiEnumProperty("是")
    YES,
    @ApiEnumProperty("散批")
    SP,
    @ApiEnumProperty("拆箱散批")
    CP,
    @ApiEnumProperty("战点自提")
    ZD;
    @JsonCreator
    public static DefaultFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
