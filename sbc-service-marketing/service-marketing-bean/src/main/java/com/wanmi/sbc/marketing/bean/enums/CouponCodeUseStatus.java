package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.util.Arrays;

/**
 * 优惠券使用状态，0(未使用)，1(已使用)，2(已过期)
 * 未使用、已使用、已过期
 */
@ApiEnum
public enum CouponCodeUseStatus {

    UN_USE(0,"未使用"),

    USED(1,"已使用"),

    EXPIRE(2,"已过期"),

    ;

    private int type;

    private String name;

    CouponCodeUseStatus(int type,String name) {
        this.type = type;
        this.name = name;
    }

    @JsonCreator
    public static CouponCodeUseStatus fromValue(int value) {
        return Arrays.stream(values()).filter(f->f.type == value).findAny().orElse(null);
    }

    @JsonValue
    public int toValue() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static String getName(CouponCodeUseStatus status) {
        return status.getName();
    }

}
