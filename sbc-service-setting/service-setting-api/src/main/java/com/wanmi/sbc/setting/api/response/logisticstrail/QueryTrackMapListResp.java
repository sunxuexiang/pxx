package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
@ToString(callSuper = true)
public class QueryTrackMapListResp{
    private List<QueryTrackMapResp> queryTrackMapRespList;
}
