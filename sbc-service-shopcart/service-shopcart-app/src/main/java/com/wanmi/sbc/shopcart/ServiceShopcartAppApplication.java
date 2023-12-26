package com.wanmi.sbc.shopcart;

import java.net.InetAddress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;

import lombok.extern.slf4j.Slf4j;

@EnableDistributedTransaction
@SpringBootApplication(scanBasePackages = {"com.wanmi.sbc"})
@EnableTransactionManagement
@EnableAsync
@EnableDiscoveryClient
@Slf4j
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
@ImportResource(locations = {"classpath:spring-plugin.xml"})
@EnableJpaAuditing
public class ServiceShopcartAppApplication {

    public static void main(String[] args) throws Exception {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        Environment env = SpringApplication.run(ServiceShopcartAppApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8929");

        String actPort = env.getProperty("management.server.port", "8986");

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
