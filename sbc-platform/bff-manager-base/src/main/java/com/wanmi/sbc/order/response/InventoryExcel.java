package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class InventoryExcel {
    @ExcelProperty(value = "erp编码", index = 0)
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称", index = 1)
    private String skuName;

    @ExcelProperty(value = "库存", index = 2)
    private String num;

    @ExcelProperty(value = "囤货数量", index = 3)
    private String pileNum;

    @ExcelProperty(value = "等货商品数量=库存-囤货数量", index = 4)
    private String pendingNum;
}
