package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 金币活动
 * @author Administrator
 */
@ApiEnum(dataType = "java.lang.String")
public enum CoinActivityType {
    /**
     * 指定商品赠金币
     */
    @ApiEnumProperty("0：指定商品赠金币")
    SPECIFIC("指定商品赠金币"),
    
    @ApiEnumProperty("1：订单赠金币")
    ORDER ("订单赠金币")

    ;

    CoinActivityType(String desc) {
        this.desc = desc;
    }

    /**
     * 描述信息
     */
    private String desc;

    @JsonCreator
    public static CoinActivityType fromValue(int value) {
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
