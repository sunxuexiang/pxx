package com.wanmi.sbc.mongo.oplog.config;

import com.wanmi.sbc.mongo.oplog.data.Parameter;
import com.wanmi.sbc.mongo.oplog.data.Parameter.MetaMode;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* @date: 2019-12-16
 * \* @time: 16:24
 * \* To change this template use File | Settings | File Templates.
 * \* @description:
 * \  client相关的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "capture.client")
public class ClientConfig {

    /**
     * zk节点信息
     */
    private String              zookeeperHost;                                                      //可以不填，如果mateMode为zookeeper或者clusterName不为空则必填

    private Parameter           parameter;

    private MetaMode            metaMode            = MetaMode.LOCAL_FILE;


    private String              regexFilter;

    private String              clusterName;                                                        //集群名称，多个集群使用同一个zk集群则不能同名

    private String              dataDir             = "../conf";                                    // 默认本地文件数据的目录默认是conf
    private Integer             flushPeriod         = 1000;                                         // meta刷新间隔
    private String              destination         = clusterName == null ? "default":clusterName;  //用于zookeeper地址
    private String              dynamicTopic;                                                       //动态topic
}
