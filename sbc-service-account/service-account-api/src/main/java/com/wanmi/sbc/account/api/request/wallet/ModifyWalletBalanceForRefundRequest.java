package com.wanmi.sbc.account.api.request.wallet;


import com.wanmi.sbc.account.bean.enums.BudgetType;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ModifyWalletBalanceForRefundRequest implements Serializable {

    private static final long serialVersionUID = 4238418547592606475L;

    @ApiModelProperty(value = "关联单号")
    private String relationId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "买家id")
    private String buyerId;

    @ApiModelProperty(value = "商家ID")
    private String storeId;

    @ApiModelProperty(value = "会员账号")
    private String customerAccount;

    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty(value = "鲸贴来源/鲸贴明细")
    private WalletDetailsType walletDetailsType;

    @ApiModelProperty(value = "收支类型")
    private BudgetType budgetType;
}