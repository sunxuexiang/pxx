package com.wanmi.sbc.common.enums;

public enum AppPayType {
    alipay("alipay", "支付宝"),
    wechatPay("wechatPay", "微信支付"),
    friendPay("friendPay", "好友代付"),
    publicPay("publicPay", "对公支付"),
    bjLoan("bjLoan", "白鲸借款"),
    bjRepayment("bjRepayment", "白鲸还款"),
    offlinePay("offlinePay", "线下支付"),
    ;

    private String type;
    private String desc;

    private AppPayType (String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
