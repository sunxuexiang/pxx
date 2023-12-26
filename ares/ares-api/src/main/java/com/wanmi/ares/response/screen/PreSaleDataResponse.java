package com.wanmi.ares.response.screen;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 今日预售数据接口响应
 * @author lm
 * @date 2022/09/06 14:07
 */
@Data
public class PreSaleDataResponse implements Serializable {

    /**
     * 今日成交金额
     */
    private String todayTotalMoney = "0.00";

    /**
     * 今日成交订单数
     */
    private String todayTotalNum = "0";
}
