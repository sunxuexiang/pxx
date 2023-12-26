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
 * 查询建设银行商家信息
 * @Author : Like
 * @create 2023/7/10 16:08
 */
@JobHandler(value = "ccbBusinessJobHandler")
@Component
@Slf4j
public class CcbBusinessJobHandler extends IJobHandler {

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        StopWatch stopWatch = new StopWatch("查询建行商家信息");
        stopWatch.start("开始查询建行商家信息");

        ccbPayProvider.businessQuery();

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return SUCCESS;
    }

}
