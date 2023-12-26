package com.wanmi.sbc.wallet.api.response.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletListResponse implements Serializable {
    private static final long serialVersionUID = 1273365272648963201L;

    @ApiModelProperty(value = "账户名称")
    private String customerAccount;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "鲸币余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "最近一次获得时间")
    private Timestamp jTime;
    @ApiModelProperty(value = "最近一次获得金额")
    private BigDecimal jMoney;
    @ApiModelProperty(value = "最后一次获得时间")
    private Timestamp hTime;
    @ApiModelProperty(value = "最后一次获得金额")
    private BigDecimal hMoney;
    @ApiModelProperty(value = "钱包ID")
    private Long walletId;
}
