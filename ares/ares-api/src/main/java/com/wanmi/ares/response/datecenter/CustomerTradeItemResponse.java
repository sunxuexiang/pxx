package com.wanmi.ares.response.datecenter;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/09/17 9:44
 */
@Data
public class CustomerTradeItemResponse {


    /*客户id*/
    private String customerId;

    /*总价格*/
    private BigDecimal totalPrice;

    /*总数量*/
    private Integer buyNum;

    /*订单号*/
    private String tid;

    /*下单时间*/
    private String orderTime;
}
