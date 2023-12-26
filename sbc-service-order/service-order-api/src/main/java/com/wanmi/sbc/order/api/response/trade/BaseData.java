package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class BaseData implements Serializable {

    @ApiModelProperty(value = "仓库")
    private String wareHouse;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
