package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商品分类
 * 0散装 1定量
 * Created by wangzhiqi on 2021/7/31.
 */
@ApiEnum
public enum CategoriesStatus {

    @ApiEnumProperty("0：散装")
    ZERO,

    @ApiEnumProperty("1：定量")
    ONE;

    @JsonCreator
    public CategoriesStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
