package com.wanmi.sbc.walletorder.bean.vo;

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
 * 订单金额归总
 * Created by jinwei on 19/03/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradePriceVO implements Serializable {

    private static final long serialVersionUID = 5258208696186469295L;
    /**
     * 商品总金额
     */
    @ApiModelProperty(value = "商品总金额")
    private BigDecimal goodsPrice;

    /**
     * 囤货已支付
     */
    @ApiModelProperty(value = "囤货已支付")
    private BigDecimal paidPrice;

    /**
     * 鲸贴抵扣
     */
    @ApiModelProperty(value = "鲸贴抵扣")
    private BigDecimal discountOfJingTie;


    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "配送费用")
    private BigDecimal deliveryPrice;

    /**
     * 特价金额，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "特价金额")
    private BigDecimal privilegePrice;

    /**
     * 优惠金额
     */
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountsPrice;

    /**
     * 积分数量，可以从TradePriceInfo获取(此变量没用到)
     */
    @ApiModelProperty(value = "积分数量")
    private Integer integral;

    /**
     * 积分兑换金额，可以从TradePriceInfo获取(此变量没用到)
     */
    @ApiModelProperty(value = "积分兑换金额")
    private BigDecimal integralPrice;

    /**
     * 积分
     */
    @ApiModelProperty(value = "积分")
    private Long points;

    /**
     * 积分兑换金额
     */
    @ApiModelProperty(value = "积分兑换金额")
    private BigDecimal pointsPrice;

    /**
     * 积分价值
     */
    @ApiModelProperty(value = "积分价值")
    private Long pointWorth;


    /**
     * 是否特价单
     */
    @ApiModelProperty(value = "是否特价单", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private boolean special;

    /**
     * 是否开启运费
     */
    @ApiModelProperty(value = "是否开启运费", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private boolean enableDeliveryPrice;

    /**
     * 原始金额, 不作为付费金额
     */
    @ApiModelProperty(value = "原始金额, 不作为付费金额")
    private BigDecimal originPrice;

    /**
     * 订单应付金额
     */
    @ApiModelProperty(value = "订单应付金额")
    private BigDecimal totalPrice;

    /**
     * 订单实际支付金额
     * 账务中心每次回调的支付金额之和：订单已支付金额
     * add wumeng
     */
    @ApiModelProperty(value = "订单实际支付金额")
    private BigDecimal totalPayCash;

    /**
     * 支付手续费
     * 账务中心支付时银行收取的支付费率
     * add wumeng
     */
    @ApiModelProperty(value = "支付手续费")
    private BigDecimal rate;

    /**
     * 平台佣金
     * add wumeng
     */
    @ApiModelProperty(value = "平台佣金")
    private BigDecimal cmCommission;

    /**
     * 发票费用
     * added by shenchunnan
     */
    @ApiModelProperty(value = "发票费用")
    private BigDecimal invoiceFee;

    /**
     * 订单优惠金额明细
     */
    @ApiModelProperty(value = "订单优惠金额明细")
    private List<DiscountsPriceDetailVO> discountsPriceDetails;

    /**
     * 优惠券优惠金额
     */
    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponPrice = BigDecimal.ZERO;

    /**
     * 单个订单返利总金额
     */
    @ApiModelProperty(value = "单个订单返利总金额")
    private BigDecimal orderDistributionCommission = BigDecimal.ZERO;

    /**
     * 订单供货价总额
     */
    @ApiModelProperty(value = "订单供货价总额")
    private BigDecimal orderSupplyPrice;

    /**
     * 父订单总额（用于代付显示）
     */
    @ApiModelProperty(value = "父订单总额")
    private BigDecimal parentTotalPrice;

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
    private BigDecimal packingPrice;
}
