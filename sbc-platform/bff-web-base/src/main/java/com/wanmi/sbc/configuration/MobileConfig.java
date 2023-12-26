package com.wanmi.sbc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/24 16:45
 */
@Component
@ConfigurationProperties(prefix = "mobile.config")
@Data
public class MobileConfig {

    private Boolean showPublishIcon = false;

    private Boolean showLiveIcon = false;

    private Long homeDefaultMarketId = 1L;

    private Long homeDefaultStoreId = 123457929L;

    private Long waitingForStoreId = 1L;

    private String gifUrlHomepage;
}
