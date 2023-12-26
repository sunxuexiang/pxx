package com.wanmi.sbc.wallet.api.request.wallet;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.math.BigDecimal;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletSchedQueryRequest {
    private BigDecimal balance;
}
