package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 商品导出类
 */
@ApiModel
@Data
public class GoodsExportByTimeAndStausVO implements Serializable {

    private static final long serialVersionUID = 3292204659622996712L;
    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private String cate_name;

    /**
     * 编码
     */
    @ApiModelProperty(value = "goods_info的编码")
    private String erp_goods_info_no;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goods_info_name;

    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    private String shelflife;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private String goods_subtitle;

    /**
     * 市场价格
     */
    @ApiModelProperty(value = "市场价格")
    private String market_price;

    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌")
    private String brand_name;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态")
    private String del_flag;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private String warestock;


}
