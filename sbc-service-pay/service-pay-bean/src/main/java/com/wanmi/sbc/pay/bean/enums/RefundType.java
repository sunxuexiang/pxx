package com.wanmi.sbc.pay.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * <p>退款类型</p>
 */
@ApiEnum
public enum RefundType {

    @ApiEnumProperty("在线退")
    ONLINE,

    @ApiEnumProperty("余额退")
    BALANCE;

    @JsonCreator
    public RefundType fromValue(String name) {
        return valueOf(name);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}