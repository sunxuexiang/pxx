package com.wanmi.sbc.account.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包VO
 */
@Data
public class CustomerWalletVO implements Serializable {
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

    /**
     * 客户账户
     */
    @ApiModelProperty(value = "客户账户")
    private String customerAccount;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 可用余额【充值金额+赠送金额】
     */
    @ApiModelProperty(value = "可用余额【充值金额+赠送金额】")
    private BigDecimal balance;

    /**
     * 充值金额
     */
    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;

    /**
     * 赠送金额
     */
    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giveBalance;

    /**
     * 冻结余额（提现中）
     */
    @ApiModelProperty(value = "冻结余额（提现中）")
    private BigDecimal blockBalance;

    /**
     * 账户状态（1可用，2冻结）
     */
    @ApiModelProperty(value = "账户状态（0可用，1冻结）")
    private DefaultFlag customerStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createId;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateId;

    /**
     * 删除标识
     */
    @ApiModelProperty(value = "删除标识")
    private DefaultFlag delFlag;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 冻结金额状态 0 可用 1 不可用
     */
    @ApiModelProperty(value = "冻结金额状态 0 可用 1 不可用")
    private  Integer giveBalanceState;

    /**
     * 申请时间(提现申请)
     */
    @ApiModelProperty(value = "申请时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    /**
     * 联系人名字
     */
    @ApiModelProperty(value = "联系人名字")
    private String contactName;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String contactPhone;

    /**
     * 已提现金额
     */
    @ApiModelProperty(value = "已提现金额")
    private BigDecimal extractBalance;

    /**
     * 已抵扣金额
     */
    @ApiModelProperty(value = "已抵扣金额")
    private BigDecimal deductionBalance;

    @ApiModelProperty(value = "商家ID")
    private Long storeId;
}
