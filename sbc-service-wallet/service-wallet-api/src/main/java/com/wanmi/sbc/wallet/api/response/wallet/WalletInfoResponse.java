package com.wanmi.sbc.wallet.api.response.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletInfoResponse implements Serializable {
    private static final long serialVersionUID = 1273365272648963201L;

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "logo")
    private String logo;
    @ApiModelProperty(value = "账户余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "钱包ID")
    private Long walletId;
}
