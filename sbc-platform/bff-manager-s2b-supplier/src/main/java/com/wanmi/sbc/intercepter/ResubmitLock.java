package com.wanmi.sbc.intercepter;

/**
 * @Author shiGuangYi
 * @createDate 2023-08-05 16:27
 * @Description: TODO
 * @Version 1.0
 */
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交的锁
 */
@Slf4j
public final class ResubmitLock {

    /**
     * 新建一个用于存放key的容器，容量为20；
     */
    private static final ConcurrentHashMap<String, Object> LOCK_CACHEMAP = new ConcurrentHashMap<>(200);

    /**
     * 新建一个可定时线程池：
     *    - 核心线程数为5；
     *    - 设置任务数超过线程池容量以及任务队列的容量时的处理程序，这里是默默丢弃掉新来的任务，并抛出一个RejectedExecutionHandler拒绝处理异常
     */
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(5, new ThreadPoolExecutor.DiscardPolicy());


    private ResubmitLock() {
    }

    /**
     * 单例模式，保证同一时间内只生成一个锁的实例；
     *          ——这里采用了静态内部类的方式；
     * @return
     */
    private static class SingletonInstance {
        private static final ResubmitLock INSTANCE = new ResubmitLock();
    }

    public static ResubmitLock getInstance() {
        return SingletonInstance.INSTANCE;
    }

    // 对参数进行md5加密：
    public static String handleKey(String param) {
        return DigestUtils.md5Hex(param == null ? "" : param);
    }

    /**
     * 加锁：
     *          putIfAbsent 是原子操作，保证线程安全
     *          putIfAbsent在放入数据时，如果存在重复的key，那么putIfAbsent不会放入值，会返回存在的value，不进行替换
     * @param key   对应的key
     * @param value
     * @return
     */
    public boolean lock(final String key, Object value) {
        //如果之前不存在该key，才会将该key和value存储起来，并返回true，
        // 如果之前存在该key，这里会返回false；
        return Objects.isNull(LOCK_CACHEMAP.putIfAbsent(key, value));
    }

    /**
     * 延时释放锁， 用以控制指定时间内的重复提交
     *
     * @param lock         是否需要解锁
     * @param key          对应的key
     * @param delaySeconds 延时时间
     */
    public void unLock(final boolean lock, final String key, final int delaySeconds) {
        if (lock) {
            //EXECUTOR.schedule()，执行定时任务；
            EXECUTOR.schedule(() -> {
                LOCK_CACHEMAP.remove(key);
            }, delaySeconds, TimeUnit.SECONDS);
        }
    }
}
