package com.wanmi.sbc.wms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author baijianzhong
 * @ClassName FluxConfig
 * @Date 2020-07-07 17:01
 * @Description TODO
 **/
@Component
public class FluxConfig {

    public static String appKey;

    public static String appSecret;

    public static String token;

    public static String clientCustomerId;

    public static String clientDb;

    @Value("${wms.appkey}")
    public void setAppKey(String appKey) {
        FluxConfig.appKey = appKey;
    }

    @Value("${wms.appToken}")
    public void setToken(String token) {
        FluxConfig.token = token;
    }

    @Value("${wms.appSecret}")
    public void setAppSecret(String appSecret) {
        FluxConfig.appSecret = appSecret;
    }

    @Value("${wms.clientDb}")
    public void setClientDb(String clientDb) {
        FluxConfig.clientDb = clientDb;
    }

    @Value("${wms.clientCustomerId}")
    public void setClientCustomerId(String clientCustomerId) {
        FluxConfig.clientCustomerId = clientCustomerId;
    }
}
