package com.wanmi.sbc.datecenter.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lm
 * @date 2022/09/17 10:29
 */
@ApiModel("客户详细数据查询")
@Data
public class CustomerTradeItemRequest {

    /*客户ID*/
    @ApiModelProperty("客户ID")
    private String customerId;

    /*时间*/
    @ApiModelProperty("时间")
    private String date;
}
