package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum ClickJumpType {
	
	@ApiEnumProperty("0：无动作")
	NONE,

	@ApiEnumProperty("1：进入店铺首页")
	INTO_STORE_HOMEPAGE
	;
	
	

	@JsonCreator
	public static ClickJumpType fromValue(int value) {
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
