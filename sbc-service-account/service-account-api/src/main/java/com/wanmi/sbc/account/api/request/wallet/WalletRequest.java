package com.wanmi.sbc.account.api.request.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest implements Serializable {

    @ApiModelProperty(value = "用户id")
    private String customerId;

    /**
     * 消费金额
     */
    @ApiModelProperty(value = "使用金额")
    private BigDecimal expenseAmount;


    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String customerAccount;
}
