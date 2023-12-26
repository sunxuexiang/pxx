package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeItemDTO implements Serializable {

    private static final long serialVersionUID = 2973899410241708605L;

    @ApiModelProperty(value = "订单标识")
    private String oid;

    /**
     * 商品所属的userId storeId?
     */
    @ApiModelProperty(value = "商品所属的userId")
    private String adminId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 商家编码
     */
    @ApiModelProperty(value = "商家编码")
    private String supplierCode;

    @ApiModelProperty(value = "spu Id")
    private String spuId;

    @ApiModelProperty(value = "spu 名称")
    private String spuName;

    @ApiModelProperty(value = "sku Id")
    private String skuId;

    /**
     * 商品skuId
     */
    @ApiModelProperty("拆箱商品skuId")
    private Long devanningId;

    @ApiModelProperty(value = "sku 名字")
    private String skuName;

    @ApiModelProperty(value = "sku 编码")
    private String skuNo;

    @ApiModelProperty(value = "厂库id")
    private Long wareId;

    @ApiModelProperty(value = "映射表伪spuid")
    private String parentGoodsInfoId;

    /**
     * 商品重量
     */
    @ApiModelProperty(value = "商品重量")
    private BigDecimal goodsWeight;

    /**
     * 商品体积 单位：m3
     */
    @ApiModelProperty(value = "商品体积", notes = "单位：m3")
    private BigDecimal goodsCubage;

    /**
     * 运费模板ID
     */
    @ApiModelProperty(value = "运费模板ID")
    private Long freightTempId;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private Long cateId;

    @ApiModelProperty(value = "分类名称")
    private String cateName;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String pic;

    /**
     * 商品品牌
     */
    @ApiModelProperty(value = "商品品牌")
    private Long brand;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    @Min(1L)
    private Long num;


    /**
     * 金钱类型的购买数量
     */
    private BigDecimal bNum;

    /**
     * 倍数
     */
    private BigDecimal divisorFlag;

    /**
     * 已发货数量
     */
    @ApiModelProperty(value = "已发货数量")
    private Long deliveredNum = 0L;

    /**
     * 发货状态
     */
    @ApiModelProperty(value = "发货状态")
    private DeliverStatus deliverStatus;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 成交价格
     */
    @ApiModelProperty(value = "成交价格")
    private BigDecimal price;

    /**
     * 更改的成交单价
     */
    @ApiModelProperty(value = "更改的成交单价")
    private BigDecimal changedPrice;

    /**
     * 商品原价 - 建议零售价
     */
    @ApiModelProperty(value = "商品原价 建议零售价")
    private BigDecimal originalPrice;

    /**
     * 商品价格 - 会员价 & 阶梯设价
     */
    @ApiModelProperty(value = "商品价格 会员价 & 阶梯设价")
    private BigDecimal levelPrice;

    /**
     * 成本价
     */
    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    /**
     * 平摊小计 - 最初由 levelPrice*num（购买数量） 计算
     */
    @ApiModelProperty(value = "平摊小计 最初由 levelPrice*num（购买数量） 计算")
    private BigDecimal splitPrice;

    /**
     * 货物id
     * added by shenchunnan
     */
    @ApiModelProperty(value = "货物id")
    private String bn;

    /**
     * 可退数量
     */
    @ApiModelProperty(value = "可退数量")
    private Integer canReturnNum;

    /**
     * 规格描述信息
     */
    @ApiModelProperty(value = "规格描述信息")
    private String specDetails;

    /**
     * 分类扣率
     */
    @ApiModelProperty(value = "分类扣率")
    private BigDecimal cateRate;

    /**
     * 分销商品审核状态
     */
    @ApiModelProperty(value = "分销商品审核状态")
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销佣金
     */
    @ApiModelProperty(value = "分销佣金")
    private BigDecimal distributionCommission;

    /**
     * 商品参加的营销活动id集合
     */
    @ApiModelProperty(value = "商品参加的营销活动id集合")
    private List<Long> marketingIds = new ArrayList<>();

    /**
     * 营销商品结算信息
     */
    @ApiModelProperty(value = "营销商品结算信息")
    private List<MarketingSettlementDTO> marketingSettlements;

    /**
     * 优惠券商品结算信息(包括商品参加的优惠券信息)
     */
    @ApiModelProperty(value = "优惠券商品结算信息", notes = "包括商品参加的优惠券信息")
    private List<CouponSettlementDTO> couponSettlements = new ArrayList<>();

    /**
     * 积分
     */
    @ApiModelProperty(value = "积分")
    private Long points;

    /**
     * 积分商品Id
     */
    @ApiModelProperty(value = "积分商品Id")
    private String pointsGoodsId;

    /**
     * 结算价格
     */
    @ApiModelProperty(value = "结算价格")
    private BigDecimal settlementPrice;

    /**
     * 是否是秒杀抢购商品
     */
    @ApiModelProperty(value = "是否是秒杀抢购商品")
    private Boolean isFlashSaleGoods;

    /**
     * 秒杀抢购商品Id
     */
    private Long flashSaleGoodsId;

    /**
     * 供应商id
     */
    private Long providerId;

    /**
     * 供货价
     */
    private BigDecimal supplyPrice;

    /**
     * 供货价总额
     */
    private BigDecimal totalSupplyPrice;

    /**
     * 供应商名称
     */
    private String providerName;

    /**
     * 供应商编号
     */
    private String providerCode;

    /**
     *批次号
     */
    private String goodsBatchNo;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    private  Integer goodsInfoType;

    /**
     * 销售单位
     */
    private String saleUnit;

    /**
     * 特价
     */
    private BigDecimal specialPrice;

    /**
     * erp编码
     */
    private String erpSkuNo;

    /**
     * 步长
     */
    private BigDecimal addStep;

    /**
     * 余额结算信息
     */
    private List<WalletSettlementDTO> walletSettlements = new ArrayList<>();

    /**
     * 返鲸币数量
     */
    private BigDecimal returnCoin;

    /**
     * @desc  成交价格改过 1是 0没有
     * @author shiy  2023/7/18 10:20
     */
    private Integer priceChanged;


    /**
     * 余额商品结算Bean
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WalletSettlementDTO implements Serializable{

        /**
         * 除去余额金额后的商品均摊价
         */
        private BigDecimal splitPrice;

        /**
         * 余额扣减金额/被扣减余额
         */
        private BigDecimal reduceWalletPrice;
    }

//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }

    /**
     * 营销优惠商品结算Bean
     */
    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Builder
    public static class MarketingSettlementDTO implements Serializable {

        private static final long serialVersionUID = -1739585650762904572L;

        /**
         * 营销类型
         */
        @ApiModelProperty(value = "营销类型", notes = "0: 满减优惠, 1: 满折优惠, 2: 满赠优惠")
        private MarketingType marketingType;

        /**
         * 除去营销优惠金额后的商品均摊价
         */
        @ApiModelProperty(value = "除去营销优惠金额后的商品均摊价")
        private BigDecimal splitPrice;
    }

    /**
     * 优惠券优惠商品结算Bean
     */
    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Builder
    public static class CouponSettlementDTO implements Serializable {

        private static final long serialVersionUID = -5694365272429246275L;

        /**
         * 优惠券码id
         */
        @ApiModelProperty(value = "优惠券码id")
        private String couponCodeId;

        /**
         * 优惠券码
         */
        @ApiModelProperty(value = "优惠券码")
        private String couponCode;

        /**
         * 优惠券类型
         */
        @ApiModelProperty(value = "优惠券类型", notes = "0通用券 1店铺券 2运费券")
        private CouponType couponType;

        /**
         * 除去优惠金额后的商品均摊价
         */
        @ApiModelProperty(value = "除去优惠金额后的商品均摊价")
        private BigDecimal splitPrice;

        /**
         * 优惠金额
         */
        @ApiModelProperty(value = "优惠金额")
        private BigDecimal reducePrice;

    }
    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;

    /**
     * 提货关联关系
     */
    @ApiModelProperty(value = "提货关联关系")
    private List<PickGoodsDTO> pickGoodsList;

    /**
     * 每个商品的价格
     */
    private List<InventoryDetailSamountTradeDTO> inventoryDetailSamountTrades = new ArrayList<>();

    /**
     * 每个商品的价格 (提货单)
     */
    private List<InventoryDetailSamountVO> inventoryDetailSamount = new ArrayList<>();
}
