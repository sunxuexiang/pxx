package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SpecialGoodsExportVo implements Serializable {

    private static final long serialVersionUID = 6921974210057741365L;

    /**
     * 商品编号，采用UUID
     */

    /**
     *商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsImg;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * SPU编码
     */
    @ApiModelProperty(value = "SPU编码")
    private String goodsNo;


    /**
     * SKU编码
     */
    @ApiModelProperty(value = "SKU编码")
    private String goodsInfoNo;

    /**
     * 销售类别
     */
    @ApiModelProperty(value = "销售类别", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 销售类别
     */
    @ApiModelProperty(value = "销售类别")
    private String saleTypeStr;

    /**
     *门店价
     */
    @ApiModelProperty(value = "门店价")
    private BigDecimal marketPrice;

    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     *商品类目
     */
    @ApiModelProperty(value = "商品类目")
    private String cateName;

    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌")
    private String brandName;

    /**
     * 上下架
     */
    @ApiModelProperty(value = "上下架")
    private Integer addedFlag;

    /**
     * 上下架
     */
    @ApiModelProperty(value = "上下架")
    private String addedFlagStr;
    /**
     * 特价
     */
    @ApiModelProperty(value = "特价")
    private BigDecimal specialPrice;

    /**
     * 商家
     */
    @ApiModelProperty(value = "商家")
    private String supplierName;
}
