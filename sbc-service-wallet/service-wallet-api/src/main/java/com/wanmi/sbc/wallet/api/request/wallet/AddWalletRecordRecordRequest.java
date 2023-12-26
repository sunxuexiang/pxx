package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWalletRecordRecordRequest {

    @ApiModelProperty(value = "用户id")
    private String customerId;
    /**
     * 交易单号
     */
    @ApiModelProperty(value = "交易单号")
    private String recordNo;

    /**
     * 交易备注
     */
    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    /**
     * 客户账号
     */
    @ApiModelProperty(value = "客户账号")
    private String customerAccount;

    /**
     * 关联订单号
     */
    @ApiModelProperty(value = "关联订单号")
    private String relationOrderId;

    /**
     * 虚拟商品id
     */
    @ApiModelProperty(value = "虚拟商品id")
    private Integer virtualGoodsId;

    /**
     * 交易类型【1充值，2提现，3余额支付】
     */
    @ApiModelProperty(value = "交易类型【1充值，2提现，3余额支付】")
    private WalletRecordTradeType tradeType;

    /**
     * 收支类型 [1 收入, 2支出]
     */
    @ApiModelProperty(value = "收支类型")
    private BudgetType budgetType;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal dealPrice;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal chargePrice;

    /**
     * 提现方式
     */
    @ApiModelProperty(value = "提现方式")
    private String extractType;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    /**
     * 当前余额
     */
    @ApiModelProperty(value = "当前余额")
    private BigDecimal currentBalance;

    /**
     * 交易状态 0 待支付 1 已支付 3 未支付
     */
    @ApiModelProperty(value = "交易状态 0 待支付 1 已支付 3 未支付")
    private TradeStateEnum tradeState;

    @ApiModelProperty(value = "支付类型")
    private Integer payType;

    @ApiModelProperty(value = "冻结的赠送金额")
    private BigDecimal blockGiveBalance;

    @ApiModelProperty(value = "最新余额")
    private BigDecimal balance;

    /**
     * 活动类型 0:销售订单  1:提货 2:为囤货
     */
    @ApiModelProperty(value = "活动类型 0:销售订单  1:提货 2:为囤货")
    private String activityType;

    @ApiModelProperty(value = "钱包ID")
    private String storeId;
}
