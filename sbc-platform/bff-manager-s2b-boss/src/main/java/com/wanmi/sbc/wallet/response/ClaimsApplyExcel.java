package com.wanmi.sbc.wallet.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.wanmi.sbc.convert.LocalDateStringConverter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description  鲸币充值导出
 * @author  shiy
 * @date    2023/5/10 16:14
 * @params
 * @return
*/
@Data
@ColumnWidth(18)
public class ClaimsApplyExcel {
    /*@ExcelProperty(value = "理赔申请单号")
    private String applyNo;*/

    @ExcelProperty(value = "用户账号")
    private String customerAccount;

    @ExcelProperty(value = "联系方式")
    private String contactPhone;

    @ColumnWidth(20)
    @ExcelProperty(value = "充值时间",converter = LocalDateStringConverter.class)
    private LocalDateTime rechargeTime;

    @ExcelProperty(value = "充值人")
    private String operatorName;

    @ExcelProperty(value = "充值类型")
    private String rechargeTypeText;

    @ExcelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;

    /*@ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "订单号")
    private String orderNo;

    @ExcelProperty(value = "退单号")
    private String returnOrderNo;*/

}