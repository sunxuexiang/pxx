package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletByWalletIdQueryRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = -5864259709984642365L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;
}
