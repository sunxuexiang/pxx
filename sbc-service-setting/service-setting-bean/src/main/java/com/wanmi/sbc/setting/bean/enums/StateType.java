package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/19
 */
public enum StateType {

    @ApiEnumProperty("0：上架")
    PUT_SHELF,
    @ApiEnumProperty("1：下架")
    OFF_SHELF;

    @JsonCreator
    public StateType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
