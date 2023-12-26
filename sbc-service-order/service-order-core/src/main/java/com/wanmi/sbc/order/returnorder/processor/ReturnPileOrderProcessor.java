package com.wanmi.sbc.order.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.repository.ReturnPileOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:39
 */
@Component
public class ReturnPileOrderProcessor extends DefaultProcessor<ReturnPileOrder,String> {

    @Autowired
    public ReturnPileOrderProcessor(ReturnPileOrderRepository returnOrderRepository){
        mongoRepository = returnOrderRepository;
    }
}
