package com.wanmi.sbc.wallet.bean.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 鲸币是否开启状态
 */
@ApiEnum
public enum JingBiState {
    @ApiEnumProperty("0: 未开通")
    NO,
    @ApiEnumProperty("1：开启")
    OPEN,
    @ApiEnumProperty("2：关闭")
    CLOSE
    ;

    @JsonCreator
    public JingBiState fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
