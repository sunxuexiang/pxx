package com.wanmi.sbc;

import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/9/19 15:32
 */


@Slf4j
@SpringBootApplication(scanBasePackages = {"com.wanmi.sbc","com.codingapi.txlcn"})
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
public class BffCallbackApplication {
    public static void main(String[] args) throws UnknownHostException {
        Environment env = SpringApplication.run(BffCallbackApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8790");
        String healthPort = env.getProperty("management.server.port", "8791");

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