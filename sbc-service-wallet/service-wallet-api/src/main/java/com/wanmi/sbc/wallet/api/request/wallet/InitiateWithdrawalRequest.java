package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
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
public class InitiateWithdrawalRequest extends BalanceBaseRequest {

    @ApiModelProperty(value = "银行卡卡号")
    private String bankCode;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "账户名称")
    private String bankBranch;

    @ApiModelProperty(value = "开户行名称")
    private String backName;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "客户姓名")
//    @NotBlank
    private String customerName;

    @ApiModelProperty(value = "客户电话")
//    @NotBlank
    private String customerPhone;

    @ApiModelProperty(value = "商户ID")
//    @NotBlank
    private String storeId;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码", dataType = "String", required = true)
    private String payPassword;


}
