package com.wanmi.ares.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyRankingListRequest {
    /**
     * 显示数据量
     */
    private Integer size = 30;

    /**
     * 筛选时间（开始 2020-12-12 00:00:00）
     */
    private String beginTime;

    /**
     * 筛选时间（开始 2020-12-12 00:00:00）
     */
    private String endTime;

    /**
     * 排序方式（asc、desc）
     */
    private String sort = "asc";
}
