package com.wanmi.sbc.marketing.marketing.price;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MarketingCalculatorResult {
    /**
     * 优惠前的价格
     */
    private BigDecimal beforeAmount;
    /**
     * 优惠后的价格
     */
    private BigDecimal payableAmount;
    /**
     * 优惠金额
     */
    private BigDecimal profitAmount;

    /**
     * 是否达到活动门槛
     */
    private Boolean reachLevel;

    /**
     * 是否达到活动门槛
     */
    private BigDecimal singleProfitAmount = BigDecimal.ZERO;

    /**
     * 活动的叠加倍数
     */
    private BigDecimal reMarketingFold;

    /**
     * 离下一门槛的差值
     *  满数量：差多少件
     *  满金额：差多少钱
     */
    private BigDecimal diffNextLevel;

    /**
     *  营销活动ID
     */
    private Long marketingId;

    private String marketingName;

    private MarketingType marketingType;

    private MarketingSubType subType;

    private BoolFlag isOverlap;

    /**
     * 商品IDs
     */
    private List<String> goodsInfoIds = Lists.newArrayList();

    /**
     * 赠品列表
     */
    private List<String> giftIds = Lists.newArrayList();

    /**
     * 营销限购
     */
    private Long purchaseNumOfMarketing;

    /**
     * 营销活动限购信息
     */
    private List<MarketingScopeVO> marketingScopeVOS = Lists.newArrayList();

    /*--------------------------------------------------当前门槛 -----------------------------------------------------------------------------------*/
    /**
     * 满减 - 当前门槛
     */
    private MarketingFullReductionLevelVO currentFullReductionLevel;

    /**
     * 满折 - 当前门槛
     */
    private MarketingFullDiscountLevelVO currentFullDiscountLevel;

    /**
     * 满赠 - 当前门槛
     *  赠品需要进行排序？
     *  还需要校验库存
     */
    private MarketingFullGiftLevelVO currentFullGiftLevel;

    /*--------------------------------------------------下一门槛 -----------------------------------------------------------------------------------*/
    /**
     * 满减 - 下一门槛
     */
    private MarketingFullReductionLevelVO nextFullReductionLevel;

    /**
     * 满折 - 下一门槛
     */
    private MarketingFullDiscountLevelVO nextFullDiscountLevel;

    /**
     * 满赠 - 下一门槛
     */
    private MarketingFullGiftLevelVO nextFullGiftLevel;
}
