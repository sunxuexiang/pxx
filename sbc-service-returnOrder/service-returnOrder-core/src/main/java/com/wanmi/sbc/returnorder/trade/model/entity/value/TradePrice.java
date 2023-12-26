package com.wanmi.sbc.returnorder.trade.model.entity.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单金额归总
 * Created by jinwei on 19/03/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradePrice implements Serializable {

    /**
     * 商品总金额
     */
    private BigDecimal goodsPrice;

    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    private BigDecimal deliveryPrice;

    /**
     * 特价金额，可以从TradePriceInfo获取
     */
    private BigDecimal privilegePrice;

    /**
     * 优惠金额
     */
    private BigDecimal discountsPrice;

    /**
     * 积分数量，可以从TradePriceInfo获取
     */
    private Integer integral;

    /**
     * 积分兑换金额，可以从TradePriceInfo获取
     */
    private BigDecimal integralPrice;

    /**
     * 积分
     */
    private Long points;

    /**
     * 积分兑换金额
     */
    private BigDecimal pointsPrice;

    /**
     * 积分价值
     */
    private Long pointWorth;

    /**
     * 是否特价单
     */
    private boolean special;

    /**
     * 是否开启运费
     */
    private boolean enableDeliveryPrice;

    /**
     * 原始金额, 不作为付费金额
     */
    private BigDecimal originPrice;

    /**
     * 订单应付金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单实际支付金额
     * 账务中心每次回调的支付金额之和：订单已支付金额
     * add wumeng
     */
    private BigDecimal totalPayCash;

    /**
     * 支付手续费
     * 账务中心支付时银行收取的支付费率
     * add wumeng
     */
    private BigDecimal rate;

    /**
     * 平台佣金
     * add wumeng
     */
    private BigDecimal cmCommission;

    /**
     * 发票费用
     * added by shenchunnan
     */
    private BigDecimal invoiceFee;

    /**
     * 订单优惠金额明细
     */
    private List<DiscountsPriceDetail> discountsPriceDetails;

    /**
     * 优惠券优惠金额
     */
    private BigDecimal couponPrice = BigDecimal.ZERO;

    /**
     * 订单供货价总额
     */
    private BigDecimal orderSupplyPrice;

    /**
     * 运费优惠金额
     */
    private BigDecimal deliveryCouponPrice;

    /**
     * 余额支付金额（用于组合支付）
     */
    private BigDecimal balancePrice;

    /**
     * 在线支付金额（用于组合支付）
     */
    private BigDecimal onlinePrice;

    /**
     * 招商优惠金额
     */
    private BigDecimal cmbDiscountsPrice;

    /**
     * 包装费
     */
    private BigDecimal packingPrice = BigDecimal.ZERO;

    /**
     * 已支付金额（囤货使用）
     */
    private BigDecimal paidPrice;
}
