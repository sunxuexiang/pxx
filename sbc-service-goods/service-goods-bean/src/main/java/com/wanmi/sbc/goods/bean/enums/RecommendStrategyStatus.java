package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 智能推荐策略相关
 */
@ApiEnum
public enum RecommendStrategyStatus {

    @ApiEnumProperty("0：开启")
    OPEN,

    @ApiEnumProperty("1: 关闭")
    CLOSE;


    @JsonCreator
    public RecommendStrategyStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
