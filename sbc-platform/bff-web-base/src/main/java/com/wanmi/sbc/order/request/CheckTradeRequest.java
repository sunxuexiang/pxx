package com.wanmi.sbc.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: chenchang
 * @Date: Created In 2023-5-25 15:21:52
 * @Description:
 */
@ApiModel
@Data
public class CheckTradeRequest {

    @ApiModelProperty("父订单号，用于不同商家订单合并支付场景")
    private String parentTid ;

    @ApiModelProperty("订单Id")
    private String tid;
}
