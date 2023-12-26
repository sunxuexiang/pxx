package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum MaterialType {
	@ApiEnumProperty("0：图片")
	IMG,
	@ApiEnumProperty("1：视频")
	VIDEO;
	
    @JsonCreator
    public static MaterialType fromValue(int value) {
        try {
			return values()[value];
		} catch (Exception e) {
			return null;
		}
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
	
}
