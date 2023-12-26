package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse
 * 根据编号查询商品视图信息响应对象
 * @author lipeng
 * @dateTime 2018/11/5 上午9:39
 */
@ApiModel
@Data
public class GoodsViewByIdResponse implements Serializable {

    private static final long serialVersionUID = -6641896293423917872L;

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
     * （新）商品SKU的规格值全部数据
     */
    @ApiModelProperty(value = "商品SKU的规格值全部数据")
    private List<GoodsAttributeKeyVO> goodsAttribute;
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
     * 商品详情模板关联
     */
    @ApiModelProperty(value = "商品详情模板关联")
    private List<GoodsTabRelaVO> goodsTabRelas;

    /**
     * 商品模板配置
     */
    @ApiModelProperty(value = "商品模板配置")
    private List<StoreGoodsTabVO> storeGoodsTabs;

    /**
     * 是否是分销商品
     */
    @ApiModelProperty(value = "是否是分销商品")
    private Boolean distributionGoods;

    /**
     * 拼团活动
     */
    @ApiModelProperty(value = "拼团活动")
    private Boolean grouponFlag;

    /**
     * 拆箱商品信息
     */
    @ApiModelProperty(value = "商品信息")
    private List<DevanningGoodsInfoVO> devanningGoodsInfoVO;



    @ApiModelProperty(value = "限购区域已经购买数量")
    private BigDecimal AlreadyNum;

    @ApiModelProperty(value = "限购区域总数量")
    private Long AllNum;


    @ApiModelProperty(value = "营销限购用户已经购买数量")
    private BigDecimal AlreadyMarketingNum;

    @ApiModelProperty(value = "营销限购总数量")
    private Long AllMarketingNum;

    @ApiModelProperty(value = "营销限购个人总数量")
    private Long AllMarkeingUserNum;

    @ApiModelProperty(value = "该商品已经选择的营销")
    private Long MarktingId;

    /**
     * 促销图片和合成图片合集
     */
    private List<GoodsImageStypeVO> goodsImageStypeVOS;

    /**
     * 合成图片与促销图片与image的关系
     */
    private RelationGoodsImagesVO relationGoodsImagesVO;

}
