package com.wanmi.sbc.job;

import com.wanmi.sbc.job.service.GetNewUserService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author jeffrey
 * @create 2021-08-20 17:55
 */


@Component
@Slf4j
@JobHandler(value="autoSendNewUserFirstOrderJobHandler")
public class AutoSendNewUserFirstOrderJobHandler extends IJobHandler {
    @Autowired
    private GetNewUserService getNewUserService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        getNewUserService.getNewUser();
        return SUCCESS;
    }
}
