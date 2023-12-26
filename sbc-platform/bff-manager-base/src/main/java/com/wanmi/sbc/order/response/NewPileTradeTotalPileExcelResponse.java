package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/10/12 16:39
 */
@Data
public class NewPileTradeTotalPileExcelResponse {

    @ExcelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称")
    private String skuName;

    @ExcelProperty(value = "囤货数量（包含已提货数量）")
    private Long totalNum;

    @ExcelProperty(value = "囤货单金额(实际付款金额）")
    private BigDecimal actualPrice;

    @ExcelProperty(value = "囤货仓库")
    private String wareName;

    @ExcelProperty(value = "商品大类")
    private String productCate;
}
