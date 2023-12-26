package com.wanmi.sbc.setting.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: sbc-backgroud
 * @description: 广告配置枚举
 * @author: gdq
 * @create: 2023-06-28 16:45
 **/
public class AdvertisingConfigEnums {

    @AllArgsConstructor
    @Getter
    public enum DayType {
        DAY(1, "天"),
        MONTH(2, "月"),
        ;
        private Integer value;
        private String name;
    }
}
