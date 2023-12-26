package com.wanmi.sbc.marketing.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@Data
public class ConfigUtil {
	
    /**
     * 全场返鲸币提示模板
     */
    @Value("${orderCoinTipsTpl.all}")
    private String allOrderCoinTipsTpl;
    
    
   

}
