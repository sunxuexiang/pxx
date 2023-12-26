package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @desc  物流类型
 * @author shiy  2023/9/13 9:14
*/
@AllArgsConstructor
@NoArgsConstructor
public enum LogisticsType {

    @ApiEnumProperty("0：第三方物流")
    THIRD_PARTY_LOGISTICS("托运部"),
    @ApiEnumProperty("1：指定物流")
    SPECIFY_LOGISTICS("指定专线");

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public static LogisticsType getByValue(int value) {
        return values()[value];
    }

    @JsonCreator
    public LogisticsType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
