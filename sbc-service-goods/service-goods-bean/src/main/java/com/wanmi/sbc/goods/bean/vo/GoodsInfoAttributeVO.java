package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-13 15:03
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class GoodsInfoAttributeVO implements Serializable {
    private static final long serialVersionUID = -1408755367571188145L;

    /**
     * 商品SKU编号
     */
    @ApiModelProperty(value = "商品SKU编号")
    private String goodsInfoId;

    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    private String goodsId;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ApiModelProperty(value = "规格名称规格值", example = "颜色:红色;大小:16G")
    private String specText;

    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存")
    private BigDecimal stock;

    /**
     * 锁定库存
     */
    @ApiModelProperty(value = "锁定库存")
    private BigDecimal lockStock;

    @ApiModelProperty(value = "拆箱id")
    private Long devanningId;

    @ApiModelProperty(value = "是否可预售")
    private Integer isPresell = 0;

    @ApiModelProperty(value = "预售虚拟库存")
    private Long presellStock;
}
