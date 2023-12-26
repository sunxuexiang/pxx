package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/10/12 16:39
 */
@Data
public class NewPileTradeNoPileExcelResponse {

    @ExcelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称")
    private String skuName;

    @ExcelProperty(value = "囤货未提数量")
    private Long noPickNum;

    @ExcelProperty(value = "囤货未提金额（实际付款）")
    private BigDecimal actualPrice;

    @ExcelProperty(value = "囤货未提仓库")
    private String wareName;

    @ExcelProperty(value = "商品大类")
    private String productCate;
}
