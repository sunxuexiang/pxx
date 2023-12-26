package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @program: sbc_h_tian
 * @description: 仓库类型枚举
 * @author: Mr.Tian
 * @create: 2020-05-21 15:19
 **/
@ApiEnum
public enum  WareHouseType {
    /**
     * 代表线上仓
     */
    @ApiEnumProperty("0:线上仓")
    ONLINE,
    /**
     * 代表门店仓
     */
    @ApiEnumProperty("1:门店仓")
    STORRWAREHOUSE;

    @JsonCreator
    public static WareHouseType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
