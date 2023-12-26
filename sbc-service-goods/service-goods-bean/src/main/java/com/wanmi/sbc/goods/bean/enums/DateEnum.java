package com.wanmi.sbc.goods.bean.enums;

public enum DateEnum {

    DAY("DAY",1L,"天"),
    MONTH("MONTH",30L,"个月"),
    YEAR("YEAR",360L,"年"),
    ;

    private String key;
    private Long value;
    private String desc;

    DateEnum(String key,Long value,String desc){
        this.key = key;
        this.value = value;
        this.desc =desc;
    }

    public Long getValue(){
        return this.value;
    }

    public String getDesc(){
        return this.desc;
    }

    public static DateEnum getDateEnum(String key){
        for(DateEnum dateEnum : DateEnum.values()){
            if (key.equals(dateEnum.key)){
                return dateEnum;
            }
        }
        return null;
    }

}
