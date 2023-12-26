package com.wanmi.sbc.goods;

//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * @Author: ZhangLingKe
 * @Description: 商品服务启动器
 * @Date: 2018-11-07 10:07
 */
@EnableDistributedTransaction
//读写分离使用
 @SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class}, scanBasePackages = {"com.wanmi.sbc"})
//@SpringBootApplication(scanBasePackages = {"com.wanmi.sbc"})
@EnableTransactionManagement
@EnableAsync
@EnableDiscoveryClient
@Slf4j
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
@EnableJpaAuditing
@EnableRetry
public class GoodsServiceApplication {

    public static void main(String[] args) throws Exception {
        Environment env = SpringApplication.run(GoodsServiceApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8490");
        String actPort = env.getProperty("management.server.port", "8491");
        IdGeneratorOptions options = new IdGeneratorOptions((short) 2);
        YitIdHelper.setIdGenerator(options);
        log.info("Access URLs:\n----------------------------------------------------------\n\t"
                        + "Local: \t\thttp://127.0.0.1:{}\n\t"
                        + "External: \thttp://{}:{}\n\t"
                        + "health: \thttp://{}:{}/act/health\n----------------------------------------------------------",
                port,
                InetAddress.getLocalHost().getHostAddress(),
                port,
                InetAddress.getLocalHost().getHostAddress(),
                actPort
        );
    }

}