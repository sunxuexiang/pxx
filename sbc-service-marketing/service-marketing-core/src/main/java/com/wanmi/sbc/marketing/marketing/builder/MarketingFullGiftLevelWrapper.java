package com.wanmi.sbc.marketing.marketing.builder;

import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import lombok.Data;

import java.util.List;

@Data
public class MarketingFullGiftLevelWrapper {
    /**
     * 满赠等级
     */
    private MarketingFullGiftLevel marketingFullGiftLevel;

    /**
     * 满赠商品
     */
    private List<MarketingFullGiftDetail> marketingFullGiftDetails;
}
