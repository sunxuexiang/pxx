package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @program: sbc_h_tian
 * @description: 补货状态
 * @author: Mr.Tian
 * @create: 2020-05-27 10:16
 **/
@ApiEnum
public enum ReplenishmentFlag {
    /**
     * 0:未补货
     */
    @ApiEnumProperty("0:暂未补齐")
    NO,
    /**
     * 1:已补货
     */
    @ApiEnumProperty("1:已经补齐")
    YES,
    /**
     * 2：缺货提醒
     */
    @ApiEnumProperty("2:缺货提醒")
    NOT_ALERT;

    @JsonCreator
    public static ReplenishmentFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public static String getName(ReplenishmentFlag flag) {
        if (flag == ReplenishmentFlag.NO) {
            return "未补货";
        } else if (flag == ReplenishmentFlag.YES) {
            return "已补货";
        } else if (flag == ReplenishmentFlag.NOT_ALERT) {
            return "缺货提醒";
        }
        return "";
    }
}
