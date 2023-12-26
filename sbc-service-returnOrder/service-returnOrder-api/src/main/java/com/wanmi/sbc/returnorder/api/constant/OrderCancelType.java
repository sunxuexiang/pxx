package com.wanmi.sbc.returnorder.api.constant;

/**
 * 订单取消类型
 */
public interface OrderCancelType {
    //订单超时未支付，系统自动取消
    String autoCancel = "autoCancel";
    //用户手动取消订单
    String manualCancel = "manualCancel";
    //线下付款，运营端驳回，取消订单
    String rejectCancel = "rejectCancel";
}
