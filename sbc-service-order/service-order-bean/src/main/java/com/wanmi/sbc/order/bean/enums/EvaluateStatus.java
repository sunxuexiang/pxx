package com.wanmi.sbc.order.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 评价状态
 */
@ApiEnum
public enum EvaluateStatus {

    @ApiEnumProperty("0: 未评价")
    NO_EVALUATE,

    @ApiEnumProperty("1: 已评价")
    HAVE_BEEN_EVALUATE;

    @JsonCreator
    public EvaluateStatus fromValue(int value){
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public static EvaluateStatus getByValue(int value){
        int size =values().length;
        if(value>=size || value<0){
            return null;
        }
        return values()[value];
    }
}
