package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum SlotState{

	@ApiEnumProperty("0：待上架")
	READY_ADDED,

	@ApiEnumProperty("1：已上架")
	ADDED,
	
	@ApiEnumProperty("2：已下架")
	TAKE_OFF
	;
	
    @JsonCreator
    public static SlotState fromValue(int value) {
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
