package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 药品分类  0：处方  1：OTC(甲类)  2：OTC(乙类)
 */
@ApiEnum
public enum DrugType {
    @ApiEnumProperty("0：处方")
    PRESCRIBE,

    @ApiEnumProperty("1：OTC(甲类)")
    OTC_A,

    @ApiEnumProperty("2：OTC(乙类)")
    OTC_B;

    @JsonCreator
    public DrugType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
