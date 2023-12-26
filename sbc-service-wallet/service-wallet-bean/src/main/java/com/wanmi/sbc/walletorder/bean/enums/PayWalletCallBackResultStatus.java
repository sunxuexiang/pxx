package com.wanmi.sbc.walletorder.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

@ApiEnum
public enum PayWalletCallBackResultStatus {

    @ApiEnumProperty("0: 待处理")
    TODO,

    @ApiEnumProperty("1: 处理中")
    HANDLING,

    @ApiEnumProperty("2: 处理成功")
    SUCCESS,

    @ApiEnumProperty("3: 处理失败")
    FAILED;

    @JsonCreator
    public PayWalletCallBackResultStatus fromValue(int value){
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
