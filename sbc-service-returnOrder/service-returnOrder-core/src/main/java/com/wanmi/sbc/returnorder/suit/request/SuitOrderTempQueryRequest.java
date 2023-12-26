package com.wanmi.sbc.returnorder.suit.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuitOrderTempQueryRequest extends BaseRequest {

    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(name = "套装id")
    private Long marketingId;

    @ApiModelProperty(value = "订单号")
    private String orderCode;
}
