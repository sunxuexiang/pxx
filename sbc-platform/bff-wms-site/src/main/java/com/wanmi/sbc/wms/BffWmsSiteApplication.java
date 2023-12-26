package com.wanmi.sbc.wms;

import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(scanBasePackages = {"com.wanmi.sbc","com.codingapi.txlcn"})
@Slf4j
@EnableAsync
@PropertySource(value = {"application.properties", "api-application.properties"},
        factory = CompositePropertySourceFactory.class)
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
public class BffWmsSiteApplication {
    public static void main(String[] args) throws UnknownHostException {

        Environment env = SpringApplication.run(BffWmsSiteApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8092");
        String healthPort = env.getProperty("management.server.port", "9091");

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
