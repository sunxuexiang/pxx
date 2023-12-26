package com.wanmi.ares.response.datecenter;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 9:33
 */
@Data
public class CustomerTradeResponse {

    /*客户账号*/
    private String customerName;

    /*客户名称*/
    private String customerAccount;

    /*客户id*/
    private String customerId;

    /*总价格*/
    private BigDecimal totalPrice;

    /*购买总数量*/
    private Integer buyNum;

    /*下单总数*/
    private Integer tradeNum;
}
