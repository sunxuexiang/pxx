package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/13
 */
public enum SizeType {

    @ApiEnumProperty("0：非全屏")
    FRAME,
    @ApiEnumProperty("1：全屏")
    FULL_SCREEN;

    @JsonCreator
    public SizeType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
