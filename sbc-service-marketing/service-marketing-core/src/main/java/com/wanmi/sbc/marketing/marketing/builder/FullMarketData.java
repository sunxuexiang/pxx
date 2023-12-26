package com.wanmi.sbc.marketing.marketing.builder;

import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import lombok.Data;

import java.util.List;

@Data
public class FullMarketData {
    /**
     * 营销活动信息
     */
    private Marketing marketing;

    /**
     * 参加营销的商品信息
     */
    private List<MarketingScope> marketingScopes;

    /**
     * 赠品信息
     */
    private List<MarketingFullGiftLevelWrapper> marketingFullGiftLevelWrappers;

    /**
     * 满折信息
     */
    private List<MarketingFullDiscountLevel> marketingFullDiscountLevels;

    /**
     * 满减信息
     */
    private List<MarketingFullReductionLevel> marketingFullReductionLevels;
}
