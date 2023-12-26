package com.wanmi.sbc.job;

import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *  * 定时任务Handler（Bean模式）
 *  * 发送上月订单邮件任务
 */
@JobHandler(value="sendOrderEmailHandler")
@Component
@Slf4j
public class SendOrderEmailHandler extends IJobHandler {
    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("发送上月订单邮件任务执行 " + LocalDateTime.now());
        tradeQueryProvider.sendEmailTranslate();
        XxlJobLogger.log("发送上月订单邮件任务执行结束： " + LocalDateTime.now());

        return SUCCESS;
    }
}
