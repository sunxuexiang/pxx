package com.wanmi.sbc.order.groupon.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.trade.model.root.GrouponInstance;
import com.wanmi.sbc.order.trade.repository.TradeGrouponInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:39
 */
@Component
public class GrouponInstanceProcessor extends DefaultProcessor<GrouponInstance,String> {

    @Autowired
    public GrouponInstanceProcessor(TradeGrouponInstanceRepository tradeGrouponInstanceRepository){
        mongoRepository = tradeGrouponInstanceRepository;
    }
}
