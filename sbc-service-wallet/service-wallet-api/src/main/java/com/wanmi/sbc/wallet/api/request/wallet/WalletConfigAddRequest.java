package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.wallet.bean.dto.WalletConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class WalletConfigAddRequest extends BalanceBaseRequest {
    private static final long serialVersionUID = -4564259709984687855L;

    @ApiModelProperty(value = "配置参数")
    private WalletConfigDTO walletConfig;
}
