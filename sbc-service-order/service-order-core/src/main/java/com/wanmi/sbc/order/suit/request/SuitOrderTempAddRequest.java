package com.wanmi.sbc.order.suit.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuitOrderTempAddRequest extends BaseRequest {

    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "购买数量")
    private Long suitBuyNum;

    @ApiModelProperty(name = "套装id")
    private Long marketingId;
}
