package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @author caofang
 * @date 2020/4/27 13:50
 * @Description 商品推荐开关
 */
@ApiEnum
public enum GoodsRecommendStatus {

    @ApiEnumProperty("0：开启")
    OPEN,

    @ApiEnumProperty("1: 关闭")
    CLOSE;

    @JsonCreator
    public GoodsRecommendStatus fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
