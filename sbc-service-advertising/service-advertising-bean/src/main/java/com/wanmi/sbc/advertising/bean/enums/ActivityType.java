package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum ActivityType {
	
	@ApiEnumProperty("0：按照时段收费，包括了按天收费")
	CPT,
	@ApiEnumProperty("1：按照展示次数付费")
	CPM,
	@ApiEnumProperty("2：按照实际点击次数付费")
	CPC,
	@ApiEnumProperty("3：按照下载次数付费")
	CPD;
	
    @JsonCreator
    public static ActivityType fromValue(int value) {
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
