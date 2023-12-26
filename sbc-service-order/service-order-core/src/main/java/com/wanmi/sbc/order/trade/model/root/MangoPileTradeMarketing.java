package com.wanmi.sbc.order.trade.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mango_pile_trade_marketing")
public class MangoPileTradeMarketing implements Serializable {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     *
     */
    private String orderNo;

    /**
     * 营销Id
     */
    private String marketingId = null;

    /**
     * 营销名称
     */
    private String marketingName = null;

    /**
     * 营销活动类型
     */
    private String marketingType = null;

    /**
     * 该营销活动关联的订单商品id集合
     */
    private String marketingSkuIds = null;

    /**
     * 营销子类型
     */
    private String marketingSubType = null;

    /**
     * 满折级别Id
     */
    private String fullDiscountLevelId = null;

    /**
     * 满金额
     */
    private BigDecimal fullFullAmount = new BigDecimal(0);

    /**
     * 满数量
     */
    private String fullFullCount = null;

    /**
     * 满金额|数量后折扣
     */
    private String fullDiscount = null;

    /**
     * 营销满赠多级优惠信息
     */
    private String giftLevel = null;

    /**
     * 当前满赠活动关联的赠品id列表，非满赠活动则为空
     */
    private String giftIds = null;

    /**
     * 满减级别主键Id
     */
    private String reductionReductionLevelId = null;

    /**
     * 满金额
     */
    private BigDecimal reductionFullAmount = new BigDecimal(0);

    /**
     * 满数量
     */
    private String reductionFullCount = null;

    /**
     * 满金额|数量后减多少元
     */
    private String reductionReduction = null;

    /**
     * 优惠金额
     */
    private BigDecimal marketingDiscountsAmount = new BigDecimal(0);

    /**
     * 该活动关联商品除去优惠金额外的应付金额
     */
    private BigDecimal marketingRealPayaAmount = new BigDecimal(0);

    /**
     * 是否叠加
     */
    private String multiple = null;

    /**
     * 创建时间
     */
    private LocalDateTime createTime = null;

}
