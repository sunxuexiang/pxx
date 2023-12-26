package com.wanmi.sbc.shopcart.api.response.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-07
 */
@Data
@ApiModel
public class PurchaseCountGoodsResponse implements Serializable {

    private static final long serialVersionUID = 6248671223640691297L;

    @ApiModelProperty(value = "商品总数",example = "0")
    private Integer total;

    @ApiModelProperty(value = "（囤货）购物车商品总数",example = "0")
    private Long pileTotal;

    @ApiModelProperty(value = "（正常）购物车商品总数",example = "0")
    private Long shopCartTotal;

}
