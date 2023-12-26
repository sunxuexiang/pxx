package com.wanmi.sbc.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel
@Data
public class SuitBuyRequest implements Serializable {

    @Valid
    @NotNull(message = "套装id不能为空！")
    @ApiModelProperty("套装id")
    private Long marketingId;

    @Valid
    @ApiModelProperty("购买数量")
    @Min(value = 1L,message = "购买数量不合法")
    private Long buyCount;

    @ApiModelProperty("分仓ID")
    private Long wareId;
}
