package com.wanmi.sbc.returnorder.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-02 14:15
 */
@Component
public class PileTradeProcessor extends DefaultProcessor<PileTrade,String> {

    @Autowired
    public PileTradeProcessor(PileTradeRepository pileTradeRepository){
        mongoRepository = pileTradeRepository;
    }
}
