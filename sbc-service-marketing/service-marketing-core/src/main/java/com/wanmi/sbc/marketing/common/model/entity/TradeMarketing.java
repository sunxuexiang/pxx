package com.wanmi.sbc.marketing.common.model.entity;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>订单营销信息</p>
 * Created by of628-wenzhi on 2018-02-26-下午5:57.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeMarketing {
    /**
     * 促销Id
     */
    private Long marketingId;

    /**
     * 促销名称
     */
    private String marketingName;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     */
    private MarketingType marketingType;

    /**
     * 该营销活动关联的订单商品id集合
     */
    private List<String> skuIds;

    /**
     * 促销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    private MarketingSubType subType;

    /**
     * 满折信息
     */
    private MarketingFullDiscountLevel fullDiscountLevel;

    /**
     * 满赠信息
     */
    private MarketingFullGiftLevel giftLevel;

    /**
     * 满减信息
     */
    private MarketingFullReductionLevel reductionLevel;

    /**
     * 优惠金额
     */
    private BigDecimal discountsAmount;

    /**
     * 该活动关联商品除去优惠金额外的应付金额
     */
    private BigDecimal realPayAmount;

    /**
     * 当前满赠活动关联的赠品id列表，非满赠活动则为空
     */
    private List<String> giftIds = new ArrayList<>();

    /**
     * 是否叠加, 0：否， 1：是
     */
    private BoolFlag isOverlap;

    /**
     * 叠加倍数
     */
    private Long multiple;
}
