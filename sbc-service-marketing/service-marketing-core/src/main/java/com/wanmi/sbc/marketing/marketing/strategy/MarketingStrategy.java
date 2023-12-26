package com.wanmi.sbc.marketing.marketing.strategy;

import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.marketing.strategy.check.MarketingCheckResult;

import javax.annotation.PostConstruct;

public abstract class MarketingStrategy {

    public abstract String getMarketingTypeEnum();

    @PostConstruct
    public void register() {
        MarketStrategyFacory.register(getMarketingTypeEnum(), this);
    }

    public abstract MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest params);

    public abstract Marketing saveOrUpdate(SaveOrUpdateMarketingRequest params);
}
