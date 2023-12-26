package com.wanmi.sbc.setting.bean.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 系统配置KEY
 * Created by daiyitian on 2017/4/22.
 */
@ApiEnum(dataType = "java.lang.String")
public enum ConfigKey {
    ///**TODO删除
    // * image_server:图片服务器
    // */
    //@ApiEnumProperty("image_server:图片服务器")
    //IMAGESERVER("image_server"),


    /**
     * resource_server:素材服务器
     */
    RESOURCESERVER("resource_server"),

    /**
     * 小程序直播开关
     */
    LIVESWITCH("live_switch"),

    /**
     * 增值税资质审核
     */
    TICKETAUDIT("ticket_aduit"),

    /**
     * 客服开关
     */
    ONLINESERVICE("online_service"),

    /**
     * kuaidi100
     */
    @ApiEnumProperty("kuaidi100")
    KUAIDI100("kuaidi100"),

    /**
     * S2B审核管理
     */
    @ApiEnumProperty("S2B审核管理")
    S2BAUDIT("s2b_audit"),

    /**
     * 订单设置
     */
    @ApiEnumProperty("订单设置")
    ORDERSETTING("order_setting"),

    /**
     * 移动端设置
     */
    @ApiEnumProperty("移动端设置")
    MOBILE_SETTING("mobile_setting"),

    @ApiEnumProperty("商品设置")
    GOODS_SETTING("goods_setting"),

    /**
     * 小程序设置
     */
    @ApiEnumProperty("小程序设置")
    SMALL_PROGRAM_SETTING("small_program_setting"),

    /**
     * 成长值获取基础规则
     */
    @ApiEnumProperty("成长值获取基础规则")
    GROWTH_VALUE_BASIC_RULE("growth_value_basic_rule"),

    /**
     * 积分基础获取规则
     */
    @ApiEnumProperty("积分基础获取规则")
    POINTS_BASIC_RULE("points_basic_rule"),

    @ApiEnumProperty("增值服务")
    VALUE_ADDED_SERVICES("value_added_services"),

    /**
     * 订单列表展示设置
     */
    @ApiEnumProperty("订单列表展示设置")
    ORDER_LIST_SHOW_TYPE("order_list_show_type"),

    @ApiEnumProperty("支付开关设置")
    PAY_SWITCH_TYPE("pay_switch_type"),

    @ApiEnumProperty("大白鲸APP右上角文字配置")
    APP_HOME_VALUE_CLERK("app_home_value_clerk"),

    @ApiEnumProperty("商品信息开关配置")
    PRODUCT_INFO_SHOW_SWITCH("product_info_show_switch")
            ;

    private final String value;

    ConfigKey(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
