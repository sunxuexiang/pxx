package com.wanmi.sbc.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @description  退款所属单据类型
 * @author  shiy
 * @date    2023/3/16 16:55
 * @params  
 * @return  
*/
@ApiEnum
public enum RefundBillTypeEnum {
    @ApiEnumProperty("订单")
    ORDER(1),
    @ApiEnumProperty("退单")
    RETURN(2);

    private final Integer status;

    RefundBillTypeEnum(Integer status){
        this.status = status;
    }

    @JsonValue
    public Integer toStatus() {
        return status;
    }

}
