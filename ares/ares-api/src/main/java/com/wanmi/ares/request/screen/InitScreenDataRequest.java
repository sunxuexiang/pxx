package com.wanmi.ares.request.screen;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 初始化订单数据请求类
 * @author lm
 * @date 2022/09/07 10:27
 */
@Data
public class InitScreenDataRequest {
    /*总金额*/
    private BigDecimal total;
    /*时间*/
    private Long time;
    /*订单总数量*/
    private BigInteger orderNum;
    /*省市区数据*/
    private List<Map> provinces;
}
