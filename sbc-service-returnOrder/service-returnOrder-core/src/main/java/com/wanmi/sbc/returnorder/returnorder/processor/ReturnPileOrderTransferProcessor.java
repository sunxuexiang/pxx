package com.wanmi.sbc.returnorder.returnorder.processor;

import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrderTransfer;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:41
 */
@Component
public class ReturnPileOrderTransferProcessor extends DefaultProcessor<ReturnPileOrderTransfer, String> {


    @Autowired
    public ReturnPileOrderTransferProcessor(ReturnPileOrderTransferRepository returnOrderTransferRepository) {
        mongoRepository = returnOrderTransferRepository;
    }

}
