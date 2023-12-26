package com.wanmi.ares.response.datecenter;

import lombok.Data;

/**
 * @author lm
 * @date 2022/09/19 14:14
 */
@Data
public class TradeItemStatisticResponse {

    /*订单号*/
    private String tid;

    /*总箱数*/
    private Integer tradeItemNum;
}
