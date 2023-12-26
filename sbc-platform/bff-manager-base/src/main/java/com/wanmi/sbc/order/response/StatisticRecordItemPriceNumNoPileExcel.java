package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticRecordItemPriceNumNoPileExcel {
    @ExcelProperty(value = "erp编码", index = 0)
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称", index = 1)
    private String skuName;

    @ExcelProperty(value = "囤货数量", index = 2)
    private BigDecimal num;
}
