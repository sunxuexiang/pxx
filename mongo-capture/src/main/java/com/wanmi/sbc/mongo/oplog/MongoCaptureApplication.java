package com.wanmi.sbc.mongo.oplog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 *  项目启动类
 *
 *  @author ：Mc_GuYi
 *  @since  ：4/26/2019-3:00 PM
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MongoCaptureApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoCaptureApplication.class, args);
    }

}
