package com.wanmi.sbc.job;

import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时任务Handler（Bean模式）
 * 导出上个月订单数据，并发送邮箱1210997183@qq.com
 */
@JobHandler(value="OrderMonthExportJobHandler")
@Component
@Slf4j
public class OrderMonthExportJobHandler extends IJobHandler {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Override
    public ReturnT<String> execute(String s) throws Exception {

        return null;
    }


}
