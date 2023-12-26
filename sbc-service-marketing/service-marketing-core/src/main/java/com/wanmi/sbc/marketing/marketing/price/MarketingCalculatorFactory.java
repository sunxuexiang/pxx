package com.wanmi.sbc.marketing.marketing.price;

import java.util.HashMap;
import java.util.Map;

public class MarketingCalculatorFactory {
    private static Map<String, MarketingCalculator> MARKETINGS = new HashMap<>();

    public static void register(String type, MarketingCalculator strategy) {
        MARKETINGS.put(type, strategy);
    }

    public static MarketingCalculator getCaculator(String marketingKey) {
        return MARKETINGS.get(marketingKey);
    }
}
