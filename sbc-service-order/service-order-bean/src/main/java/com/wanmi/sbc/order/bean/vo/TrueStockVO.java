package com.wanmi.sbc.order.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
