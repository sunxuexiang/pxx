package com.wanmi.sbc.returnorder.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.trade.model.root.ProviderTrade;
import com.wanmi.sbc.returnorder.trade.repository.ProviderTradeRepository;
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
