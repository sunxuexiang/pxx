package com.wanmi.sbc.pay.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * <p>退款来源类型</p>
 */
@ApiEnum
public enum RefundSourceType {

    @ApiEnumProperty("取消订单")
    CANCEL_ORDER,

    @ApiEnumProperty("退货退款")
    RETURN_ORDER;

    @JsonCreator
    public RefundSourceType fromValue(String name) {
        return valueOf(name);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}