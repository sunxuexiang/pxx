package com.wanmi.sbc.returnorder.api.response.payorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PayOrderUpdatePayOrderResponse implements Serializable {

    @ApiModelProperty(value = "支付单id")
    String value;
}