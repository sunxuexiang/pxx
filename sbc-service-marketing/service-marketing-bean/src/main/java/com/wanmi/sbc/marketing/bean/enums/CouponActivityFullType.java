package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 优惠券赠券满赠条件[购买指定商品赠券]
 * 0 全部满足赠，1 满足任意一个赠
 * @author MarsJiang
 */
@ApiEnum
public enum CouponActivityFullType {

    @ApiEnumProperty("0:购所有赠")
    ALL,

    @ApiEnumProperty("1:购任一赠")
    ANY_ONE,

    @ApiEnumProperty("2:满金额赠")
    FULL_AMOUNT,

    @ApiEnumProperty("3:满数量赠")
    FULL_COUNT;

    @JsonCreator
    public CouponActivityFullType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
