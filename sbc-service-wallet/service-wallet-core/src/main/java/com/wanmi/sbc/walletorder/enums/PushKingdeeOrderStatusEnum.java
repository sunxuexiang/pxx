package com.wanmi.sbc.walletorder.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
public enum PushKingdeeOrderStatusEnum {
    @ApiEnumProperty("创建")
    CREATE(0),
    @ApiEnumProperty("取消成功")
    CANCELSUCCESS(1),
    @ApiEnumProperty("取消失败")
    CANCELFAILURE(2),
    @ApiEnumProperty("参数错误")
    PARAMETERERROR(3);
    private final Integer orderStatus;
    PushKingdeeOrderStatusEnum(Integer orderStatus){
        this.orderStatus = orderStatus;
    }

    @JsonValue
    public Integer toOrderStatus(){
        return orderStatus;
    }
}
