package com.wanmi.sbc.logisticsapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(value = "com.wanmi.sbc", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.wanmi.sbc.common.handler.aop.TomcatDataSourceAspect.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.wanmi.sbc.common.config.redis.RedisAutoConfiguration.class)
})
@MapperScan(basePackages = "com.wanmi.sbc.logisticscore.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.wanmi.sbc.provider"})
@PropertySource(value = {"api-application.properties"})
public class LogisticsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsAppApplication.class, args);
    }

}
