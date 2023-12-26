package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum SlotSeqState{

	@ApiEnumProperty("0：不可选")
	NOT_AVAILABLE("不可选"),

	@ApiEnumProperty("1：可选")
	AVAILABLE("可选"),
	
	@ApiEnumProperty("2：已占")
	OCCUPIED("已占")
	;
	
	private String msg;
	
    SlotSeqState(String msg) {
		this.msg = msg;
	}

	@JsonCreator
    public static SlotSeqState fromValue(int value) {
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

	public String getMsg() {
		return msg;
	}





}
