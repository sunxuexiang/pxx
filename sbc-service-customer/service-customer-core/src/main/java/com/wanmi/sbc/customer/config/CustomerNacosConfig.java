package com.wanmi.sbc.customer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-11-02 14:05
 **/
@Configuration
@RefreshScope
@Getter
public class CustomerNacosConfig {

    @Value("${init_store_log:https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311021102087944.png}")
    private String initStoreLog;
}
