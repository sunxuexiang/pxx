package com.wanmi.sbc.marketing;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-21 14:13
 */
@EnableDistributedTransaction
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class}, scanBasePackages = {"com.wanmi.sbc"})
@EnableTransactionManagement
@EnableAsync
@EnableDiscoveryClient
@Slf4j
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
@ImportResource(locations = {"classpath:spring-plugin.xml"})
@EnableJpaAuditing
public class MarketingServieApplication {

    public static void main(String[] args) throws Exception {

        System.setProperty("nacos.logging.default.config.enabled", "false");
        Environment env = SpringApplication.run(MarketingServieApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8095");
        String healthPort = env.getProperty("management.server.port", "9095");

        log.info("Access URLs:\n----------------------------------------------------------\n\t"
                        + "Local: \t\thttp://127.0.0.1:{}\n\t"
                        + "External: \thttp://{}:{}\n\t"
                        + "health: \thttp://{}:{}/act/health\n----------------------------------------------------------",
                port,
                InetAddress.getLocalHost().getHostAddress(),
                port,
                InetAddress.getLocalHost().getHostAddress(),
                healthPort
        );

    }


}
