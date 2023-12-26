package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class WalletPayRequest extends WalletPayOrderRequest implements Serializable {
    private static final long serialVersionUID = -7874086624970340300L;

    @ApiModelProperty(value = "customerId", name = "用户id")
    private String customerId;

    @ApiModelProperty(value = "", name = "终端类型(APP,H5)")
    @NotBlank(message = "终端类型不能为空！")
    private String channelType;
}
