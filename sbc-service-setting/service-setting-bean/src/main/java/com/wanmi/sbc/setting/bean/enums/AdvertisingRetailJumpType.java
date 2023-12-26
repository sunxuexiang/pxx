package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @description: 零售广告跳转类型枚举 0：分类，1：商品
 * @author: XinJiang
 * @time: 2022/4/18 17:17
 */
public enum AdvertisingRetailJumpType {
    @ApiEnumProperty("0：分类")
    CATE,

    @ApiEnumProperty("1：商品")
    GOODS;

    @JsonCreator
    public static AdvertisingRetailJumpType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
