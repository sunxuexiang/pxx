package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体类
 * Created by dyt on 2020/12/29.
 */
@ApiModel
@Data
@Accessors(chain = true)
public class GoodsInfoPurchaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 商品SKU名称
     */
    @ApiModelProperty(value = "商品SKU名称")
    private String goodsInfoName;

    /**
     * erpSKU编码
     */
    @ApiModelProperty(value = "erpSKU编码")
    private String erpGoodsInfoNo;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存")
    private Long stock;

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
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 购买量
     */
    @ApiModelProperty(value = "购买量")
    private Long buyCount = 0L;

    /**
     * 最新计算的起订量
     * 为空，则不限
     */
    @ApiModelProperty(value = "最新计算的起订量", notes = "为空，则不限")
    private Long count;

    /**
     * 最新计算的限定量
     * 为空，则不限
     */
    @ApiModelProperty(value = "最新计算的限定量", notes = "为空，则不限")
    private Long maxCount;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ApiModelProperty(value = "规格名称规格值", example = "颜色:红色;大小:16G")
    private String specText;

    /**
     * 预估佣金
     */
    @ApiModelProperty(value = "预估佣金")
    private BigDecimal distributionCommission;

    /**
     * 分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
     */
    @ApiModelProperty(value = "分销商品审核状态")
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ApiModelProperty(value = "商品状态", notes = "0：正常 1：缺货 2：失效")
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    @ApiModelProperty(value = "商品状态")
    private  Integer goodsInfoType;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;

    @ApiModelProperty(value = "设价类型", dataType = "com.wanmi.sbc.goods.bean.enums.PriceType")
    private Integer priceType;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价", notes = "为空，以市场价为准")
    private BigDecimal salePrice;

    /**
     * 购物车商品是否选中
     */
    @ApiModelProperty(value = "购物车商品是否选中")
    private DefaultFlag isCheck = DefaultFlag.YES;

    /**
     * 关联活动限购营销id
     */
    @ApiModelProperty(value = "关联活动限购营销id")
    private Long marketingId;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;
}