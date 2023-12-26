package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesOrderNewDataView implements Serializable {

    private static final long serialVersionUID = -5545392814064931085L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    /**
     * 大白鲸今日订单数
     */
    private Long todayOrderCount;

    /**
     * 大白鲸昨日订单数
     */
    private Long yesterdayOrderCount;

    /**
     * 大白鲸今昨订单总数数
     */
    private Long towDayOrderCount;

    /**
     * 入驻商家今日订单数
     */
    private Long thirdTodayOrderCount;

    /**
     * 入驻商家昨日订单数
     */
    private Long thirdYesterdayOrderCount;

    /**
     * 入驻商家今昨订单总数数
     */
    private Long thirdTowDayOrderCount;

}
