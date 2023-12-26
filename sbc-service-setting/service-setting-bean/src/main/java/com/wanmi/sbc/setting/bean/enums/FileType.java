package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by yang on 2023/09/08.
 */
public enum FileType {
    ZIP, IMAGE, VIDEO, SHORT_VIDEO, LOGO,EXCEL;
    @JsonCreator
    public static FileType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
