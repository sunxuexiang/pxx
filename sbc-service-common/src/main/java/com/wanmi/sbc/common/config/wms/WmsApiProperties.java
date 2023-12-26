package com.wanmi.sbc.common.config.wms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/26 10:52
 */

@Data
@ConfigurationProperties(prefix = "wms.api")
@Component
public class WmsApiProperties {

    private Boolean flag = true;

    private Boolean inventoryFlag = true;
}
