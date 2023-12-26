package com.wanmi.sbc.walletorder.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 订单活动类型
 *
 * @author yitang
 * @version 1.0
 */
@ApiEnum
public enum TradeActivityTypeEnum {
    @ApiEnumProperty("0: 订单")
    TRADE("0"),
    @ApiEnumProperty("1: 提货")
    STOCKUP("1"),
    @ApiEnumProperty("2: 囤货")
    PICKGOODS("2"),
    @ApiEnumProperty("3: 新版本囤货")
    NEWPILETRADE("3"),
    @ApiEnumProperty(": 新版本提货")
    NEWPICKTRADE("4");

    private String activityType;

    TradeActivityTypeEnum(String activityType){
        this.activityType = activityType;
    }

    public String toActivityType(){
        return this.activityType;
    }
}
