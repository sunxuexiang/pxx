package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName GoodsWareStockPageVO
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/4/10 16:42
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    /**
     * 商品分类名称
     */
    @ApiModelProperty(value = "商品分类名称")
    private String cateName;

    /**
     * 商品品牌名称
     */
    @ApiModelProperty(value = "商品品牌名称")
    private String brandName;

    /**
     * 商品属性
     */
    @ApiModelProperty(value = "商品属性")
    private String specName;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    private Long stock;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * spu ID
     */
    @ApiModelProperty(value = "spu ID")
    private String goodsId;

    /**
     * 分类 Id
     */
    @ApiModelProperty(value = "分类 ID")
    private String cateId;

    /**
     * 品牌 ID
     */
    @ApiModelProperty(value = "品牌 ID")
    private String brandId;
}
