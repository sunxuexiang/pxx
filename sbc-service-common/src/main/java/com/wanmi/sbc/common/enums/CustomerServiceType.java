package com.wanmi.sbc.common.enums;

/**
 * 客服服务类型枚举
 */
public enum CustomerServiceType {
    CLOSE(0, "关闭"),
    TENCENT_IM(1, "腾讯IM"),
    SOBOT(2, "智齿");


    private Integer type;
    private String desc;

    private CustomerServiceType (int type, String desc) {
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
