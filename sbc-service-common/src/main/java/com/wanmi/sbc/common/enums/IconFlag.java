package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 桌面图标类型 0默认 1囤货 2年货
 * @author jiangxin
 */
public enum IconFlag {

    @ApiEnumProperty("默认图标")
    DEFAULT,
    @ApiEnumProperty("囤货")
    STOCKUP,
    @ApiEnumProperty("年货")
    NECESSITIES;

    @JsonCreator
    public IconFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
