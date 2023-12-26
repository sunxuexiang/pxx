package com.wanmi.sbc.mongo.oplog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-25
 * \* Time: 19:13
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Data
@Component
@ConfigurationProperties(prefix = "tunnel")
public class TunnelConfig {

    private String type         =   "kafka";

    @Deprecated
    private Integer partition   =   0;
}
