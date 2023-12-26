package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesAmountDataView extends BaseDataView implements Serializable {

    // 今日整箱批发销售金额
    private BigDecimal todaySalesPrice;

    // 昨日整箱批发销售金额
    private BigDecimal yesterdaySalesPrice;

    // 今日散批销售金额
    private BigDecimal todaySPSalePrice;

    // 昨日散批销售金额
    private BigDecimal yesterdaySPSalePrice;
}
