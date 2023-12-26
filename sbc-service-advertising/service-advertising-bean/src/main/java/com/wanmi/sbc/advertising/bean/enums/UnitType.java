package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum UnitType {
	// 单位 天
	@ApiEnumProperty("0：元/天")
	DAY,
	// 单位 千次
	@ApiEnumProperty("1：千次")
	THOUSAND;
	
    @JsonCreator
    public static UnitType fromValue(int value) {
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
