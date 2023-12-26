package com.wanmi.sbc.es.elastic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>多Elasticsearch链接配置：商品Elasticsearch配置</p>
 * @Author zhouzhenguo
 * @createDate 2023-08-16 15:54
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "spring.data.elasticsearch")
public class GoodsElasticsearchProperties {

    private String clusterName;

    private String clusterNodes;

    private Map<String, String> properties = new HashMap<>();
}
