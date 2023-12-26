package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 终端: 0 pc 1 h5 2 app
 * Created by ruilinxin
 */
@ApiEnum
public enum TerminalType {

    @ApiEnumProperty("PC")
    PC,
    @ApiEnumProperty("H5")
    H5,
    @ApiEnumProperty("APP")
    APP;

    @JsonCreator
    public static TerminalType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
