package com.wanmi.sbc.wallet.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户名称
 * 账号
 * 联系人
 * 联系方式
 * 最近提现时间
 * 当前鲸币
 * @author lm
 * @date 2022/10/20 11:44
 */
@Data
public class CustomerWalletExcel {

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("账号")
    private String customerAccount;

    @ExcelProperty("联系人")
    private String concatName;

    @ExcelProperty("联系方式")
    private String concatPhone;

    @ExcelProperty("最近提现时间")
    private String applyTime;

    @ExcelProperty("当前鲸币")
    private BigDecimal balance;
}
