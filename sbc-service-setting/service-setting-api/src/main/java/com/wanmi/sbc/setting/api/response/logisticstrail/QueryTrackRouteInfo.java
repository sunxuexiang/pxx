package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
public class QueryTrackRouteInfo {
    /**
     * 出发位置
     */
    private QueryTrackPosition from;
    /**
     * 当前位置
     */
    private QueryTrackPosition cur;
    /**
     * 收货地
     */
    private QueryTrackPosition to;
}
