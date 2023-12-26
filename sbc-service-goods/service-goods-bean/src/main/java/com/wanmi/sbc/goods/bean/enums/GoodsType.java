package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商品类型，0：实体商品，1：虚拟商品
 * @author wanggang
 */
@ApiEnum
public enum GoodsType {

    @ApiEnumProperty("0：实体商品")
    REAL_GOODS,

    @ApiEnumProperty("1: 虚拟商品")
    VIRTUAL_GOODS;
    @JsonCreator
    public GoodsType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
