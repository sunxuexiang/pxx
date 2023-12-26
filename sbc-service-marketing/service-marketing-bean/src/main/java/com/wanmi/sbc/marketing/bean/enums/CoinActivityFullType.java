package com.wanmi.sbc.marketing.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;


/**
 * 指定商品赠金币类型
 * @author Administrator
 */

@ApiEnum
public enum CoinActivityFullType {


    @ApiEnumProperty("0:任买返")
    ANY_ONE,
    
    @ApiEnumProperty("1:商品总额返")
    REAL_GOODS_PRICE
    ;

    @JsonCreator
    public CoinActivityFullType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
