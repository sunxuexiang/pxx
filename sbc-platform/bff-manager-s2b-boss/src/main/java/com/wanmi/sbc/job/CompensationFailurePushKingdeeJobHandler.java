package com.wanmi.sbc.job;

import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 补偿推金蝶失败订单
 *
 * @author yitang
 * @version 1.0
 */
@Component
@Slf4j
@JobHandler(value="compensationFailurePushKingdeeJobHandler")
public class CompensationFailurePushKingdeeJobHandler extends IJobHandler {
    @Autowired
    private TradeProvider tradeProvider;

    public ReturnT<String> execute(String s) throws Exception{
        log.info("CompensationFailurePushKingdeeJobHandler start");
        tradeProvider.compensateFailedSalesOrders();
        return SUCCESS;
    }
}
