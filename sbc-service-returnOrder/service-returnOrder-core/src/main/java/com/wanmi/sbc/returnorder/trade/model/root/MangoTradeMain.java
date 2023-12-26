package com.wanmi.sbc.returnorder.trade.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mango_trade_main")
public class MangoTradeMain implements Serializable {

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
     * 父订单号，用于组织批量订单合并支付，目前仅在支付与退款中使用。
     */
    private String parentId;

    /**
     * 活动类型(囤货->2;自提->1、普通订单->（物流发货）)
     */
    private Integer activityType = 0;

    /**
     * 审核状态(NON_CHECKED->未审核;CHECKED->已审核;REJECTED->已打回)
     */
    private String auditState = null;

    /**
     * 物流状态(INIT->创建订单;REMEDY->修改订单;AUDIT->已审核;DELIVERED_PART->部分发货;DELIVERED_PART->部分发货;DELIVERED->已发货;CONFIRMED->已确认;COMPLETED->已完成;VOID->已作废;GROUPON->已参团;TOPICKUP->待自提)
     */
    private String flowState = null;

    /**
     * 支付状态(NOT_PAID->未支付;UNCONFIRME->待确认;PAID->已支付)
     */
    private String payState = null;

    /**
     * 发货状态(NOT_YET_SHIPPED->未发货;SHIPPED->已发货;PART_SHIPPED->部分发货;VOID->作废)
     */
    private String deliverStatus = null;

    /**
     * 购买人编号
     */
    private String buyerId = null;

    /**
     * 会员在erp里的id
     */
    private String buyerErpId = null;

    /**
     * 购买人姓名
     */
    private String buyerName = null;

    /**
     * 账号
     */
    private String buyerAccount = null;

    /**
     * 买家手机号
     */
    private String buyerPhone = null;

    /**
     * 买家关联的业务员id
     */
    private String buyerEmployeeId = null;

    /**
     * 收货人 名字
     */
    private String consigneeName = null;

    /**
     * 收货人 电话
     */
    private String consigneePhone = null;

    /**
     * 配送方式(0->其他;1-> 物流;2->快递;3->自提;4->配送到家)
     */
    private String deliverWay = null;

    /**
     * 订单组号
     */
    private String groupId = null;

    /**
     * 商品总件数
     */
    private Long goodsTotalNum = 0l;

    /**
     * 订单评价状态(0->未评价;1->已评价)
     */
    private Integer orderEvaluateStatus = 0;

    /**
     * 订单来源(0->代客下单;1->会员h5端下单;2->会员pc端下单;3->会员APP端下单;4-> 会员小程序端下单)
     */
    private String orderSource = null;

    /**
     * 订单类型 (0->普通订单；1->积分订单；2-> 所有订单)
     */
    private String orderType = null;

    /**
     * 支付方式（UNIONPAY->银联;WECHAT->微信;ALIPAY->支付宝;ADVANCE->预存款;POINT->积分兑换;CASH->转账汇款;UNIONPAY_B2B->企业银联;COUPON->优惠券;BALANCE->余额）
     */
    private String payWay = null;

    /**
     * 订单来源方(BOSS->商户;CUSTOMER->客户;THIRD->第三方;SUPPLIER->商家;PLATFORM->平台;MALL->品牌商;PROVIDER->供应商)
     */
    private String platform = null;

    /**
     * 退款标识(0->正常；1->退款)
     */
    private String refundFlag = null;

    /**
     * 正在进行的退单数量
     */
    private Integer returnOrderNum = null;

    /**
     * 小店名称
     */
    private String shopName = null;

    /**
     * 商家编码
     */
    private String supplierSupplierCode = null;

    /**
     * 商家名称
     */
    private String supplierSupplierName = null;

    /**
     * 商家id
     */
    private String supplierSupplierId = null;

    /**
     * 店铺id
     */
    private String supplierStoreId = null;

    /**
     * 使用的运费模板类别(0:店铺运费,1:单品运费)
     */
    private String supplierFreightTemplateType = null;

    /**
     * 店铺名称
     */
    private String supplierStoreName = null;

    /**
     * 代理人Id，用于代客下单
     */
    private String supplierEmployeeId = null;

    /**
     * 商家在erp里的标志（目前只有个XYY）
     */
    private String supplierErpId = null;

    /**
     * 商家类型(PLATFORM->平台自营;SUPPLIER->第三方商家;UNIFIED->统仓统配)
     */
    private String supplierCompanyType = null;

    /**
     * 商品总金额
     */
    private BigDecimal priceGoodsPrice = new BigDecimal(0);

    /**
     * 配送费用
     */
    private BigDecimal priceDeliveryPrice = new BigDecimal(0);

    /**
     * 原始金额, 不作为付费金额
     */
    private BigDecimal priceOriginPrice = new BigDecimal(0);

    /**
     * 订单应付金额
     */
    private BigDecimal priceTotalPrice = new BigDecimal(0);

    /**
     * 订单实际支付金额
     */
    private BigDecimal priceTotalPayCash = new BigDecimal(0);

    /**
     * 平台佣金
     */
    private BigDecimal priceCmCommission = new BigDecimal(0);

    /**
     * 订单供货价总额
     */
    private BigDecimal priceOrderSupplyPrice = new BigDecimal(0);

    /**
     * 优惠券码id
     */
    private String couponCodeId = null;

    /**
     * 优惠券码值
     */
    private String couponCode = null;

    /**
     * 优惠券名称
     */
    private String couponName = null;

    /**
     * 优惠券类型
     */
    private String couponType = null;

    /**
     * 创建时间
     */
    private LocalDateTime createTime = null;

}