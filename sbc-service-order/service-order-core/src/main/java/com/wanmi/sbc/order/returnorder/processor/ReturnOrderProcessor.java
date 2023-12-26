package com.wanmi.sbc.order.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:39
 */
@Component
public class ReturnOrderProcessor extends DefaultProcessor<ReturnOrder,String> {

    @Autowired
    public ReturnOrderProcessor(ReturnOrderRepository returnOrderRepository){
        mongoRepository = returnOrderRepository;
    }
}
