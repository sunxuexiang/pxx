package com.wanmi.sbc.marketing.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>获取营销优惠时传入的订单商品信息参数封装</p>
 * Created by of628-wenzhi on 2018-03-14-下午6:02.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeItemInfoDTO implements Serializable {

    private static final long serialVersionUID = -4632043243598392566L;

    /**
     * 单品id
     */
    @ApiModelProperty(value = "单品sku id")
    private String skuId;

    /**
     * 拆箱id
     */
    @ApiModelProperty(value = "拆箱id")
    private Long devanningId;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品spu id")
    private String spuId;

    /**
     * 商品价格（计算区间价或会员价后的应付金额）
     */
    @ApiModelProperty(value = "商品价格（计算区间价或会员价后的应付金额）")
    private BigDecimal price;


    /**
     * 商品的总价已经为数量乘以市场价
     */
    @ApiModelProperty(value = "商品的总价已经为数量乘以市场价")
    private BigDecimal allMarketPrice;



    /**
     * 商品的总价已经为数量乘以市场价
     */
    @ApiModelProperty(value = "商品的总价已经为数量乘以市场价")
    private BigDecimal allVipPrice;


    /**
     * 商品购买数量
     */
    @ApiModelProperty(value = "商品购买数量")
    private Long num;

    /**
     * 倍数
     */
    private BigDecimal divisorFlag;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 商品品牌id
     */
    @ApiModelProperty(value = "商品品牌id")
    private Long brandId;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private Long cateId;

    /**
     * 店铺分类
     */
    @ApiModelProperty(value = "店铺分类")
    private List<Long> storeCateIds;

    /**
     * 分销商品审核状态
     */
    private DistributionGoodsAudit distributionGoodsAudit;


    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    @ApiModelProperty("商品状态（0：普通商品，1：特价商品）")
    private  Integer goodsInfoType;

    /**
     *批次号
     */
    @ApiModelProperty("批次号")
    private String goodsBatchNo;

    /**
     * 分销佣金
     */
    @ApiModelProperty(value = "分销佣金")
    private BigDecimal distributionCommission;

    /**
     * 销售类型 0批发 1零售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售")
    private SaleType saleType = SaleType.WHOLESALE;
}
