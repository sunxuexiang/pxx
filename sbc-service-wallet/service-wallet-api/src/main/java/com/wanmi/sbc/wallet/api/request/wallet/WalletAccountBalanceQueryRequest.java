package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description: 钱包账户余额分页查询请求
 * @author: jiangxin
 * @create: 2021-08-24 11:21
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountBalanceQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -6394696053741160558L;

    @ApiModelProperty(value = "钱包id")
    private Long walletId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户账户")
    private String customerAccount;

    @ApiModelProperty(value = "最大可用余额")
    private BigDecimal maxBalance;

    @ApiModelProperty(value = "最小可用余额")
    private BigDecimal minBalance;

    @ApiModelProperty(value = "最大充值金额")
    private BigDecimal maxRechargeBalance;

    @ApiModelProperty(value = "最小充值金额")
    private BigDecimal minRechargeBalance;

    @ApiModelProperty(value = "最大赠送金额")
    private BigDecimal maxGiveBalance;

    @ApiModelProperty(value = "最小赠送金额")
    private BigDecimal minGiveBalance;

    @ApiModelProperty(value = "账户状态 1-可用，2-冻结")
    private DefaultFlag customerStatus;

    @ApiModelProperty(value = "删除标志 0-否，1-是")
    private DefaultFlag delFlag;


}
