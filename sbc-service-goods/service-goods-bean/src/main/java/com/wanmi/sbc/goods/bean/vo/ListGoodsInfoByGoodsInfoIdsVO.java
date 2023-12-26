package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SKU实体类
 * Created by chenchang on 2022/9/22.
 */
@ApiModel
@Data
@Accessors(chain = true)
public class ListGoodsInfoByGoodsInfoIdsVO implements Serializable {

    private static final long serialVersionUID = 6429788013325287483L;

    @ApiModelProperty(value = "SKU id")
    private String goodsInfoId;

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
}