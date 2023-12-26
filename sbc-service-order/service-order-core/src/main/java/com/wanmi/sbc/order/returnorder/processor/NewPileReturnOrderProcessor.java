package com.wanmi.sbc.order.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.repository.NewPileReturnOrderRepository;
import com.wanmi.sbc.order.returnorder.repository.ReturnPileOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ChenChang
 * @Description:
 */
@Component
public class NewPileReturnOrderProcessor extends DefaultProcessor<NewPileReturnOrder,String> {

    @Autowired
    public NewPileReturnOrderProcessor(NewPileReturnOrderRepository newPileReturnOrderRepository){
        mongoRepository = newPileReturnOrderRepository;
    }
}
