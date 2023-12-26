    package com.wanmi.sbc.mongo.oplog.running;

import com.wanmi.sbc.mongo.oplog.config.ClientConfig;
import com.wanmi.sbc.mongo.oplog.data.RunningData;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-19
 * \* Time: 17:05
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \ 运行调度器
 */
@Component
public class RunningDispatch {

    @Autowired
    private ClusterRunning      clusterRunning;                 //集群模式
    @Autowired
    private StandAloneRunning   standAloneRunning;              //单机模式

    private ClientConfig        clientConfig;                   //配置信息
    private boolean             isCluster           = false;    //是否是集群模式


    public RunningDispatch(ClientConfig clientConfig){
        this.clientConfig = clientConfig;

    }

    public void start(){
        if(StringUtils.isNotBlank(clientConfig.getZookeeperHost())
                && StringUtils.isNotBlank(clientConfig.getClusterName()))
        {
            isCluster = true;
            clusterRunning.setZkClientx(getZkclientx());
            clusterRunning.setDestination(clientConfig.getDestination());
            clusterRunning.setRunningData(new RunningData());
            clusterRunning.start();
        }else {
            isCluster = false;
            if(clientConfig.getMetaMode().isZookeeper()) {
                standAloneRunning.setZkClientx(getZkclientx());
            }
            standAloneRunning.start();
        }
    }

    //应用停止前，自动停止
    @PreDestroy
    public void stop(){
        if(isCluster){
            clusterRunning.stop();
        }else{
            standAloneRunning.stop();
        }
    }

    private synchronized ZkClientx getZkclientx() {
        // 做一下排序，保证相同的机器只使用同一个链接
        List<String> zkClusters = new ArrayList<String>(Arrays.asList(
                clientConfig.getZookeeperHost()
                        .split(",")
        )
        );
        Collections.sort(zkClusters);

        return ZkClientx.getZkClient(StringUtils.join(zkClusters, ";"));
    }
}
