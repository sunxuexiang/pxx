package com.wanmi.sbc;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import com.wanmi.sbc.common.configure.CompositePropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

@EnableDistributedTransaction
@SpringBootApplication(scanBasePackages = {"com.wanmi.sbc"})
@EnableTransactionManagement
@EnableAsync
@EnableDiscoveryClient
@Slf4j
@MapperScan(basePackages = {"com.wanmi.sbc.live.*.dao"})
@EnableFeignClients(basePackages = {"com.wanmi.sbc"})
@PropertySource(value = {"api-application.properties"}, factory = CompositePropertySourceFactory.class)
@EnableCaching
public class LiveServicesApplication {

	public static void main(String[] args) throws UnknownHostException {
		Environment env = SpringApplication.run(LiveServicesApplication.class, args).getEnvironment();
		String port = env.getProperty("server.port", "8180");
		String healthPort = env.getProperty("management.server.port", "9101");

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
