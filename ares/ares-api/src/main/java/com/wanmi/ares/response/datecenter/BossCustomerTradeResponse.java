package com.wanmi.ares.response.datecenter;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 14:12
 */
@Data
public class BossCustomerTradeResponse {

    /*客户ID*/
    private String customerId;

    /*下单数*/
    private Integer tradeNum;

    /*总下单箱数*/
    private Integer tradeItemNum;

    /*下单总金额*/
    private BigDecimal tradePrice;

}
