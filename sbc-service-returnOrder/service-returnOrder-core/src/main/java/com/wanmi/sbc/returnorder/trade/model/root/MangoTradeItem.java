package com.wanmi.sbc.returnorder.trade.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mango_trade_item")
public class MangoTradeItem implements Serializable {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     *
     */
    private String oid = null;

    /**
     * 商品所属的userId storeId?
     */
    private String adminId = null;

    /**
     * 店铺id
     */
    private String storeId = null;

    /**
     * 商家编码
     */
    private String supplierCode = null;

    /**
     *
     */
    private String spuId = null;

    /**
     *
     */
    private String spuName = null;

    /**
     *
     */
    private String skuId = null;

    /**
     *
     */
    private String skuName = null;

    /**
     *
     */
    private String skuNo = null;

    /**
     * 商品重量
     */
    private BigDecimal goodsWeight = new BigDecimal(0);

    /**
     * 商品体积 单位：m3
     */
    private String goodsCubage = null;

    /**
     * 运费模板ID
     */
    private String freightTempId = null;

    /**
     * 分类
     */
    private String cateId = null;

    /**
     *
     */
    private String cateName = null;

    /**
     * 商品图片
     */
    private String pic = null;

    /**
     * 商品品牌
     */
    private String brand = null;

    /**
     * 购买数量
     */
    private Integer num = null;

    /**
     * 已发货数量
     */
    private Integer deliveredNum = 0;

    /**
     * 发货状态
     */
    private String deliverStatus = null;

    /**
     * 基础单位
     */
    private String unit = null;

    /**
     * 成交价格
     */
    private BigDecimal price = new BigDecimal(0);

    /**
     * 品原价 - 建议零售价
     */
    private BigDecimal originalPrice = new BigDecimal(0);

    /**
     * 商品价格 - 会员价 & 阶梯设价
     */
    private BigDecimal levelPrice = new BigDecimal(0);

    /**
     * 大客户价格
     */
    private BigDecimal vipPrice = new BigDecimal(0);

    /**
     * 成本价
     */
    private BigDecimal cost = new BigDecimal(0);

    /**
     * 平摊小计 - 最初由 levelPrice*num（购买数量） 计算
     */
    private BigDecimal splitPrice = new BigDecimal(0);

    /**
     * 货物id
     */
    private String bn = null;

    /**
     * 可退数量
     */
    private Integer canReturnNum = 0;

    /**
     * 规格描述信息
     */
    private String specdDetails = null;

    /**
     * 分类扣率
     */
    private String cateRate = null;

    /**
     * 商品参加的营销活动id集合
     */
    private String marketingIds = null;

    /**
     * 营销信息
     */
    private String marketingSettlements = null;

    /**
     * 除去营销优惠金额后的商品均摊价
     */
    private BigDecimal splitPriceReal = new BigDecimal(0);

    /**
     * 商品评价状态
     */
    private String goodsEvaluateStatus = null;

    /**
     * 积分
     */
    private String points = null;

    /**
     * 积分兑换金额
     */
    private BigDecimal pointsPrice = new BigDecimal(0);

    /**
     * 积分商品Id
     */
    private String pointsGoodsId = null;

    /**
     * 结算价格
     */
    private BigDecimal settlementPrice = new BigDecimal(0);

    /**
     * 企业购商品的审核状态
     */
    private String enterPriseAuditState = null;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    private String goodsInfoType = null;

    /**
     * 优惠券
     */
    private String couponSettlements = null;

    /**
     * 是否是秒杀抢购商品
     */
    private String isFlashSaleGoods = null;

    /**
     * 秒杀抢购商品Id
     */
    private String flashSaleGoodsId = null;

    /**
     * 供应商id
     */
    private String providerId = null;

    /**
     * 供货价
     */
    private BigDecimal supplyPrice = new BigDecimal(0);

    /**
     * 供货价总额
     */
    private BigDecimal totalSupplyPrice = new BigDecimal(0);

    /**
     * 供应商名称
     */
    private String providerName = null;

    /**
     * 供应商编号
     */
    private String providerCode = null;

    /**
     * 批次号
     */
    private String goodsBatchNo = null;

    /**
     * 销售单位
     */
    private String saleUnit = null;

    /**
     * 特价
     */
    private BigDecimal specialPrice = new BigDecimal(0);

    /**
     * erp编码
     */
    private String erpSkuNo = null;

    /**
     * 步长
     */
    private String addsStep = null;

    /**
     * 商品副标题
     */
    private String goodsSubtitle = null;

    /**
     * 已使用数量
     */
    private Integer useNum = 0;

    /**
     * 商品囤货订单号
     */
    private String pileOrderCode = null;

    /**
     * 创建时间
     */
    private LocalDateTime createTime = null;
}
