package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum PayType {
	@ApiEnumProperty("0：线上二维码")
	ONLINE,
	@ApiEnumProperty("1：鲸币")
	COIN,
	@ApiEnumProperty("2：线上小程序")
	ONLINE_XCX;
	
	
    @JsonCreator
    public static PayType fromValue(int value) {
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
