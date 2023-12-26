package com.wanmi.sbc.pay.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/8/18 9:39
 */
@ApiEnum
public enum CcbSubOrderType {

    @ApiEnumProperty("0:商家")
    MERCHANT,
    @ApiEnumProperty("1:佣金")
    COMMISSION,
    @ApiEnumProperty("2:运费")
    FREIGHT,
    @ApiEnumProperty("3:运费佣金")
    FREIGHT_COMMISSION,
    @ApiEnumProperty("4:附加运费")
    EXTRA,
    ;
    @JsonCreator
    public static CcbSubOrderType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
