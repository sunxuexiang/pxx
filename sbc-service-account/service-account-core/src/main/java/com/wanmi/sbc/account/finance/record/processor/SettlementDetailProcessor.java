package com.wanmi.sbc.account.finance.record.processor;

import com.wanmi.sbc.account.finance.record.model.root.SettlementDetail;
import com.wanmi.sbc.account.finance.record.repository.SettlementDetailRepository;
import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 20:08
 */
@Component
public class SettlementDetailProcessor extends DefaultProcessor<SettlementDetail, String> {


    @Autowired
    public SettlementDetailProcessor(SettlementDetailRepository settlementDetailRepository) {
        mongoRepository = settlementDetailRepository;
    }
}
