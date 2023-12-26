package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
    * @Description:  自提枚举类
    * @Param:
    * @return:
    * @Author: Mr.Tian
    * @Date: 2020/5/21
    */
@ApiEnum
public enum PickUpFlag {
    @ApiEnumProperty("0:否")
    NO,
    @ApiEnumProperty("1:是")
    YES;

    @JsonCreator
    public static PickUpFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
