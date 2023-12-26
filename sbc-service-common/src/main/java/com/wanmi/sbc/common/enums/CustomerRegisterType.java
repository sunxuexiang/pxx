package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 客户类型,0:普通用户  1:商户  2:单位
 */
@ApiEnum
public enum CustomerRegisterType {

    @ApiEnumProperty("0:普通用户")
    COMMON,
    @ApiEnumProperty("1:商户")
    MERCHANT,
    @ApiEnumProperty("2:单位")
    UNIT;

    @JsonCreator
    public static CustomerRegisterType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
