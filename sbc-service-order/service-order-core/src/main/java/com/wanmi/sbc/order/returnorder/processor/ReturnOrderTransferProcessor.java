package com.wanmi.sbc.order.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrderTransfer;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:41
 */
@Component
public class ReturnOrderTransferProcessor extends DefaultProcessor<ReturnOrderTransfer, String> {


    @Autowired
    public ReturnOrderTransferProcessor(ReturnOrderTransferRepository returnOrderTransferRepository) {
        mongoRepository = returnOrderTransferRepository;
    }

}
