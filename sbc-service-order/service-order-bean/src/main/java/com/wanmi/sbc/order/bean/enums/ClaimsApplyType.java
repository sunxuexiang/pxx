package com.wanmi.sbc.order.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import lombok.AllArgsConstructor;

/**
 * 鲸贴充值类型
 */
@ApiEnum
@AllArgsConstructor
public enum ClaimsApplyType {

    @ApiEnumProperty("6：手动充值")
    INCREASE_RECHARGE(0,"手动充值"),

    @ApiEnumProperty("6：手动扣除")
    DEDUCTION_RECHARGE(1,"手动扣除");

    private Integer id;
    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }
}
