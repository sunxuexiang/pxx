package com.wanmi.sbc.mongo.oplog.service;

import com.wanmi.sbc.mongo.oplog.running.ClusterRunning;
import com.wanmi.sbc.mongo.oplog.running.RunningDispatch;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: zhanggaolei
 * MongoBD 日志获取处理服务线程入口
 */
@Component
public class CoreManagerService implements ApplicationRunner {

    @Autowired
    private RunningDispatch dispatch;

    @Override
    public void run(ApplicationArguments args) {
        dispatch.start();
    }

}

