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
public class SalesCaseNewDataView implements Serializable {

    private static final long serialVersionUID = -6306730947048488087L;
    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    /**
     * 大白鲸今日销售箱数
     */
    private BigDecimal todaySalesCase;

    /**
     * 大白鲸昨日销售箱数
     */
    private BigDecimal yesterdaySalesCase;

    /**
     * 大白鲸今昨销售总箱数
     */
    private BigDecimal towDaySalesCase;


    /**
     * 入驻商家今日销售箱数
     */
    private BigDecimal thirdTodaySalesCase;

    /**
     * 入驻商家昨日销售箱数
     */
    private BigDecimal thirdYesterdaySalesCase;

    /**
     * 入驻商家今昨销售总箱数
     */
    private BigDecimal thirdTowDaySalesCase;
}
