package com.wanmi.sbc.order.bean.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName WMSCarrierMail
 * @Description TODO
 * @Author shiy
 * @Date 2023/9/15 10:42
 * @Version 1.0
 */

public enum WMSCarrierMail {

    ZTCK("ZTCK","自提出库"),
    KDCK("KDCK","快递出库"),
    PSCK("PSCK","免费店配"),
    WLCK("WLCK","物流出库"),
    PSDD("PSDD","配送到店"),
    KDDF("KDDF","快递到付"),
    TCPS("TCPS","同城配送"),
    ZDZX("ZDZX","指定专线"),
    ;
    private String code;
    private String desc;
    @JsonValue
    public Integer toValue(){
        return this.ordinal();
    }
    WMSCarrierMail(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
