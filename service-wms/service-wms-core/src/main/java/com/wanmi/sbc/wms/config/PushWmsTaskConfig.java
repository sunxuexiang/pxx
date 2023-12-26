package com.wanmi.sbc.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

@Configuration
public class PushWmsTaskConfig {

    private static final int CORE_POOL_SIZE = 10;
    private static final String THREAD_NAME = "PushWmsTask-";
    private static final int MAXI_NUM_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 30;
    private static final int LINK_BLOCK_QUEUE_SIZE = 1000;

    @Bean(name = "pushWmsExecutor")
    public Executor pushWmsExecutor() {
        ThreadFactory factory = new CustomizableThreadFactory(THREAD_NAME);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXI_NUM_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(LINK_BLOCK_QUEUE_SIZE),
                factory,
                new ThreadPoolExecutor.AbortPolicy()
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
