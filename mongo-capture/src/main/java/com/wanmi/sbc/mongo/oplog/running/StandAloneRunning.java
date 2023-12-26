package com.wanmi.sbc.mongo.oplog.running;

import com.wanmi.sbc.mongo.oplog.comm.AbstractMongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.context.MongoCaptureAdapter;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-19
 * \* Time: 14:56
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \ 单机模式运行
 */
@Data
@Component
public class StandAloneRunning extends AbstractMongoCaptureLifeCycle {

    private ZkClientx zkClientx;
    @Autowired
    private MongoCaptureAdapter mongoCaptureAdapter;

    @Override
    public void start(){
        super.start();
        mongoCaptureAdapter.setZkClientx(zkClientx);
        mongoCaptureAdapter.start();
    }

    @Override
    public void stop(){
        super.stop();
        mongoCaptureAdapter.stop();
    }
}
