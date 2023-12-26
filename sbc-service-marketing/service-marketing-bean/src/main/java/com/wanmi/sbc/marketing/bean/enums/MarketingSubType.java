package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

@ApiEnum
public enum MarketingSubType {
    /**
     * 满金额减
     */
    @ApiEnumProperty("0：满金额减")
    REDUCTION_FULL_AMOUNT,

    /**
     * 满数量减
     */
    @ApiEnumProperty("1：满数量减")
    REDUCTION_FULL_COUNT,

    /**
     * 满金额折
     */
    @ApiEnumProperty("2：满金额折")
    DISCOUNT_FULL_AMOUNT,

    /**
     * 满数量折
     */
    @ApiEnumProperty("3：满数量折")
    DISCOUNT_FULL_COUNT,

    /**
     * 满金额赠
     */
    @ApiEnumProperty("4：满金额赠")
    GIFT_FULL_AMOUNT,

    /**
     * 满数量赠
     */
    @ApiEnumProperty("5：满数量赠")
    GIFT_FULL_COUNT,

    /**
     * 满订单赠
     */
    @ApiEnumProperty("6：满订单赠")
    GIFT_FULL_ORDER,

    /**
     * 满订单减（满x件减x元）
     */
    @ApiEnumProperty("7：满订单减")
    REDUCTION_FULL_ORDER,

    /**
     * 满订单减（满x件打x折）
     */
    @ApiEnumProperty("8：满订单折")
    DISCOUNT_FULL_ORDER,

    /**
     * 套装购买（买X套增X件商品）
     */
    @ApiEnumProperty("9：套装购买")
    SUIT_TO_BUY;

    @JsonCreator
    public static MarketingSubType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
