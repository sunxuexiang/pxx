package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseData implements Serializable {

    @ApiModelProperty(value = "仓库")
    private String wareHouse;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
