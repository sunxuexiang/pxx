package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @Author: chenchang
 * @Date: 2022/09/06
 * @Description: 囤货类型，1 全款囤货
 */
@ApiEnum
public enum PileActivityType {
    /**
     * 指定赠券
     */
    @ApiEnumProperty("0：全款囤货")
    FULL_PAID_PILE(0);

    private int type;

    PileActivityType(int type) {
        this.type = type;
    }

    @JsonCreator
    public static PileActivityType fromValue(int value) {
        try {
            return values()[value];
        } catch (Exception ex) {
            return null;
        }
    }

    @JsonValue
    public int toValue() {
        return this.type;
    }

}
