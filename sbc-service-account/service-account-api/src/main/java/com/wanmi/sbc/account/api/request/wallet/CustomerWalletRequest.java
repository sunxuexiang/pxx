package com.wanmi.sbc.account.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-24 14:06
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 钱包id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;

    @ApiModelProperty("客户ID")
    private List<String> customerFundsIdList;

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

    @ApiModelProperty(value = "最大可用余额")
    private BigDecimal maxBalance;

    @ApiModelProperty(value = "最小可用余额")
    private BigDecimal minBalance;

    /**
     * 充值金额
     */
    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;
    @ApiModelProperty(value = "最大充值金额")
    private BigDecimal maxRechargeBalance;
    @ApiModelProperty(value = "最小充值金额")
    private BigDecimal minRechargeBalance;

    /**
     * 赠送金额
     */
    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giveBalance;
    @ApiModelProperty(value = "最大赠送金额")
    private BigDecimal maxGiveBalance;
    @ApiModelProperty(value = "最小赠送金额")
    private BigDecimal minGiveBalance;

    /**
     * 冻结余额（提现中）
     */
    @ApiModelProperty(value = "冻结余额（提现中）")
    private BigDecimal blockBalance;

    /**
     * 账户状态（1可用，2冻结）
     */
    @ApiModelProperty(value = "账户状态（1可用，2冻结）")
    private DefaultFlag customerStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
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
     * 最近提现时间
     */
    @ApiModelProperty(value = "最近提现时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime recentlyTicketsTimeStart;

    /**
     * 最近提现时间
     */
    @ApiModelProperty(value = "最近提现时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime recentlyTicketsTimeEnd;

    @ApiModelProperty(value = "是否使用余额,0：否，1：是")
    private Integer useBalance;
}
