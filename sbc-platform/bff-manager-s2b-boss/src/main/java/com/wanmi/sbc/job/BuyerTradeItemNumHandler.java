package com.wanmi.sbc.job;

import com.wanmi.ares.request.replay.ReplayTradeBuyerItemNumQuery;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@JobHandler(value = "buyerTradeItemNumHandler")
@Slf4j
@Component
public class BuyerTradeItemNumHandler extends IJobHandler {

    @Autowired
    private BuyerTradeItemNumService buyerTradeItemNumService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        buyerTradeItemNumService.execute(new ReplayTradeBuyerItemNumQuery());
        return SUCCESS;
    }
}
