package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateWithdrawalWithoutCheckRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = 2123071731414023432L;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;

}
