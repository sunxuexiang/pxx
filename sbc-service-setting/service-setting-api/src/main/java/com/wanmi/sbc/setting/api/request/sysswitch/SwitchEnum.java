package com.wanmi.sbc.setting.api.request.sysswitch;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public enum SwitchEnum {

    zengzhuangzz("zengzhuangzz","增专资质开关"),
    notSameProvinceCanDeliveryToStore("notSameProvinceCanDeliveryToStore","不是同省也能使用配送到店"),
    hunanFirstVillageAdd("hunanFirstVillageAdd","湖南首单加收");

    public static SwitchEnum checkSwtichCode(String switchCode) {
        Integer j=0;
        for (; j <values().length ; j++) {
            SwitchEnum switchEnum =values()[j];
            if(switchEnum.switchCode.equals(switchCode)){
                return switchEnum;
            }
        }
        if(j>=values().length){
            throw new RuntimeException("请先配置系统开关");
        }
        return null;
    }

    private String switchCode;
    private String switchName;

    public String getSwitchCode() {
        return switchCode;
    }

    public void setSwitchCode(String switchCode) {
        this.switchCode = switchCode;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }
}
