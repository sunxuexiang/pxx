package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
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
public class WalletByCustomerAccountQueryRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = -5864259709984642505L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "客户账户")
    @NotBlank
    private String customerAccount;
}
