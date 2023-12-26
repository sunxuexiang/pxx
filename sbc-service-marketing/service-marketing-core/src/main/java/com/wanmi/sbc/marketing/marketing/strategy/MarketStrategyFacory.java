package com.wanmi.sbc.marketing.marketing.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 营销互动校验器
 */

public class MarketStrategyFacory {

    private static Map<String, MarketingStrategy> MARKETINGS = new HashMap<String, MarketingStrategy>();

    public static void register(String type, MarketingStrategy strategy) {
        MARKETINGS.put(type, strategy);
    }

    public static MarketingStrategy getMarketingStrategy(String promotionKey) {
       return MARKETINGS.get(promotionKey);
    }
}
