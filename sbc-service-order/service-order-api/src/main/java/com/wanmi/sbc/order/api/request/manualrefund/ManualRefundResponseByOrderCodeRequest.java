package com.wanmi.sbc.order.api.request.manualrefund;


import com.wanmi.sbc.common.base.Operator;
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
public class ManualRefundResponseByOrderCodeRequest implements Serializable {

    private static final long serialVersionUID = -3009581667118223677L;

    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty(value = "退款金额")
    private String refundPrice;

    @ApiModelProperty(value = "退款原因")
    private String refuseReason;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;

}
