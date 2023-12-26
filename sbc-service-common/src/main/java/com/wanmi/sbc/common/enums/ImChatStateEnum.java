package com.wanmi.sbc.common.enums;

public enum ImChatStateEnum {

    chatting(0, "聊天中"),
    user_timeout(1, "用户超时"),

    server_timeout(2, "客服超时"),

    closed(3, "会话结束"),

    queue(4, "排队中");

    private Integer state;

    private String desc;

    private ImChatStateEnum (Integer state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public Integer getState() {
        return this.state;
    }

    public String getDesc () {
        return this.desc;
    }
}
