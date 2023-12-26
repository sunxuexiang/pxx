package com.wanmi.ares.view.trade;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyRankingListView {
    /**
     * 具体地址
     */
    private String address;

    /**
     * 购买数量
     */
    private String num;

    /**
     * 购买金额
     */
    private BigDecimal price;
}
