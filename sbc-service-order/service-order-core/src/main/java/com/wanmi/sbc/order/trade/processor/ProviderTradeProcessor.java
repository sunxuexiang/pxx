package com.wanmi.sbc.order.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.trade.model.root.ProviderTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.repository.ProviderTradeRepository;
import com.wanmi.sbc.order.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: qiaokang
 * @Description:
 * @Date: 2020-03-26 14:15
 */
@Component
public class ProviderTradeProcessor extends DefaultProcessor<ProviderTrade,String> {

    @Autowired
    public ProviderTradeProcessor(ProviderTradeRepository providerTradeRepository){
        mongoRepository = providerTradeRepository;
    }
}
