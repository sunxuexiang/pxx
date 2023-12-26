package com.wanmi.sbc.customer.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商家入驻申请处理类型（0：未处理 1：已处理）
 * @author hudong
 * @date 2023-06-20 08:27
 */
@ApiEnum
public enum HandleFlag {
    @ApiEnumProperty("处理类型 0:未处理")
    NO_HANDLE,
    @ApiEnumProperty("处理类型 1:已处理")
    HANDLE;

    @JsonCreator
    public HandleFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
