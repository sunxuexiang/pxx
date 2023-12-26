package com.wanmi.sbc.wallet.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

@ApiEnum(dataType = "java.lang.String")
public enum SettleMarketingType {
    /**
     * 满减
     */
    @ApiEnumProperty("满减优惠")
    REDUCTION("满减优惠"),

    /**
     * 满折
     */
    @ApiEnumProperty("满折优惠")
    DISCOUNT("满折优惠"),

    /**
     * 满赠
     */
    @ApiEnumProperty("满赠优惠")
    GIFT("满赠优惠");

    SettleMarketingType(String desc) {
        this.desc = desc;
    }

    /**
     * 描述信息
     */
    private String desc;

    @JsonCreator
    public static SettleMarketingType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public String getDesc() {
        return desc;
    }

}
