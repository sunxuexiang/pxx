package com.wanmi.sbc.wallet.api.request.wallet;


import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.wallet.bean.enums.JingBiState;
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
public class WalletByWalletIdAddRequest extends BalanceBaseRequest {

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

    @ApiModelProperty(value = "用于标识是否是商家账户")
    private Boolean merchantFlag;

    @ApiModelProperty(value = "商家ID")
    private String storeId;

    @ApiModelProperty(value = "是否启用")
    private JingBiState jingBiState;
}
