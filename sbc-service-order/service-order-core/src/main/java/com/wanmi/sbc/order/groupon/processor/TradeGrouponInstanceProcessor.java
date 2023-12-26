package com.wanmi.sbc.order.groupon.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.trade.model.root.GrouponInstance;
import com.wanmi.sbc.order.trade.repository.TradeGrouponInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yang
 * @since 2019-08-16
 */
@Component
public class TradeGrouponInstanceProcessor extends DefaultProcessor<GrouponInstance, String> {

    @Autowired
    public TradeGrouponInstanceProcessor(TradeGrouponInstanceRepository tradeGrouponInstanceRepository) {
        mongoRepository = tradeGrouponInstanceRepository;
    }
}
