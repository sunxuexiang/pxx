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
public class CreateRechargeRequest extends AccountBaseRequest {
    private static final long serialVersionUID = 2321329611892565308L;

    @ApiModelProperty(value = "客户账号")
    @NotBlank
    private String customerAccount;

    @ApiModelProperty(value = "充值金额")
    @NotNull
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "备注")
    private String tradeRemark;

    @ApiModelProperty(value = "交易凭证图片地址")
    private String voucherUrl;

}
