package com.wanmi.sbc.mongo.oplog.comm;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-17
 * \* Time: 10:28
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public interface MongoCaptureLifeCycle {

    void start();

    void stop();

    boolean isStart();
}
