package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/27 10:08
 */
@ApiEnum
public enum AdvertisingConfigType {

    @ApiEnumProperty("0海报页")
    POSTER,
    @ApiEnumProperty("1不跳转")
    NONE,
    @ApiEnumProperty("2直播页")
    LIVE,
    @ApiEnumProperty("3拆箱散批")
    CP,
    @ApiEnumProperty("4促销商家")
    MARKETING,
    @ApiEnumProperty("5新入驻商家")
    NEW,
    @ApiEnumProperty("6商家广告位")
    MERCHANT
    ;
    @JsonCreator
    public static AdvertisingConfigType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
