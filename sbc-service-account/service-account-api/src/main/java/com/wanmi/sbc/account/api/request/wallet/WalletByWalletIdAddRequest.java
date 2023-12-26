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
public class WalletByWalletIdAddRequest extends AccountBaseRequest {

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    @NotBlank
    private String customerId;

    /**
     * 客户账户
     */
    @ApiModelProperty(value = "客户账户")
    @NotBlank
    private String customerAccount;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String customerName;

    @ApiModelProperty(value = "商户ID")
    private String storeId;
}
