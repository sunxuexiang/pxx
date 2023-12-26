package com.wanmi.sbc.returnorder.trade.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.trade.model.root.PileTradeItemSnapshot;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeItemSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-02 14:15
 */
@Component
public class PileTradeItemSnapshotProcessor extends DefaultProcessor<PileTradeItemSnapshot,String> {

    @Autowired
    public PileTradeItemSnapshotProcessor(PileTradeItemSnapshotRepository pileTradeItemSnapshotRepository){
        mongoRepository = pileTradeItemSnapshotRepository;
    }
}
