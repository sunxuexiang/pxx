package com.wanmi.sbc.job;

import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 扫满十件单子
 *
 * @author yitang
 * @version 1.0
 */
@Component
@Slf4j
@JobHandler(value="cachePushKingdeeOrderJobHandler")
public class CachePushKingdeeOrderJobHandler extends IJobHandler {

    @Autowired
    private TradeProvider tradeProvider;

    public ReturnT<String> execute(String s) throws Exception{
        log.info("CachePushKingdeeOrderJobHandler start");
        tradeProvider.pushCachePushKingdeeOrder();
        return SUCCESS;
    }
}
