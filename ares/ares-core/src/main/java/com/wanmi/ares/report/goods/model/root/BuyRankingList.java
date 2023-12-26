package com.wanmi.ares.report.goods.model.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyRankingList implements Serializable {

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
