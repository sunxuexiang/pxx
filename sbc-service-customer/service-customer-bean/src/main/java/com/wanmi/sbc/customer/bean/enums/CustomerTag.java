package com.wanmi.sbc.customer.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @author caofang
 * @date 2020/4/28 14:33
 * @Description 会员标签枚举
 */
@ApiEnum
public enum CustomerTag {

    @ApiEnumProperty("0:零食店")
    SNACKS,
    @ApiEnumProperty("1:便利店")
    CONVENIENCE,
    @ApiEnumProperty("2:商超")
    SUPERMARKET,
    @ApiEnumProperty("3:二批商")
    ASSISTANT,
    @ApiEnumProperty("4:水果零售店")
    GREENGROCER,
    @ApiEnumProperty("5:连锁系统")
    CHAIN_SYSTEM,
    @ApiEnumProperty("6:炒货")
    ROASTED_GOODS;

    @JsonCreator
    public CustomerTag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
