package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>参与囤货活动商品</p>
 * author: chenchang
 * Date: 2022-09-19
 */
@ApiModel
@Data
public class PileActivityGoodsPageVO implements Serializable {

    private static final long serialVersionUID = -1876493701433382583L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "SKU编码")
    private String goodsInfoNo;

    @ApiModelProperty(value = "ERP编码")
    private String erpNo;

    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    @ApiModelProperty(value = "分类名称")
    private String cateName;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "分类id")
    private Long cateId;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal goodsInfoPrice;

    @ApiModelProperty(value = "虚拟库存")
    private Long virtualStock;

}
