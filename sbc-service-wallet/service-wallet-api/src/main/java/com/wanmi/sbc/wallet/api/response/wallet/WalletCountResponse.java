package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletCountResponse implements Serializable {
    private static final long serialVersionUID = 1273365272648963201L;

    @ApiModelProperty(value = "总余额")
    private BigDecimal TotalBalance;
    @ApiModelProperty(value = "昨天新增总余额")
    private BigDecimal addBalance;
    @ApiModelProperty(value = "昨天减少总余额")
    private BigDecimal reduceBalance;

}
