package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;

public interface MarketingCheckChainNode {
    /**
     * 通用检查处理器
     */
    MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest);
}
