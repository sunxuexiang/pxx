package com.wanmi.sbc.configure;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

//@Configuration
public class SentinelConfiguration {
    /**
     * @NOTE 在spring-cloud-alibaba v2.1.1.RELEASE及前，sentinel1.7.0及后，关闭URL PATH聚合需要通过该方式，
     * spring-cloud-alibaba v2.1.1.RELEASE后，可以通过配置关闭：spring.cloud.sentinel.web-context-unify=false
     * 手动注入Sentinel的过滤器，关闭Sentinel注入CommonFilter实例，修改配置文件中的 spring.cloud.sentinel.filter.enabled=false
     * 入口资源聚合问题：https://github.com/alibaba/Sentinel/issues/1024 或 https://github.com/alibaba/Sentinel/issues/1213
     * 入口资源聚合问题解决：https://github.com/alibaba/Sentinel/pull/1111
     */
//    @Bean
    public FilterRegistrationBean<Filter> registrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CommonFilter());
        registrationBean.addUrlPatterns("/*");
        // 入口资源关闭聚合
//        registrationBean.addInitParameter(CommonFilter, "false");
        registrationBean.setName("sentinelFilter");
        return registrationBean;
    }

}
