package com.wanmi.sbc.mq.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/8 18:43
 */
@Data
@Component
@ConfigurationProperties(prefix = "order.award")
public class OrderAwardSwitchProperties {
    /**
     * 省内300箱 返运费（赠鲸币） 开关
     */
    private Boolean switchFlag;

    /**
     * 省外30箱 返运费（赠鲸币） 开关
     */
    private Boolean otherSwitchFlag;
}
