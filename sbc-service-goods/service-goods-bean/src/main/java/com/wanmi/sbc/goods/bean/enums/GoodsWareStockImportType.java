package com.wanmi.sbc.goods.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 商品库存明细导入类型枚举
 * @author wanggang
 */
@ApiEnum
public enum GoodsWareStockImportType {

    @ApiEnumProperty("0: 导入")
    IMPORT,
    @ApiEnumProperty("1: 编辑")
    EDIT,
    @ApiEnumProperty("2: 返还")
    ADD,
    @ApiEnumProperty("3: 下单扣减")
    SUB;
    @JsonCreator
    public GoodsWareStockImportType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
