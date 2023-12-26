package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by yang on 2019/11/10.
 */
public enum ResourceType {
    IMAGE, VIDEO, SHORT_VIDEO, LOGO,EXCEL,USER_VIDEO;
    @JsonCreator
    public static ResourceType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
