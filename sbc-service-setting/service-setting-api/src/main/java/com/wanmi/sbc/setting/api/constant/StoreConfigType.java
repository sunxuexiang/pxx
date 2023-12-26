package com.wanmi.sbc.setting.api.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/***
 * @desc 商家设置
 * @author shiy  2023/7/3 17:53
*/
@ApiEnum(dataType = "java.lang.String")
public enum StoreConfigType {

    /**
     * 订单支付顺序设置（先款后货/不限）
     */
    @ApiEnumProperty("订单支付顺序设置（先款后货/不限）")
    ORDER_SETTING_PAYMENT_ORDER("order_setting_payment_order","订单支付顺序设置（先款后货/不限）"),

    /**
     * 超时未支付取消订单
     */
    @ApiEnumProperty("超时未支付取消订单")
    ORDER_SETTING_TIMEOUT_CANCEL("order_setting_timeout_cancel","超时未支付取消订单"),

    /**
     * 订单设置自动收货
     */
    @ApiEnumProperty("订单设置自动收货")
    ORDER_SETTING_AUTO_RECEIVE("order_setting_auto_receive","订单设置自动收货"),

    /**
     * 退单自动审核
     */
    @ApiEnumProperty("退单自动审核")
    ORDER_SETTING_REFUND_AUTO_AUDIT("order_setting_refund_auto_audit","退单自动审核"),

    /**
     * 退单自动收货
     */
    @ApiEnumProperty("退单自动收货")
    ORDER_SETTING_REFUND_AUTO_RECEIVE("order_setting_refund_auto_receive","退单自动收货"),

    /**
     * 允许申请退单
     */
    @ApiEnumProperty("允许申请退单")
    ORDER_SETTING_APPLY_REFUND("order_setting_apply_refund","允许申请退单"),
    @ApiEnumProperty("乡镇件配送启用开关")
    LOGISTICS_VILLAGES_OPEN_FLAG("logistics_villages_open_flag","乡镇件配送启用开关"),

    @ApiEnumProperty("第三方物流启用开关")
    LOGISTICS_THIRD_PARTY_OPEN_FLAG("LOGISTICS_THIRD_PARTY_OPEN_FLAG","第三方物流启用开关"),

    @ApiEnumProperty("免费店配启用开关")
    LOGISTICS_FREE_STORE_OPEN_FLAG("logistics_free_store_open_flag","免费店配启用开关");

    private final String configType;

    private final String configName;

    StoreConfigType(String configType,String configName) {
        this.configType = configType;
        this.configName = configName;
    }

    @JsonValue
    public String toValue() {
        return configType;
    }
}
