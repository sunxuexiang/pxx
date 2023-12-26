package com.wanmi.sbc.wms.erp.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 推金蝶异步线程池
 *
 * @author yitang
 * @version 1.0
 */
@Configuration
@EnableAsync
public class PushKingdeeThreadPool {
    //核心线程池大小
    private static final int CORE_POOL_SIZE = 10;
    //创建线程池名称
    private static final String THREAD_NAME = "pushKingdeeStockSalesback";
    private static final String PILE_THREAD_NAME = "pushKingdeeStockReturnGoodsback";
    //最大线程池大小
    private static final int MAXI_NUM_SIZE = 10;
    //线程最大空闲时间
    private static final int KEEP_ALIVE_TIME = 30;
    private static final int LINK_BLOCK_QUEUE_SIZE = 1000;

    private LinkedBlockingQueue pushKingdeeStockSalesQueue;

    private LinkedBlockingQueue pushKingdeeStockReturnGoodsQueue;

    @Bean(name = "pushKingdeeStockSalesBack")
    public Executor myExecutor(){
        //TimeUnit.SECONDS 时间类型
        //线程等待队列
        pushKingdeeStockSalesQueue = new LinkedBlockingQueue<Runnable>(LINK_BLOCK_QUEUE_SIZE);
        //线程创建工厂
        ThreadFactory factory = new CustomizableThreadFactory(THREAD_NAME);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXI_NUM_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                pushKingdeeStockSalesQueue,
                factory,
                new ThreadPoolExecutor.AbortPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean(name = "pushKingdeeStockReturnGoodsBack")
    public Executor plieExecutor(){
        pushKingdeeStockReturnGoodsQueue = new LinkedBlockingQueue<Runnable>(LINK_BLOCK_QUEUE_SIZE);
        ThreadFactory factory = new CustomizableThreadFactory(PILE_THREAD_NAME);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXI_NUM_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                pushKingdeeStockReturnGoodsQueue,
                factory,
                new ThreadPoolExecutor.AbortPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
