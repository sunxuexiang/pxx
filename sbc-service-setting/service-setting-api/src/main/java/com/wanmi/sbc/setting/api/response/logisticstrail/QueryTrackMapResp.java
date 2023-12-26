package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
@ToString(callSuper = true)
public class QueryTrackMapResp extends QueryTrackResp {

    /**
     * 轨迹地图链接
     */
    private String trailUrl;
    /**
     * 预计到达时间
     */
    private String arrivalTime;
    /**
     * 平均耗时
     */
    private String totalTime;
    /**
     * 到达还需多少时间
     */
    private String remainTime;
}
