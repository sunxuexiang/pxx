package com.wanmi.sbc.returnorder.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
public class TrueStockVO implements Serializable {


    private static final long serialVersionUID = -5537659417419870678L;
    @ApiModelProperty(value = "skuid")
    private String skuid;
    @ApiModelProperty(value = "stock")
    private BigDecimal stock;
}
