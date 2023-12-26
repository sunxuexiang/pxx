package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExportPileAndTradeStatisticsExcel {
    @ExcelProperty(value = "囤货笔数", index = 0)
    private String orderCount;

    @ExcelProperty(value = "提货笔数", index = 1)
    private String tradeItemCount;

    @ExcelProperty(value = "囤货箱数", index = 2)
    private BigDecimal goodsNum;

    @ExcelProperty(value = "提货箱数", index = 3)
    private BigDecimal tradeItemNum;

    @ExcelProperty(value = "囤货金额",index = 4)
    private BigDecimal splitPrice;
}
