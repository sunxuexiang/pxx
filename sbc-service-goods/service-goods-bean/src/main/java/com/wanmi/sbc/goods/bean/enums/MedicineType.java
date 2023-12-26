package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商品类型 0 物料 1 药品  2 非药品
 * Created by xw
 */
@ApiEnum
public enum MedicineType {
    @ApiEnumProperty("0：物料")
    MATERIEL,

    @ApiEnumProperty("1：药品")
    OTC,

    @ApiEnumProperty("2：非药品")
    NON_DRUG;

    @JsonCreator
    public MedicineType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
