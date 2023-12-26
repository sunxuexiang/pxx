package com.wanmi.sbc.job;

import com.wanmi.sbc.customer.api.provider.dotdistance.DotDistanceProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 加定时任务 定时任务跑2表直接的距离
 */
@JobHandler(value = "dotDistance")
@Component
@Slf4j
public class DotDistanceHandler extends IJobHandler {

   @Autowired
   private DotDistanceProvider dotDistanceProvider;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        this.asyncExe();
        return SUCCESS;
    }

    @Async
    public void asyncExe() {
        LocalDateTime localDateTime = LocalDateTime.now();
        logger.info("执行配送距离定时任务开始时间"+localDateTime);
        dotDistanceProvider.execut();
        LocalDateTime localDateTime1 = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime,localDateTime1);
        logger.info("执行配送距离全部时间"+duration.toMillis()+"秒");
    }
}
