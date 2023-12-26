package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Accessors(chain = true)
public class GoodsInfoNewVO implements Serializable {
    private static final long serialVersionUID = -1408755367571188145L;

    /**
     * 商品SKU编号
     */
    @Id
    @ApiModelProperty(value = "商品SKU编号")
    private String goodsInfoId;

    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    private String goodsId;

    /**
     * 商品SKU名称
     */
    @ApiModelProperty(value = "商品SKU名称")
    private String goodsInfoName;

    /**
     * 商品SKU编码
     */
    @ApiModelProperty(value = "商品SKU编码")
    private String goodsInfoNo;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    @ApiModelProperty(value = "商家类型", notes = "0、平台自营 1、第三方商家")
    private CompanyType companyType;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ApiModelProperty(value = "商品状态", notes = "0：正常 1：缺货 2：失效")
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    /**
     * 促销标签
     */
    @ApiModelProperty(value = "促销标签，折扣")
    private List<MarketingLabelVO> marketingLabels = new ArrayList<>();

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价", notes = "为空，以市场价为准")
    private BigDecimal salePrice;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    private BigDecimal marketPrice;

    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    private BigDecimal vipPrice;

    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存")
    private Long stock;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;

    /**
     * 商品分类，0散称，1定量
     */
    @ApiModelProperty(value = "商品分类")
    private int isScatteredQuantitative;

    /**
     * 商品保质期
     */
    @ApiModelProperty(value = "商品保质期")
    private int shelflife;

    /**
     * 购买量
     */
    @ApiModelProperty(value = "购买量")
    private Long buyCount = 0L;

    /**
     * 优惠券标签
     */
    @ApiModelProperty(value = "优惠券标签")
    private List<CouponLabelVO> couponLabels = new ArrayList<>();

    /**
     * 商品绑定的规格数据
     */
    @ApiModelProperty(value = "商品绑定的规格数据")
    private List<GoodsLabelVO> goodsLabels = new ArrayList<>();

    /**
     * 询价标志 0-否，1-是
     */
    @ApiModelProperty(value = "询价标志 0-否，1-是")
    private Integer inquiryFlag;

}
