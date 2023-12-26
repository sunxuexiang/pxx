package com.wanmi.sbc.order.api.request.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.BudgetType;
import com.wanmi.sbc.account.bean.enums.TradeStateEnum;
import com.wanmi.sbc.account.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
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
public class AddWalletRecordRequest {

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
    @ApiModelProperty(value = "交易类型【0充值，1提现，2余额支付，3购物返现】")
    private WalletRecordTradeType tradeType;

    /**
     * 交易类型【1充值，2提现，3余额支付】--中文
     */
    @ApiModelProperty(value = "交易类型【0充值，1提现，2余额支付，3购物返现】")
    private String tradeTypeString;

    /**
     * 收支类型
     */
    @ApiModelProperty(value = "收支类型 0收入，1支出")
    private BudgetType budgetType;

    /**
     * 收支类型--中文
     */
    @ApiModelProperty(value = "收支类型 0收入，1支出")
    private String budgetTypeString;

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
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    /**
     * 当前余额
     */
    @ApiModelProperty(value = "当前余额")
    private BigDecimal currentBalance;

    /**
     * 交易状态 0 待支付 1 已支付 2 未支付
     */
    @ApiModelProperty(value = "交易状态 0 未支付 1 待确认 2 已支付")
    private TradeStateEnum tradeState;

    /**
     * 支付类型 微信 支付宝
     */
    @ApiModelProperty(value = "支付类型")
    private Integer payType;

    @ApiModelProperty(value = "冻结的赠送金额")
    private BigDecimal blockGiveBalance;

    /**
     * 活动类型 0:销售订单  1:提货 2:为囤货
     */
    @ApiModelProperty(value = "活动类型 0:销售订单  1:提货 2:为囤货")
    private String activityType;
}
