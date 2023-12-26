package com.wanmi.sbc.mongo.oplog.context.handler;

import com.wanmi.sbc.mongo.oplog.comm.AbstractMongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.config.ClientConfig;
import com.wanmi.sbc.mongo.oplog.data.Parameter;
import com.wanmi.sbc.mongo.oplog.data.Parameter.MetaMode;
import com.wanmi.sbc.mongo.oplog.position.FilePositionManager;
import com.wanmi.sbc.mongo.oplog.position.MemoryPositionManager;
import com.wanmi.sbc.mongo.oplog.position.PositionManager;
import com.wanmi.sbc.mongo.oplog.position.ZookeeperPositionManager;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-13
 * \* Time: 17:43
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Data
@Component
public class OplogPositionHandler extends AbstractMongoCaptureLifeCycle {
    private static final Logger log = LoggerFactory.getLogger(OplogPositionHandler.class);
    private ClientConfig clientConfig;
    private PositionManager positionManager;
    private ZkClientx zkClientx;

    public OplogPositionHandler(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
    }

    @Override
    public void start(){
        initPositionManager();
        if(positionManager!=null){
            positionManager.start();
        }
    }

    @Override
    public void stop(){
        if(positionManager!=null){
            positionManager.stop();
        }
    }

    protected void initPositionManager()  {
        MetaMode mode = clientConfig.getMetaMode();
        if (mode.isMemory()) {
            MemoryPositionManager memoryPositionManager = new MemoryPositionManager();
            memoryPositionManager.setDestination(Parameter.getPositionPath(clientConfig.getDestination()));
        } else if (mode.isZookeeper()) {
            ZookeeperPositionManager zookeeperPositionManager = new ZookeeperPositionManager();
            zookeeperPositionManager.setPeriod(clientConfig.getFlushPeriod());
            zookeeperPositionManager.setDestination(Parameter.getPositionPath(clientConfig.getDestination()));
            zookeeperPositionManager.setZkClientx(zkClientx);
            positionManager = zookeeperPositionManager;
        } else if (mode.isLocalFile()) {
            FilePositionManager filePositionManager = new FilePositionManager();
            filePositionManager.setDataDir(clientConfig.getDataDir());
            filePositionManager.setPeriod(clientConfig.getFlushPeriod());
            filePositionManager.setDestination(Parameter.getPositionPath(clientConfig.getDestination()));
            positionManager = filePositionManager;
        } else {
            throw new RuntimeException("unSupport MetaMode for " + mode);
        }
        log.info("init metaManager end! \n\t load CanalMetaManager:{} ", positionManager.getClass().getName());
    }





}
