package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;

import java.util.List;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
public class QueryTrackListParam {
    private List<QueryTrackParam> queryTrackParamList;
}
