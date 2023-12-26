package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.goods.GoodsModifyRequest
 * 修改商品请求对象
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午10:23
 */
@ApiModel
@Data
public class GoodsModifyRequest implements Serializable {

    private static final long serialVersionUID = 8511009723657831203L;

    /**
     * 商品信息
     */
    @ApiModelProperty(value = "商品信息")
    private GoodsVO goods;

    /**
     * 商品相关图片
     */
    @ApiModelProperty(value = "商品相关图片")
    private List<GoodsImageVO> images;

    /**
     * 商品属性列表
     */
    @ApiModelProperty(value = "商品属性列表")
    private List<GoodsPropDetailRelVO> goodsPropDetailRels;

    /**
     * 商品规格列表
     */
    @ApiModelProperty(value = "商品规格列表")
    private List<GoodsSpecVO> goodsSpecs;

    /**
     * 商品规格值列表
     */
    @ApiModelProperty(value = "商品规格值列表")
    private List<GoodsSpecDetailVO> goodsSpecDetails;

    /**
     * 商品SKU列表
     */
    @ApiModelProperty(value = "商品SKU列表")
    private List<GoodsInfoVO> goodsInfos;

    /**
     * 商品SKU列表(新增属性列表)
     */
    @ApiModelProperty(value = "商品SKU列表")
    private List<GoodsInfoVO> addGoodsInfos;

    /**
     * 商品等级价格列表
     */
    @ApiModelProperty(value = "商品等级价格列表")
    private List<GoodsLevelPriceVO> goodsLevelPrices;

    /**
     * 商品客户价格列表
     */
    @ApiModelProperty(value = "商品客户价格列表")
    private List<GoodsCustomerPriceVO> goodsCustomerPrices;

    /**
     * 商品订货区间价格列表
     */
    @ApiModelProperty(value = "商品订货区间价格列表")
    private List<GoodsIntervalPriceVO> goodsIntervalPrices;

    /**
     * 是否修改价格及订货量设置
     */
    @ApiModelProperty(value = "是否修改价格及订货量设置", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer isUpdatePrice;

    /**
     * 商品详情模板关联
     */
    @ApiModelProperty(value = "商品详情模板关联")
    private List<GoodsTabRelaVO> goodsTabRelas;

    /**
     * 类型图片集合
     */
    @ApiModelProperty(value = "类型图片集合")
    private List<GoodsImageStypeVO> goodsImageVOS;

    /**
     * 选中的商品id
     */
    @ApiModelProperty(value = "选中的商品id")
    private Long checkImageId;

}
