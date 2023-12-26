package com.wanmi.sbc.account.api.request.cashBack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CashBackModifyRequest {

    @ApiModelProperty(value = "返现记录id")
    private Integer id;

    @ApiModelProperty(value = "状态")
    private Integer returnStatus;
}
