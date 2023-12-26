package com.wanmi.sbc.returnorder.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPileTradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: chenchang
 * @Description:
 * @Date: 2022-10-6
 */
@Component
public class NewPileTradeProcessor extends DefaultProcessor<NewPileTrade,String> {

    @Autowired
    public NewPileTradeProcessor(NewPileTradeRepository newPileTradeRepository){
        mongoRepository = newPileTradeRepository;
    }
}