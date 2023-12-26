package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @description: 广告位类型 0-通栏推荐位，1-分栏推荐位，2-轮播推荐位
 * @author: MarJiang
 * @time: 2022-02-18 9:35:22
 */
public enum AdvertisingType {

    @ApiEnumProperty("0：通栏推荐位")
    BANNER,

    @ApiEnumProperty("1：分栏推荐位")
    COLUMNS,

    @ApiEnumProperty("2: 轮播推荐位")
    SHUFFLING;

    @JsonCreator
    public static AdvertisingType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
