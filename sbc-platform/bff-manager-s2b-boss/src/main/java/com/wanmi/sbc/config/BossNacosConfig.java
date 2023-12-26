package com.wanmi.sbc.config;

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
public class BossNacosConfig {

    @Value("${buyerGoodsNumTrade:30}")
    private Integer buyerGoodsNumTrade;
}
