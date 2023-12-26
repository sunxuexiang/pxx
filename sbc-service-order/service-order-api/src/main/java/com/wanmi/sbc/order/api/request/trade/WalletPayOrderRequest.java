package com.wanmi.sbc.order.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class WalletPayOrderRequest implements Serializable {

    private static final long serialVersionUID = -4471719792482429873L;

    @ApiModelProperty(value = "tid", name = "订单号")
    @NotBlank(message = "订单号不能为空！")
    private String tid;

    @ApiModelProperty(value = "parentId", name = "父订单号")
    private String parentId;
}
