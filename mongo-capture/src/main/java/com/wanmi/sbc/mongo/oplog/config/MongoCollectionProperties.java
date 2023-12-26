package com.wanmi.sbc.mongo.oplog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

 /**
   * @Description: MongoDB 连接信息配置对象
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:35
   */
@Data
@Component
@ConfigurationProperties(prefix = "replica.mongo")
public class MongoCollectionProperties {

    /**
     * 监听Mongo主机地址和端口，多个是，分割
     */
    private String uri;

    private String authenticationDatabase;

    /**
     * 监听Mongo数据库名
     */
    private String db = "local";

    /**
     * 监听Mongo集合名
     */
    private String collection = "oplog.rs";


     /**
      * 用户名
      */
    private String userName;

     /**
      * 密码
      */
    private String password;

    private String version;

}
