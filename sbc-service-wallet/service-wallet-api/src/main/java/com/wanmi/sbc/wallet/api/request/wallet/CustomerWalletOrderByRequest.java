package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletOrderByRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 钱包id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;
    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "用户账号")
    private String customerAccount;

    /**
     * 赠送金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal balance;

    @ApiModelProperty(value = "商户ID")
    private String storeId;

    @ApiModelProperty(value = "订单编号")
    private String relationOrderId;
    @ApiModelProperty(value = "退单号")
    private String returnOrderCode;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    @ApiModelProperty(value = "支付类型")
    private Integer payType;

    @ApiModelProperty(value = "订单活动类型")
    private String activityType;
    @ApiModelProperty(value = "交易类型")
    private BudgetType budgetType;

    @ApiModelProperty(value = "鲸币类型")
    private WalletRecordTradeType walletRecordTradeType;
}
