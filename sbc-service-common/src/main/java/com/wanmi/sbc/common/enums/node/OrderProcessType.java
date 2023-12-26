package com.wanmi.sbc.common.enums.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单进度通知枚举类
 */
@ApiEnum
public enum OrderProcessType {
    @ApiEnumProperty(" 0：订单提交成功通知")
    ORDER_COMMIT_SUCCESS("ORDER_COMMIT_SUCCESS","订单提交成功通知"),

    @ApiEnumProperty("1：订单提交成功审核通知")
    ORDER_COMMIT_SUCCESS_CHECK("ORDER_COMMIT_SUCCESS_CHECK","订单提交成功审核通知"),

    @ApiModelProperty("2: 订单审核通过通知")
    ORDER_CHECK_PASS("ORDER_CHECK_PASS","订单审核通过通知"),

    @ApiModelProperty("3: 订单审核未通过通知")
    ORDER_CHECK_NOT_PASS("ORDER_CHECK_NOT_PASS","订单审核未通过通知"),

    @ApiModelProperty("4: 订单支付成功通知")
    ORDER_PAY_SUCCESS("ORDER_PAY_SUCCESS","订单支付成功通知"),

    @ApiModelProperty("5: 订单发货通知")
    ORDER_SEND_GOODS("ORDER_SEND_GOODS","订单发货通知"),

    @ApiModelProperty("6: 订单完成通知")
    ORDER_COMPILE("ORDER_COMPILE","订单完成通知"),

    @ApiModelProperty("7: 商品评价提醒")
    GOODS_EVALUATION("GOODS_EVALUATION","商品评价提醒"),

    @ApiModelProperty("8: 服务评价提醒")
    SERVICE_EVALUATION("SERVICE_EVALUATION","服务评价提醒"),

    @ApiModelProperty("9: 开团成功通知")
    GROUP_OPEN_SUCCESS("GROUP_OPEN_SUCCESS","开团成功通知"),

    @ApiModelProperty("10: 参团人数不足提醒")
    GROUP_NUM_LIMIT("GROUP_NUM_LIMIT","参团人数不足提醒"),

    @ApiModelProperty("11: 拼团成功通知")
    JOIN_GROUP_SUCCESS("JOIN_GROUP_SUCCESS","拼团成功通知"),

    @ApiModelProperty("12: 拼团失败通知")
    JOIN_GROUP_FAIL("JOIN_GROUP_FAIL","拼团失败通知"),

    @ApiEnumProperty("13:会员自提订单发货回传短信通知")
    CUSTOMER_PICK_UP_CODE("CUSTOMER_PICK_UP_CODE","会员自提订单发货回传短信通知"),

    @ApiEnumProperty("14:会员自提订单支付短信通知")
    CUSTOMER_PICK_UP_PAY_SUCCESS("CUSTOMER_PICK_UP_PAY_SUCCESS","会员自提订单支付短信通知"),

    @ApiEnumProperty("15:会员自提订单提货短信通知")
    CUSTOMER_PICK_UP_RECEIVE ("CUSTOMER_PICK_UP_RECEIVE","会员自提订单提货短信通知");

    private String type;

    private String description;

    OrderProcessType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @JsonCreator
    public OrderProcessType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
