package com.wanmi.sbc.mongo.oplog.comm;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* @date: 2019-12-17
 * \* @time: 10:46
 * \* To change this template use File | Settings | File Templates.
 * \* @description:
 * \
 */
public abstract class AbstractMongoCaptureLifeCycle implements MongoCaptureLifeCycle {
    /**
     * 是否处于运行中
      */
    protected volatile boolean running = false;

    @Override
    public boolean isStart() {
        return running;
    }

    @Override
    public void start() {
        if (running) {
            throw new RuntimeException(this.getClass().getName() + " has startup , don't repeat start");
        }

        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            throw new RuntimeException(this.getClass().getName() + " isn't start , please check");
        }

        running = false;
    }
}
