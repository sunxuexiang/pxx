package com.wanmi.sbc.job;

import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/7/18 10:07
 */
@JobHandler(value = "ccbSubAccountJobHandler")
@Component
@Slf4j
public class CcbSubAccountJobHandler extends IJobHandler {

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("开始查询建行订单分账状态");
        ccbPayProvider.syncCcbSubAccountStatus();
        stopWatch.stop();

        //
        log.info(stopWatch.prettyPrint());
        return SUCCESS;
    }


}
