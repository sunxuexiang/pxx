package com.wanmi.sbc.order.api.request.refund;

import com.wanmi.sbc.common.base.Operator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RefundOrderNotAuditProducerRequest implements Serializable {
    private static final long serialVersionUID = -3009581557118223677L;

    @ApiModelProperty(value = "订单id")
    private String tId;

    @ApiModelProperty(value = "退单id")
    private String rId;

    @ApiModelProperty(value = "操作人")
    private Operator operator;

    @ApiModelProperty(value = "新提货退单标记")
    private boolean newPickOrderFlag;

    public boolean getNewPickOrderFlag() {
        return newPickOrderFlag;
    }
}
