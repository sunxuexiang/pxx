package com.wanmi.sbc.customer.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
* 开启状态枚举
*/
@ApiEnum
public enum MallOpenStatus {
    @ApiEnumProperty("0：关闭")
    CLOSE,
    @ApiEnumProperty("1：打开")
    OPEN,;
    @JsonCreator
    public MallOpenStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}

