package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDetailsRequest extends AccountBaseRequest {


    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(value = "钱包id")
    private Long walletId;

}