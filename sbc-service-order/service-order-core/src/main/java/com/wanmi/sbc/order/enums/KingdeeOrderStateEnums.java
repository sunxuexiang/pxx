package com.wanmi.sbc.order.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 金蝶订单状态枚举
 *
 * @author yitang
 * @version 1.0
 */
@ApiEnum
public enum KingdeeOrderStateEnums {
    /**
     * 未付款
     */
    @ApiEnumProperty("未付款")
    NOTPAYING("01"),

    /**
     * 已付款
     */
    @ApiEnumProperty("已付款")
    PAYMENTHASBEEN("02");

    private final String value;

    KingdeeOrderStateEnums(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

}
