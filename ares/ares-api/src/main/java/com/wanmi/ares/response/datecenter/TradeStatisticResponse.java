package com.wanmi.ares.response.datecenter;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 15:30
 */
@Data
public class TradeStatisticResponse {

    /*客户ID*/
    private String customerId;

    /*订单id*/
    private String tid;

    /*订单金额*/
    private BigDecimal tradePrice;

    /*当前订单箱数*/
    private Integer num;
}
