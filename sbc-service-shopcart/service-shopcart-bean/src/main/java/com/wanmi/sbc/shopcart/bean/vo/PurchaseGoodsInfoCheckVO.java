package com.wanmi.sbc.shopcart.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 已勾选购物车商品
 * Created by yang on 2021/03/12
 */
@ApiModel
@Data
public class PurchaseGoodsInfoCheckVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拆箱编号
     */
    @ApiModelProperty(value = "拆箱编号")
    private Long devanningId;

    /**
     * sku编号
     */
    @ApiModelProperty(value = "sku编号")
    private String goodsInfoId;

    /**
     * 商品是否选中
     */
    @ApiModelProperty(value = "商品是否选中")
    private DefaultFlag isCheck;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Long buyCount = 0L;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private Long stock;

    /**
     * 商品状态
     */
    @ApiModelProperty(value = "商品状态")
    private GoodsStatus goodsStatus;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty(value = "设价类型", dataType = "com.wanmi.sbc.goods.bean.enums.PriceType")
    private Integer priceType;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价", notes = "为空，以市场价为准")
    private BigDecimal salePrice;

    /**
     * 预估佣金
     */
    @ApiModelProperty(value = "预估佣金")
    private BigDecimal distributionCommission;

    /**
     * erpSKU编码
     */
    @ApiModelProperty(value = "erpSKU编码")
    private String erpGoodsInfoNo;

    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    private BigDecimal vipPrice;

    /**
     * 是否零售商品 0否 1是
     */
    @ApiModelProperty(value = "是否零售商品 0否 1是")
    private Integer isSupermarketGoods = 0;
}
