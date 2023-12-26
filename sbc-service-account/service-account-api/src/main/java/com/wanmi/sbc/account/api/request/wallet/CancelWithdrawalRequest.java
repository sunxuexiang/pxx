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
public class CancelWithdrawalRequest extends AccountBaseRequest {

    @ApiModelProperty(value = "申请单号")
    private Long formId;


    @ApiModelProperty(value = "客户id")
    private String customerId;
    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码", dataType = "String", required = true)
    private String payPassword;


}
