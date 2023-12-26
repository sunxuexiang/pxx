package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 竞价类型
 * 0 - 关键字      1 - 分类
 * Created by zhangjin on 2017/3/22.
 */
@ApiEnum
public enum BiddingType {

    @ApiEnumProperty("0: 关键字")
    KEY_WORDS_TYPE,
    @ApiEnumProperty("1: 分类")
    CATE_WORDS_TYPE;
    
    @JsonCreator
    public BiddingType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
