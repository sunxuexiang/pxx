package com.wanmi.sbc.returnorder.trade.model.entity;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.returnorder.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.EvaluateStatus;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.returnorder.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
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

/**
 * 订单商品
 * Created by jinwei on 19/03/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeItem implements Serializable, Cloneable {

    private static final long serialVersionUID = 2973899410241708605L;
    private String oid;

    /**
     * 商品所属的userId storeId?
     */
    private String adminId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商家编码
     */
    private String supplierCode;

    private String spuId;

    private String spuName;

    private String skuId;

    private Long devanningId;

    private String skuName;

    private String skuNo;


    private Long wareId;


    private String parentGoodsInfoId;


    /**
     * 商品重量
     */
    private BigDecimal goodsWeight;
    /**
     * 商品体积 单位：m3
     */
    private BigDecimal goodsCubage;
    /**
     * 运费模板ID
     */
    private Long freightTempId;
    /**
     * 分类
     */
    private Long cateId;

    private String cateName;


    /**
     * 商品的总价已经为数量乘以市场价
     */

    private BigDecimal allMarketPrice;


    /**
     * 商品的总价已经为数量乘以市场价
     */

    private BigDecimal allVipPrice;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 商品品牌
     */
    private Long brand;

    /**
     * 购买数量
     */
    @Min(1L)
    private Long num;

    /**
     * 购买数量 金钱类型
     */
    private BigDecimal bNum;

    /**
     * 倍数
     */
    private BigDecimal divisorFlag;

    /**
     * 已发货数量
     */
    private Long deliveredNum = 0L;

    /**
     * 发货状态
     */
    private DeliverStatus deliverStatus;

    /**
     * 基础单位
     */
    private String unit;

    /**
     * 成交价格
     */
    private BigDecimal price;

    /**
     * 更改的成交单价
     */
    @ApiModelProperty(value = "更改的成交单价")
    private BigDecimal changedPrice;

    /**
     * 商品原价 - 建议零售价
     */
    private BigDecimal originalPrice;

    /**
     * 商品价格 - 会员价 & 阶梯设价
     */
    private BigDecimal levelPrice;

    /**
     * 大客户价格
     */
    private BigDecimal vipPrice;

    /**
     * 成本价
     */
    private BigDecimal cost;

    /**
     * 平摊小计 - 最初由 levelPrice*num（购买数量） 计算
     */
    private BigDecimal splitPrice;

    /**
     * 货物id
     * added by shenchunnan
     */
    private String bn;

    /**
     * 可退数量
     */
    private BigDecimal canReturnNum;

    /**
     * 规格描述信息
     */
    private String specDetails;

    /**
     * 分类扣率
     */
    private BigDecimal cateRate;

    /**
     * 分销商品审核状态
     */
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销佣金（返利人）
     */
    private BigDecimal distributionCommission;

    /**
     * 佣金比例（返利人）
     */
    private BigDecimal commissionRate;

    /**
     * 商品参加的营销活动id集合
     */
    private List<Long> marketingIds = new ArrayList<>();

    /**
     * 营销商品结算信息
     */
    private List<MarketingSettlement> marketingSettlements;

    /**
     * 优惠券商品结算信息(包括商品参加的优惠券信息)
     */
    private List<CouponSettlement> couponSettlements = new ArrayList<>();

    /**
     * 余额结算信息
     */
    private List<WalletSettlement> walletSettlements = new ArrayList<>();

    /**
     * 每个商品的价格
     */
    private List<InventoryDetailSamountTrade> inventoryDetailSamountTrades = new ArrayList<>();

    /**
     * 每个商品的价格 提货单
     */
    private List<InventoryDetailSamountVO> inventoryDetailSamount = new ArrayList<>();

    /**
     * 商品评价状态
     */
    private EvaluateStatus goodsEvaluateStatus = EvaluateStatus.NO_EVALUATE;

    /**
     * 积分
     */
    private Long points;

    /**
     * 积分兑换金额
     */
    private BigDecimal pointsPrice;

    /**
     * 积分商品Id
     */
    private String pointsGoodsId;

    /**
     * 结算价格
     */
    private BigDecimal settlementPrice;

    /**
     * 企业购商品的审核状态
     */
    private EnterpriseAuditState enterPriseAuditState;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    private Integer goodsInfoType;


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 营销优惠商品结算Bean
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketingSettlement implements Serializable {

        private static final long serialVersionUID = -1739585650762904572L;

        /**
         * 营销类型
         */
        private MarketingType marketingType;

        /**
         * 除去营销优惠金额后的商品均摊价
         */
        private BigDecimal splitPrice;

        /**
         * 营销优惠金额
         */
        private BigDecimal marketAmount;
    }

    /**
     * 优惠券优惠商品结算Bean
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CouponSettlement implements Serializable {

        private static final long serialVersionUID = -5694365272429246275L;

        /**
         * 优惠券码id
         */
        private String couponCodeId;

        /**
         * 优惠券码
         */
        private String couponCode;

        /**
         * 优惠券类型
         */
        private CouponType couponType;

        /**
         * 除去优惠金额后的商品均摊价
         */
        private BigDecimal splitPrice;

        /**
         * 优惠金额
         */
        private BigDecimal reducePrice;

    }

    /**
     * 余额商品结算Bean
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WalletSettlement implements Serializable {

        /**
         * 除去余额金额后的商品均摊价
         */
        private BigDecimal splitPrice;

        /**
         * 余额扣减金额/被扣减余额
         */
        private BigDecimal reduceWalletPrice;
    }

    /**
     * 是否是秒杀抢购商品
     */
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
     * 批次号
     */
    private String goodsBatchNo;

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
     * 商品副标题
     */
    private String goodsSubtitle;

    /**
     * 已使用数量
     */
    @Min(0L)
    private Long useNum;

    /**
     * 商品囤货订单号
     */
    private String pileOrderCode;

    /**
     * 税率
     * 新增字段
     */
    private BigDecimal FEntryTaxRate;

    /**
     * 囤货已支付金额（总）
     */
    private BigDecimal paidGoodsPrice;

    /**
     * 提货关联关系
     */
    private List<PickGoodsDTO> pickGoodsList;


    /**
     * 返鲸币数量
     */
    private BigDecimal returnCoin;

    /**
     * @desc  成交价格改过 1是 0没有
     * @author shiy  2023/7/18 10:20
    */
    private Integer priceChanged;
}
