package com.wanmi.sbc.account.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * @Description: 钱包交易记录交易类型枚举类
 * @author: jiangxin
 * @create: 2021-11-02 19:36
 * 0充值，1提现，2余额支付，3购物返现
 */
@ApiEnum
public enum WalletRecordTradeType {

    @ApiEnumProperty("0:充值")
    RECHARGE("充值"),

    @ApiEnumProperty("1:提现")
    WITHDRAWAL("提现"),

    @ApiEnumProperty("2:余额支付")
    BALANCE_PAY("余额支付"),

    @ApiEnumProperty("3:购物返现")
    ORDER_CASH_BACK("购物返现"),

    @ApiEnumProperty("4:调账")
    ACCOUNT_REGULATION("调账"),

    @ApiEnumProperty("5:退款")
    BALANCE_REFUND("退款"),

    @ApiEnumProperty("6:撤销申请")
    BALANCE_CANCEL("撤销申请"),

    @ApiEnumProperty("7:驳回")
    TURN_DOWN("驳回"),

    @ApiEnumProperty("8:失败")
    FAIL("失败"),
    @ApiEnumProperty("9:赠送")
    GIVE("赠送"),
    @ApiEnumProperty("10:收回")
    BACK("收回"),
    @ApiEnumProperty("11: 鲸币抵扣")
    DEDUCTION("鲸币抵扣")
    ;

    private String desc;

    WalletRecordTradeType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static WalletRecordTradeType fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
