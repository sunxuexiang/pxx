package com.wanmi.ares.view.replay;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/10/7 15:08
 */
@Data
public class TowDayStoreSalePriceView {

    private String companyId;

    private String storeName;

    private BigDecimal todaySalesPrice;

    private BigDecimal yesterdaySalesPrice;

    private BigDecimal towDaySalesPrice;
}
