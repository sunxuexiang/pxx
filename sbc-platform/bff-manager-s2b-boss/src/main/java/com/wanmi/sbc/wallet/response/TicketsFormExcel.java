package com.wanmi.sbc.wallet.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务-提现列表-批量导出字段
 *     用户账号
 *     联系方式
 *     联系人
 *     提现申请时间
 *     客服审核时间
 *     财务审核时间
 *     取消时间
 *     提现金额
 *     到账金额
 *     收款账户
 *     当前状态
 * @author lm
 * @date 2022/10/15 11:42
 */
@Data
public class TicketsFormExcel {

    @ExcelProperty("用户账号")
    private String customerAccount;

    @ExcelProperty("联系方式")
    private String customerPhone;

    @ExcelProperty("联系人")
    private String customerName;

    @ExcelProperty("提现申请时间")
    private String applyStartTime;

    @ExcelProperty("客服审核时间")
    private String auditStartTime;

    @ExcelProperty("财务审核时间")
    private String auditEndTime;

    @ExcelProperty("取消时间")
    private String cancelTime;

    @ExcelProperty("提现金额")
    private BigDecimal fadeInPrice;

    @ExcelProperty("到账金额")
    private BigDecimal arrivalPrice;

    @ExcelProperty("收款账户")
    private String acceptAccount;

    @ExcelProperty("当前状态")
    private String state;

}
