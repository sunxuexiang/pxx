package com.wanmi.sbc.returnorder.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 采购车配置类型
 *
 * @author yitang
 * @version 1.0
 */
@ApiEnum
public enum ProcurementTypeEnum {
    @ApiEnumProperty("0: 购物车")
    SHOPPINGCART(0),
    @ApiEnumProperty("1: 囤货")
    STOCKUP(1);

    private Integer procurementType;

    ProcurementTypeEnum(Integer procurementType){
        this.procurementType = procurementType;
    }

    public Integer toProcurementType(){
        return this.procurementType;
    }

}
