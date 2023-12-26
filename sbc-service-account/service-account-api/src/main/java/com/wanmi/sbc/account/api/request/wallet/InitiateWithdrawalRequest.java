package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateWithdrawalRequest extends AccountBaseRequest {

    @ApiModelProperty(value = "银行卡卡号")
    private String bankCode;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "客户姓名")
    @NotBlank
    private String customerName;

    @ApiModelProperty(value = "客户电话")
    @NotBlank
    private String customerPhone;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码", dataType = "String", required = true)
    private String payPassword;


}
