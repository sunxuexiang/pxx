package com.wanmi.sbc.customer.bean.enums;

/**
 * 开启状态枚举
 */

public enum MallContractRelationType {
    TAB(1, "商城"),
    MARKET(2,"市场"),
    ;
    private Integer value;
    private String name;

    MallContractRelationType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}

