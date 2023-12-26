package com.wanmi.sbc.pay.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/15 9:10
 */
@ApiEnum
public enum CcbDelFlag {
    @ApiEnumProperty("0:否")
    NO,
    @ApiEnumProperty("1:是")
    YES,
    @ApiEnumProperty("2:停用")
    STOP,
    @ApiEnumProperty("1:待删除")
    WAIT_DEL
    ;
    @JsonCreator
    public static CcbDelFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
