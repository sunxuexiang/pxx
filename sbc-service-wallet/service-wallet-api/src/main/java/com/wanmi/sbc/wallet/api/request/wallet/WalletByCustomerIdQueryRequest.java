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
public class WalletByCustomerIdQueryRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = -5864259709984687855L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    private String customerId;

    @ApiModelProperty(value = "交易单号")
    private String orderNo;
}
