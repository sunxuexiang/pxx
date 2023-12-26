package com.wanmi.sbc.setting;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.net.InetAddress;

@EnableDistributedTransaction
@SpringBootApplication
@Configuration
@Slf4j
@ComponentScan(basePackages={"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
@EnableJpaAuditing
public class SettingApplication {
    public static void main(String[] args) throws Exception {
        Environment env = SpringApplication.run(SettingApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8091");
        System.setProperty("nacos.logging.default.config.enabled", "false");
        String healthPort = env.getProperty("management.server.port", "9092");

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
