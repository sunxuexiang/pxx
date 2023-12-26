package com.wanmi.sbc.wms.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: AppConfig
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 9:06
 * @Version: 1.0
 */
@Configuration
public class AppConfig {

    @Value("${template.timeout}")
    private Integer timeOut;

    @Bean
    public RestTemplate customRestTemplate(){
        //设置restTemplate超时时间
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(timeOut);
        httpRequestFactory.setConnectTimeout(timeOut);
        httpRequestFactory.setReadTimeout(timeOut);

        return new RestTemplate(httpRequestFactory);
    }

}
