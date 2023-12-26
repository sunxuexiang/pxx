package com.wanmi.sbc.order.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
/**
 * 功能描述: <br>
 * 〈〉
 * @Param: wms 订单推送状态位
 * @Return:
 * @Author: yxb
 * @Date: 2020/5/20 14:31
 */
@ApiEnum
public enum WMSPushState {

    @ApiEnumProperty("0: 退款推送成功")
    CANCEL_ORDER_SUCCESS(0,"退款推送成功"),

    @ApiEnumProperty("1: 退款推送失败")
    CANCEL_ORDER_FAIL(1,"退款推送失败"),

    @ApiEnumProperty("2: 退单推送成功")
    BACK_ORDER_SUCCESS(2,"退单推送成功"),

    @ApiEnumProperty("3: 退单推送成功")
    BACK_ORDER_FAIL(3,"退单推送失败");

    private  Integer type;
    private String desc;

    WMSPushState(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
