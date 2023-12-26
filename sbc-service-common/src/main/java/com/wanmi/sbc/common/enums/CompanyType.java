package com.wanmi.sbc.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * Created by hht on 2017/12/6.
 */
@ApiEnum
public enum CompanyType {

    @ApiEnumProperty("0:平台自营")
    PLATFORM,

    @ApiEnumProperty("1:第三方商家")
    SUPPLIER,

    @ApiEnumProperty("2:统仓统配")
    UNIFIED,

    @ApiEnumProperty("3：零售超市")
    RETAIL,

    @ApiEnumProperty("4：新散批")
    BULK;

    @JsonCreator
    public static CompanyType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public static boolean isSelf(CompanyType companyType) {
        return CompanyType.PLATFORM.equals(companyType) || CompanyType.UNIFIED.equals(companyType);
    }
}
