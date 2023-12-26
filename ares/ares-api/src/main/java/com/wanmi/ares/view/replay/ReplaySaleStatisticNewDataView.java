package com.wanmi.ares.view.replay;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
public class ReplaySaleStatisticNewDataView implements Serializable {

    private static final long serialVersionUID = 7640565831897429795L;

    /**
     * 今昨日销售金额
     */
    private List<SalesAmountNewDataView> salesAmountDataViewList;

    /**
     * 今昨日销售箱数
     */
    private List<SalesCaseNewDataView> salesCaseDataViewList;

    /**
     * 今昨日销售单数
     */
    private List<SalesOrderNewDataView> salesOrderDataViewList;

    /**
     * 今日下单用户数
     */
    private List<SalesUserNewDataView> salesUserDataViewList;

}
