package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExportGoodsByStatusExcel {
    @ExcelProperty(value = "分类", index = 0)
    private String cate_name;

    @ExcelProperty(value = "编码", index = 1)
    private String erp_goods_info_no;

    @ExcelProperty(value = "商品名称", index = 2)
    private String goods_info_name;

    @ExcelProperty(value = "保质期", index = 3)
    private String shelflife;

    @ExcelProperty(value = "规格",index = 4)
    private String goods_subtitle;

    @ExcelProperty(value = "价格",index = 5)
    private String market_price;

    @ExcelProperty(value = "品牌",index = 6)
    private String brand_name;

    @ExcelProperty(value = "上下架状态",index = 7)
    private String del_flag;

    @ExcelProperty(value = "库存",index = 8)
    private String warestock;




}
