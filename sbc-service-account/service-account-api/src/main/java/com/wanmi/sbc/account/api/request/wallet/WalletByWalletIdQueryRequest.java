package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletByWalletIdQueryRequest extends AccountBaseRequest {

    private static final long serialVersionUID = -5864259709984642365L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;
}
