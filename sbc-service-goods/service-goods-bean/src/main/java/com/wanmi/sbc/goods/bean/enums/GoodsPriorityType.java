package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @author caofang
 * @date 2020/4/27 13:39
 * @Description 商品推荐优先级
 */
@ApiEnum
public enum GoodsPriorityType {

    @ApiEnumProperty("0：最新上架时间")
    ADD_TIME,

    @ApiEnumProperty("1: 关注度")
    ATTENTION,

    @ApiEnumProperty("2: 浏览量")
    PAGE_VIEWS,

    @ApiEnumProperty("3: 销量")
    SALES,

    @ApiEnumProperty("4:按默认")
    DEFAULT,

    @ApiEnumProperty("5:按综合")
    SYNTHESIS;

    @JsonCreator
    public GoodsPriorityType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
