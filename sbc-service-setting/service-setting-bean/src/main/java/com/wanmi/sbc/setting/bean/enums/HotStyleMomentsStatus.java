package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @description: 爆款时刻状态枚举类&查询类型，0：全部，1：进行中，2：暂停中，3：未开始，4：已结束，5：进行中&未开始，6：终止
 * @author: XinJiang
 * @time: 2022/5/9 19:59
 */
@ApiEnum
public enum HotStyleMomentsStatus {

    @ApiEnumProperty("0：全部")
    ALL,

    @ApiEnumProperty("1：进行中")
    STARTED,

    @ApiEnumProperty("2：暂停中")
    PAUSED,

    @ApiEnumProperty("3：未开始")
    NOT_START,

    @ApiEnumProperty("4：已结束")
    ENDED,

    @ApiEnumProperty("5：进行中&未开始")
    S_NS,

    @ApiEnumProperty("6：终止")
    TERMINATION;

    @JsonCreator
    public HotStyleMomentsStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
