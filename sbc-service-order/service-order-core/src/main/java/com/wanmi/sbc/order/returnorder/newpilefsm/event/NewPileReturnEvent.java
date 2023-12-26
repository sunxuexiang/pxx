package com.wanmi.sbc.order.returnorder.newpilefsm.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 退货流程事件
 * Created by jinwei on 21/4/2017.
 */
public enum NewPileReturnEvent {

    REFUND("REFUND", "退款"),

    VOID("VOID", "作废");

    private String type;

    private String desc;

    NewPileReturnEvent(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static Map<String, NewPileReturnEvent> map = new HashMap<>();

    static {
        Arrays.asList(NewPileReturnEvent.values()).stream().forEach(
            t -> map.put(t.getType(), t)
        );
    }

    @JsonCreator
    public static NewPileReturnEvent forValue(String type){
        return map.get(type);
    }

    @JsonValue
    public String toValue(){
        return this.getType();
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<String, NewPileReturnEvent> getMap() {
        return map;
    }
}
