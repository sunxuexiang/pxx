package com.wanmi.ares.report.trade.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyRankingListCollect {
    /**
     * 显示数据量
     */
    private Integer size;

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
    private String sort;
}
