package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticRecordItemPriceNumNoPileUserExcel {
    @ExcelProperty(value = "囤货未提数量", index = 0)
    private BigDecimal num;

    @ExcelProperty(value = "客户手机号", index = 1)
    private String account;

    @ExcelProperty(value = "客户名称", index = 2)
    private String name;

    @ExcelProperty(value = "业务员名称", index = 3)
    private String employeeName;
}
