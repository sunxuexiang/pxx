package com.wanmi.sbc.returnorder.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.NewPileReturnOrderRepository;
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
