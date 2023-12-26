package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @Author: songhanlin
 * @Date: Created In 10:47 AM 2018/9/12
 * @Description: 优惠券类型
 * 0通用券 1店铺券 2运费券
 */
@ApiEnum
public enum CouponType {

    /**
     * 通用券
     */
    @ApiEnumProperty("0：通用券")
    GENERAL_VOUCHERS(0),

    /**
     * 店铺券
     */
    @ApiEnumProperty("1：店铺券")
    STORE_VOUCHERS(1),

    /**
     * 运费券
     */
    @ApiEnumProperty("2：运费券")
    FREIGHT_VOUCHER(2);

//    @ApiEnumProperty("3：签到券")
//    SIGN_VOUCHERS(3);

    private int type;

    CouponType(int type) {
        this.type = type;
    }

    @JsonCreator
    public static CouponType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.type;
    }
}
