package com.wanmi.sbc.goods.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class GoodsDevanningDetailRequest {

    /**
     * goodsInfoId
     */
    @ApiModelProperty(value = "goodsInfoId")
    private String goodsInfoId;

    /**
     * parentGoodsInfoId
     */
    @ApiModelProperty(value = "parentGoodsInfoId")
    @NotBlank
    private String parentGoodsInfoId;


}
