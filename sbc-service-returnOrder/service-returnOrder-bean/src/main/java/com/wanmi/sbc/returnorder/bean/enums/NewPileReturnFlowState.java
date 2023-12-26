package com.wanmi.sbc.returnorder.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 退货流程状态
 */
@ApiEnum(dataType = "java.lang.String")
public enum NewPileReturnFlowState {

    @ApiEnumProperty("0: AUDIT 待退款")
    AUDIT("AUDIT", "待退款"),

    @ApiEnumProperty("1: COMPLETED 已完成")
    COMPLETED("COMPLETED", "已完成"),

    @ApiEnumProperty("2: VOID 已作废")
    VOID("VOID", "已作废");

    private String stateId;

    private String desc;

    NewPileReturnFlowState(String stateId, String desc) {
        this.stateId = stateId;
        this.desc = desc;
    }

    public static Map<String, NewPileReturnFlowState> map = new HashMap<>();

    static {
        Arrays.stream(NewPileReturnFlowState.values()).forEach(
                t -> map.put(t.getStateId(), t)
        );
    }

    @JsonCreator
    public static NewPileReturnFlowState forValue(String stateId) {
        return map.get(stateId);
    }

    @JsonValue
    public String toValue() {
        return this.getStateId();
    }

    public String getStateId() {
        return stateId;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<String, NewPileReturnFlowState> getMap() {
        return map;
    }
}
