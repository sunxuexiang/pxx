package com.wanmi.sbc.order.trade.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mango_pile_trade_plus")
public class MangoPileTradePlus implements Serializable {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号对应mango：mango_id
     */
    private String orderNo;

    /**
     * 买家备注
     */
    private String buyerRemark = null;

    /**
     * 预约发货时间
     */
    private String bookingDate = null;

    /**
     * 收货人 省
     */
    private String consigneeProvinceId = null;

    /**
     * 收货人 市
     */
    private String consigneeCityId = null;

    /**
     * 收货人 区
     */
    private String consigneeAreaId = null;

    /**
     * 收货人 详细地址
     */
    private String consigneeAddress = null;

    /**
     * 收货人 期望收获时间
     */
    private LocalDateTime consigneeExpectTime = null;

    /**
     * 调用方的请求 IP
     */
    private String requestIp = null;

    /**
     * 开店礼包（NO->否；YES->是）
     */
    private String storeBagsFlag = null;

    /**
     * 店铺服务评价状态（0:->未评价；1-> 已评价）
     */
    private String storeEvaluate = null;

    /**
     * 配送费用
     */
    private BigDecimal priceDeliveryPrice = new BigDecimal(0);

    /**
     * 特价金额
     */
    private BigDecimal pricePrivilegePrice = new BigDecimal(0);

    /**
     * 优惠金额
     */
    private BigDecimal priceDiscountsPrice = new BigDecimal(0);

    /**
     * 积分数量
     */
    private Integer priceIntegral = 0;

    /**
     * 积分兑换金额
     */
    private BigDecimal priceIntegralPrice = new BigDecimal(0);

    /**
     * 积分
     */
    private Long pricePoints = 0l;

    /**
     * 积分兑换金额
     */
    private BigDecimal pricePointsPrice = new BigDecimal(0);

    /**
     * 积分价值
     */
    private Long pricePointWorth = 0l;

    /**
     * 是否特价单
     */
    private String priceSpecial = null;

    /**
     * 是否开启运费
     */
    private String priceEnableDeliveryPrice = null;

    /**
     * 运费优惠金额
     */
    private BigDecimal priceDeliveryCouponPrice = new BigDecimal(0);

    /**
     * 余额支付金额（用于组合支付）
     */
    private BigDecimal priceBalancePrice = new BigDecimal(0);

    /**
     * 在线支付金额（用于组合支付）
     */
    private BigDecimal priceOnlinePrice = new BigDecimal(0);

    /**
     * 优惠券关联的商品id集合
     */
    private String couponGoodsIds = null;

    /**
     * 优惠金额
     */
    private BigDecimal couponDiscountsAmount = new BigDecimal(0);

    /**
     * 购满多少钱
     */
    private BigDecimal couponFullBuyPrice = new BigDecimal(0);

    /**
     * 创建时间
     */
    private LocalDateTime createTime = null;

}