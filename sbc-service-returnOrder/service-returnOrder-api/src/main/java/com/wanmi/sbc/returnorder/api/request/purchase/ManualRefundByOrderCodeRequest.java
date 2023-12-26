package com.wanmi.sbc.returnorder.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@Builder
public class ManualRefundByOrderCodeRequest implements Serializable {


    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty(value = "退款金额")
    private String refundPrice;

    @ApiModelProperty(value = "退款原因")
    private String refuseReason;
}
