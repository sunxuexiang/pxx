package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateWithdrawalWithoutCheckRequest extends AccountBaseRequest {

    private static final long serialVersionUID = 2123071731414023432L;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;

    @ApiModelProperty(value = "是否自动申请")
    private Boolean autoType;

}
