package com.wanmi.sbc.returnorder.api.response.payorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
@Builder
public class FindPayOrderResponseWrapper implements Serializable {
    /**
     * 支付单查询结果
     */
    @ApiModelProperty(value = "支付单查询结果")
    List<FindPayOrderResponse> responses;
}
