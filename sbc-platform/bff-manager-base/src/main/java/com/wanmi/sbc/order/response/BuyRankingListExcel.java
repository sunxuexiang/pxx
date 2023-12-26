package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyRankingListExcel {
    /**
     * 具体地址
     */
    @ExcelProperty(value = "具体地址", index = 0)
    private String address;

    /**
     * 购买数量
     */
    @ExcelProperty(value = "购买数量", index = 1)
    private String num;

    /**
     * 购买金额
     */
    @ExcelProperty(value = "购买金额", index = 2)
    private BigDecimal price;
}
