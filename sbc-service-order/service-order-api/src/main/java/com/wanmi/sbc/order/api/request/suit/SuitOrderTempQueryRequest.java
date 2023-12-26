package com.wanmi.sbc.order.api.request.suit;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuitOrderTempQueryRequest extends BaseRequest implements Serializable {

    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(name = "套装id")
    private Long marketingId;
}
