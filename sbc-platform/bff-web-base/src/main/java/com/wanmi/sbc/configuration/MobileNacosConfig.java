package com.wanmi.sbc.configuration;

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
public class MobileNacosConfig {

    @Value("${storeLiveOpenMessage:直播卖货中}")
    private String storeLiveOpenMessage;

    @Value("${storeLiveCloseMessage:暂未开始直播}")
    private String storeLiveCloseMessage;

    @Value("${storeLiveSearchSupplier:直播}")
    private String storeLiveSearchSupplier;

    // 默认1是开启的
    @Value("${goodsSearchByMarketFlag:1}")
    private Integer goodsSearchByMarketFlag;

    @Value("${mobile.config.gifUrlHomepage:[]}")
    private String popUrlHomePage;

    @Value("${goodsSearchByMarketFlagNum:1000}")
    private Integer goodsSearchByMarketFlagNum;

    @Value("${mobile.config.gifUrlHomepageRec:[]}")
    private String popUrlHomePageRec;

    @Value("${searchSpuForSearchSearchOpen:1}")
    private Integer searchSpuForSearchSearchOpen;

    @Value("${homePageMode:1}")
    private Integer homePageMode;
}
