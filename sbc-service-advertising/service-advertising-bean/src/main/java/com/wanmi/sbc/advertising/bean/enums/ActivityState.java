package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum ActivityState {

	@ApiEnumProperty("0：待支付")
	CREATED(0),

	@ApiEnumProperty("10：待审核")
	PAID(10),

	@ApiEnumProperty("20：待生效")
	AUDIT_PASS(20),

	@ApiEnumProperty("30：已生效")
	SUCCESS(30),
	
	@ApiEnumProperty("31：已投放(待生效,已生效)，查询使用")
	RELEASED(31),

	@ApiEnumProperty("40：已驳回")
	AUDIT_REJECTED(40),

	@ApiEnumProperty("50：已取消")
	CANCELLED(50),

	@ApiEnumProperty("100：已过期")
	COMPLETED(100);

	private int state;

	ActivityState(int state) {
		this.state = state;
	}

	@JsonCreator
	public static ActivityState fromValue(int value) {
		for (ActivityState st : values()) {
			if (st.state == value) {
				return st;
			}
		}
		return null;
	}

	@JsonValue
	public int toValue() {
		return this.state;
	}

}
