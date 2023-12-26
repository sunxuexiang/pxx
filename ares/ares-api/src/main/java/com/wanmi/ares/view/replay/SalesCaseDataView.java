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
public class SalesCaseDataView extends BaseDataView implements Serializable {

    // 今日整箱批发销售箱数
    private BigDecimal todaySalesCase;

    // 今日散批销售箱数
    private BigDecimal todaySPSaleCase;
}
