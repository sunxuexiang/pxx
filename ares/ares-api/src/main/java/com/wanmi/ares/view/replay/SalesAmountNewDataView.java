package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesAmountNewDataView implements Serializable {

    private static final long serialVersionUID = 9071141561057979232L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    /**
     * 大白鲸今日销售金额
     */
    private BigDecimal todaySalesPrice;

    /**
     * 大白鲸昨日销售金额
     */
    private BigDecimal yesterdaySalesPrice;

    /**
     * 大白鲸今昨销售总额
     */
    private BigDecimal towDayTotalSalesPrice;

    /**
     * 入驻商家今日销售金额
     */
    private BigDecimal thirdTodaySalesPrice;

    /**
     * 入驻商家昨日销售金额
     */
    private BigDecimal thirdYesterdaySalesPrice;

    /**
     * 入驻商家今昨销售总额
     */
    private BigDecimal thirdTowDayTotalSalesPrice;
}
