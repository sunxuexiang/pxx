package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 数据类型
 * 0：批发 1零售
 * Created by xiemengnan on 2018/10/22.
 */
@ApiEnum
public enum SaleType {

    @ApiEnumProperty("0：批发")
    WHOLESALE,

    @ApiEnumProperty("1: 零售")
    RETAIL,

    @ApiEnumProperty("2: 散批")
    BULK;

    @JsonCreator
    public SaleType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public static String getName(SaleType flag) {
        if (flag == SaleType.WHOLESALE) {
            return "批发";
        } else if (flag == SaleType.RETAIL) {
            return "零售";
        }else if (flag == SaleType.BULK){
            return "新散批";
        }
        return "";
    }

    public static String getName(int flag) {
        if (flag == SaleType.WHOLESALE.ordinal()) {
            return "批发";
        } else if (flag == SaleType.RETAIL.ordinal()) {
            return "零售";
        }
        else if (flag == SaleType.BULK.ordinal()){
            return "新散批";
        }
        return "";
    }
}
