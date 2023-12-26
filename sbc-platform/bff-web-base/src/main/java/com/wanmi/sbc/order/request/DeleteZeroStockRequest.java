package com.wanmi.sbc.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 */
@ApiModel
@Data
public class DeleteZeroStockRequest implements Serializable {

    /**
     * wareId
     */
    @ApiModelProperty(value = "wareId")
    @NotNull
    private Long wareId;

    /**
     * 城市code
     */
    @ApiModelProperty(value = "城市code")
    private String cityCode;

    @ApiModelProperty(value = "店铺id")
    @NotNull
    private Long storeId;
    /**
     * 订单wareId
     */
    @ApiModelProperty(value = "订单wareId")
    @NotNull
    private Long realWareId;
}
