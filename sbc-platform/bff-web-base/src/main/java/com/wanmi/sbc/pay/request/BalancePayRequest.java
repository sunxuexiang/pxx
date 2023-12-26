package com.wanmi.sbc.pay.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel
public class BalancePayRequest implements Serializable {
    private static final long serialVersionUID = -8926200762681700128L;

    @ApiModelProperty(value = "tid", name = "订单号")
    @NotBlank(message = "订单号不能为空！")
    private String tid;

    @ApiModelProperty(value = "parentId", name = "父订单号")
    private String parentId;

    @ApiModelProperty(value = "", name = "终端类型(APP,H5)")
    @NotBlank(message = "终端类型不能为空！")
    private String channelType;

    @ApiModelProperty(value = "支付密码", dataType = "String", required = true)
    private String payPassword;

    @NotNull
    @ApiModelProperty(value = "支付渠道id", dataType = "Long", required = true)
    private Long channelItemId;

}
