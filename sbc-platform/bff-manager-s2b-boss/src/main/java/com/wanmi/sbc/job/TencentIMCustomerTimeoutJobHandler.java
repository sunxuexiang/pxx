package com.wanmi.sbc.job;

import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceSettingProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>腾讯IM消费者超时未回复结束会话定时任务</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Component
@Slf4j
@JobHandler(value="tencentIMCustomerTimeoutJobHandler")
public class TencentIMCustomerTimeoutJobHandler extends IJobHandler {

    @Autowired
    private CustomerServiceSettingProvider customerServiceSettingProvider;

    private boolean isRunning = false;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        if (isRunning) {
            log.info("上一轮正在执行腾讯IM消费者超时未回复结束会话定时任务");
            return SUCCESS;
        }
        try {
            isRunning = true;
            customerServiceSettingProvider.customerReplyTimeout(ImSettingTypeEnum.Close.getType());
        }
        catch (Exception e) {
            log.error("腾讯IM消费者超时未回复结束会话定时任务异常", e);
        }
        finally {
            isRunning = false;
        }
        return SUCCESS;
    }
}
