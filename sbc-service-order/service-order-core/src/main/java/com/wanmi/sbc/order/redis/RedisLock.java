package com.wanmi.sbc.order.redis;


import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    RedissonClient redissonClient;

    /**
     * 读写锁-写锁
     * @param key
     */
    public boolean writeLock(String key) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        RLock rLock = readWriteLock.writeLock();
        if (rLock.isLocked()) {
            return false;
        }
        rLock.lock();
        return true;
    }

    public RLock fairLock(String key){
        RLock fairLock = redissonClient.getFairLock(key);
        fairLock.lock();
        return fairLock;
    }
    public void unFairLock(RLock fairLock){
        fairLock.unlock();
    }

    /**
     * 读写锁-读锁
     *
     * @param key
     */
    public boolean readLock(String key) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        RLock rLock = readWriteLock.readLock();
        if (rLock.isLocked()) {
            return false;
        }
        rLock.lock();
        return true;
    }

    /**
     * 读写锁-释放写锁
     * @param key
     */
    public void unWriteLock(String key) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        RLock rLock = readWriteLock.writeLock();
        if (rLock.isLocked()) {
            rLock.unlock();
        }
    }

    /**
     * 读写锁-释放读锁
     * @param key
     */
    public void unReadLock(String key) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
        RLock rLock = readWriteLock.readLock();
        if (rLock.isLocked()) {
            rLock.unlock();
        }
    }

    /**
     * 互斥锁，seconds秒后自动失效
     * @param key
     * @param seconds
     */
    public boolean lock(String key, int seconds) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isLocked()) {
            return false;
        }
        rLock.lock(seconds, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 互斥锁，自动续期
     *
     * @param key
     */
    public boolean lock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isLocked()) {
            return false;
        }
        rLock.lock();
        return true;
    }
    /**
     * 上锁，后进入的休眠尝试再争夺
     *
     * @param key
     */
    public boolean retryLock(String key) {
        while (true){
            RLock rLock = redissonClient.getLock(key);
            if (rLock.isLocked()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                rLock.lock();
                break;
            }
        }
        return true;
    }

    /**
     * 手动释放锁
     *
     * @param key
     */
    public void unlock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isLocked()) {
            rLock.unlock();
        }
    }

    /**
     * 尝试获取锁
     * @param key
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    public boolean tryLock(String key, Long timeout) throws InterruptedException {
        RLock rLock = redissonClient.getLock(key);
        return rLock.tryLock(timeout, TimeUnit.SECONDS);
    }

    public boolean tryLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        return rLock.tryLock();
    }

    /**
     * 这里简单的增加了等待时间来获取锁
     *
     * @param key
     * @throws InterruptedException
     */
    public void blockedLock(String key) throws InterruptedException {
        this.tryLock(key,120L);
    }
}