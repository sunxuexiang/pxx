package com.wanmi.sbc.returnorder.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-02 14:15
 */
@Component
public class TradeProcessor extends DefaultProcessor<Trade,String> {

    @Autowired
    public TradeProcessor(TradeRepository tradeRepository){
        mongoRepository = tradeRepository;
    }
}
