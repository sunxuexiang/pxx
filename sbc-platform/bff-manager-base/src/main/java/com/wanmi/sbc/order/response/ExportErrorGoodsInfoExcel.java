package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExportErrorGoodsInfoExcel {

    @ExcelProperty(value = "ERP编码", index = 0)
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称", index = 1)
    private String goodsInfoName;

    @ExcelProperty(value = "规格", index = 2)
    private String goodsSpecs;

    @ExcelProperty(value = "数量", index = 3)
    private Integer num;

    @ExcelProperty(value = "报错信息", index = 6)
    //校验报错信息
    private String excelError;
}
