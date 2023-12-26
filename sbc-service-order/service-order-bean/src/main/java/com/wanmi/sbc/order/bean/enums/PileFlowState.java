package com.wanmi.sbc.order.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态
 * Created by jinwei on 14/3/2017.
 */
@ApiEnum(dataType = "java.lang.String")
public enum PileFlowState {

    @ApiEnumProperty("0: INIT 创建订单")
    INIT("INIT", "创建订单"),

    @ApiEnumProperty("1: AUDIT 审核订单")
    AUDIT("AUDIT", "审核订单"),

    @ApiEnumProperty("2: PILE 已囤货")
    PILE("PILE", "已囤货"),

    @ApiEnumProperty("3: PICK 已提货")
    PICK("PICK", "已提货"),

    @ApiEnumProperty("4: REFUND 已退款")
    REFUND("REFUND", "已退款"),

    @ApiEnumProperty("5: REJECT 拒绝退款")
    REJECT("REJECT", "拒绝退款"),

    @ApiEnumProperty("6: VOID 已作废")
    VOID("VOID", "已作废"),

    @ApiEnumProperty("7: COMPLETED 已完成")
    COMPLETED("COMPLETED", "已完成");



    private static Map<String, PileFlowState> flowStateMap = new HashMap<>();

    static {
        Arrays.asList(PileFlowState.values()).stream().forEach(
                t -> flowStateMap.put(t.getStateId(), t)
        );
    }


    private String stateId;

    private String description;

    PileFlowState(String stateId, String description) {
        this.stateId = stateId;
        this.description = description;
    }

    @JsonCreator
    public static PileFlowState forValue(String stateId) {
        return flowStateMap.get(stateId);
    }

    @JsonValue
    public String toValue() {
        return this.getStateId();
    }

    public String getStateId() {
        return stateId;
    }

    public String getDescription() {
        return description;
    }

}
