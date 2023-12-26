package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

@ApiEnum
public enum ForcePileFlag {
    @ApiEnumProperty("未开启囤货")
    CLOSE,
    @ApiEnumProperty("优先囤货")
    PILE,
    @ApiEnumProperty("强制囤货")
    FORCEPILE;
    @JsonCreator
    public static ForcePileFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
