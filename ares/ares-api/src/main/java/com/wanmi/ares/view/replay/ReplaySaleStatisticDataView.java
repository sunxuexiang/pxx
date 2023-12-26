package com.wanmi.ares.view.replay;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReplaySaleStatisticDataView implements Serializable {

    // 今昨日销售金额
    private List<SalesAmountDataView> salesAmountDataViewList;

    // 今昨日销售箱数
    private List<SalesCaseDataView> salesCaseDataViewList;

    // 今昨日销售单数
    private List<SalesOrderDataView> salesOrderDataViewList;

    // 今昨日下单用户数
    private List<SalesUserDataView> salesUserDataViewList;

}
