package com.wanmi.sbc.account.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @Description: 收支类型 0收入，1支出
 * @author: jiangxin
 * @create: 2021-11-02 19:52
 */
@ApiEnum
public enum BudgetType {

    @ApiEnumProperty("0：收入")
    INCOME("收入"),

    @ApiEnumProperty("1：支出")
    EXPENDITURE("支出");

    private String desc;

    BudgetType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static BudgetType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
