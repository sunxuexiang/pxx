package com.wanmi.sbc.job;

import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>腾讯IM排队聊天即时分配客服定时任务</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Component
@Slf4j
@JobHandler(value="tencentIMChatQueueJobHandler")
public class TencentIMChatQueueJobHandler extends IJobHandler {

    @Autowired
    private CustomerServiceChatProvider customerServiceChatProvider;

    private boolean isRunning = false;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        if (isRunning) {
            log.info("上一轮正在执行腾讯IM排队聊天即时分配客服定时任务");
            return SUCCESS;
        }
        try {
            isRunning = true;
            customerServiceChatProvider.processChatQueueTask();
        }
        catch (Exception e) {
            log.error("客服排队重新分配异常", e);
        }
        finally {
            isRunning = false;
        }
        return SUCCESS;
    }
}
