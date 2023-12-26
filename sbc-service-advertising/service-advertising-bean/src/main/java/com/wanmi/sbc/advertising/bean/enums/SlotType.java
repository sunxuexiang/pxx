package com.wanmi.sbc.advertising.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

public enum SlotType{

	@ApiEnumProperty("0：开屏广告位")
	BOOT_PAGE(0),

	@ApiEnumProperty("1：首页banner广告位")
	HOME_PAGE_BANNER(1),
	
	@ApiEnumProperty("2：批发市场下商城广告位 不再使用")
	MALL_STORE_LIST(2),
	
	@ApiEnumProperty("3：批发市场下商品广告位")
	MALL_GOOODS_LIST(3),
	
	
	@ApiEnumProperty("100：启动页图片")
	BOOTSTRAP_IMG(100),
	
	@ApiEnumProperty("999：其他广告位")
	OTHER(999)
	;
	
	
	
    private int type;

    SlotType(int type){
        this.type = type;
    }

    @JsonCreator
    public static SlotType fromValue(int value) {
        for (SlotType st : values()) {
            if (st.type == value) {
                return st;
            }
        }
        return null;
    }

    @JsonValue
    public int toValue() {
        return this.type;
    }
    



}
