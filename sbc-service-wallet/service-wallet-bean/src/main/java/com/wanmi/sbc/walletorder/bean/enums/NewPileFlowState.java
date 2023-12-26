package com.wanmi.sbc.walletorder.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态
 *
 * @author chenchang
 * @since 2022/9/19
 */
@ApiEnum(dataType = "java.lang.String")
public enum NewPileFlowState {

    @ApiEnumProperty("0: INIT 创建订单（待支付）")
    INIT("INIT", "待支付"),

    @ApiEnumProperty("2: PILE 已囤货（待提货）")
    PILE("PILE", "待提货"),

    @ApiEnumProperty("3: PICK_PART 部分提货）")
    PICK_PART("PICK_PART", "部分提货"),

    @ApiEnumProperty("4: COMPLETED 已完成(已提货)")
    COMPLETED("COMPLETED", "已完成"),

    @ApiEnumProperty("5: VOID 已作废")
    VOID("VOID", "已作废");


    private static Map<String, NewPileFlowState> flowStateMap = new HashMap<>();

    static {
        Arrays.asList(NewPileFlowState.values()).stream().forEach(
                t -> flowStateMap.put(t.getStateId(), t)
        );
    }


    private String stateId;

    private String description;

    NewPileFlowState(String stateId, String description) {
        this.stateId = stateId;
        this.description = description;
    }

    @JsonCreator
    public static NewPileFlowState forValue(String stateId) {
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
