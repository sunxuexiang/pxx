package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @desc  
 * @author shiy  2023/11/30 16:39
*/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeEvaluateRequest extends BaseRequest {
    private static final long serialVersionUID = -5483465106894784005L;
    @ApiModelProperty("订单Id")
    private String tid;
    @ApiModelProperty("0: 未评价,1: 已评价")
    private Integer evaluateStatus;
}