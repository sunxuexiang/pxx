package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleSynthesisDataView extends BaseDataView implements Serializable {

    /*总金额*/
    private BigDecimal salePrice;

    /*下单用户数*/
    private Integer buyNum;

    /*订单数*/
    private Integer orderNum;

    /*订单箱数*/
    private Long orderItemNum;


    private List<RecentSevenDaySaleView> recentSevenDaySaleTotalCaseList;

    private List<RecentSevenDaySalePriceView> recentSevenDaySaleTotalPriceList;
}
