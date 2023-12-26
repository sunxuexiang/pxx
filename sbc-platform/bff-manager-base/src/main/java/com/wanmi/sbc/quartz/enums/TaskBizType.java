package com.wanmi.sbc.quartz.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum TaskBizType {

    /**
     * 精准发券
     */
    PRECISION_VOUCHERS,

    /**
     * 消息发送
     */
    MESSAGE_SEND;

    @JsonCreator
    public static TaskBizType fromValue(int ordinal) {
        return TaskBizType.values()[ordinal];
    }

    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }

}
