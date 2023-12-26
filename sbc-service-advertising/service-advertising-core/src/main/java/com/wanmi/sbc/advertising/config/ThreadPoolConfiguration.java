package com.wanmi.sbc.advertising.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfiguration {

	@Bean
	public ThreadPoolTaskExecutor sendMqMsgTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 设置核心线程数
		executor.setCorePoolSize(3);
		// 设置最大线程数
		executor.setMaxPoolSize(6);
		// 设置队列容量
		executor.setQueueCapacity(100);
		// 设置线程活跃时间（秒）
		executor.setKeepAliveSeconds(60);
		// 设置默认线程名称
		executor.setThreadNamePrefix("发送mq消息线程-");
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}
	
	@Bean
	public ThreadPoolTaskExecutor updateRedisTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 设置核心线程数
		executor.setCorePoolSize(6);
		// 设置最大线程数
		executor.setMaxPoolSize(12);
		// 设置队列容量
		executor.setQueueCapacity(200);
		// 设置线程活跃时间（秒）
		executor.setKeepAliveSeconds(90);
		// 设置默认线程名称
		executor.setThreadNamePrefix("更新redis线程-");
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}
}