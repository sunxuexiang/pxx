package com.wanmi.sbc.customer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

@ApiEnum
public enum MergeAccountBeforeStatus {
    @ApiEnumProperty("已关联主账号")
    HAVE_PARENT,
    @ApiEnumProperty("存在子账号")
    HAVE_CHILD,
    @ApiEnumProperty("无关系")
    NONE;

    @JsonCreator
    public MergeAccountBeforeStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
