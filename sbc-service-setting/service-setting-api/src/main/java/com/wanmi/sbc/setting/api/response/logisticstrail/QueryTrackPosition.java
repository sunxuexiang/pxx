package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
public class QueryTrackPosition {

    /**
     * 地址编码
     */
    private String number;
    /**
     * 地址名称
     */
    private String name;
}
