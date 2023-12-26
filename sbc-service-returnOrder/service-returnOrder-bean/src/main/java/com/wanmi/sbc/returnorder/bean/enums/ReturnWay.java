package com.wanmi.sbc.returnorder.bean.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 退货方式
 */
@ApiEnum
public enum ReturnWay {

    @ApiEnumProperty("0: 其他")
    OTHER(0, "其他"),

    @ApiEnumProperty("1: 物流")
    EXPRESS(1, "物流");

    private Integer type;

    private String desc;

    ReturnWay(Integer type, String desc){
        this.type = type;
        this.desc = desc;
    }

    private static Map<String, ReturnWay> returnWayMap = new HashMap<>();

    static {
        Arrays.stream(ReturnWay.values()).forEach(
                t -> returnWayMap.put(t.getType().toString(), t)
        );
    }

    @JsonCreator
    public static ReturnWay forValue(Map<String,String> map){
        return returnWayMap.get(map.keySet().iterator().next());
    }

    @JsonValue
    public Map<String, String> toValue(){
        Map<String, String> map = new HashMap<>();
        map.put(this.getType().toString(), this.getDesc());
        return map;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<String, ReturnWay> getReturnWayMap() {
        return returnWayMap;
    }
}
