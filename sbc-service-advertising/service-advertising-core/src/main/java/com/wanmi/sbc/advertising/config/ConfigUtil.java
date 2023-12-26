package com.wanmi.sbc.advertising.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@Data
public class ConfigUtil {
	
    /**
     * 默认延时30分钟
     */
    @Value("${pay.cancel.delay:#{30*60*1000}}")
    private Integer payCancelDelay;
    
    /**
     * #鲸币平台id
     */
    @Value("${wallet.platform.storeId}")
    private String platformStoreId;
    
    /**
     * 启动页广告默认持续时间
     */
    @Value("${ad.bootPage.duration:5}")
    private Integer bootPageDuration;
    
    /**
     * 批发市场下商品广告位推荐排名数量
     */
    @Value("${ad.mallGooods.limit:30}")
    private Integer mallGooodsLimit;
    
    /**
     * 首页banner广告位数量
     */
    @Value("${ad.banner.limit:6}")
    private Integer bannerLimit;
    
    /**
     * 广告位默认价格
     */
    @Value("${ad.defaultPrice}")
    private BigDecimal defaultPrice;
    
    /**
     * 广告位初始化购买日期天数
     */
    @Value("${ad.initDate.limit}")
    private Integer initDateLimit;
    
    /**
     * #上线商品广告临时配置，key为批发市场id_商城id_类目id
     */
    @Value("#{${adv.maps}}")
    private  Map<String, String> advMap;
    
    
   

}
