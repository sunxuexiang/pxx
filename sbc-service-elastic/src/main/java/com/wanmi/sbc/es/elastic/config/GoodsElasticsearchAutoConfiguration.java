package com.wanmi.sbc.es.elastic.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;

import java.util.Properties;

/**
 * <p>多Elasticsearch链接配置：商品Elasticsearch配置</p>
 * @Author zhouzhenguo
 * @createDate 2023-08-16 15:54
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({Client.class, TransportClientFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.elasticsearch", name = {"cluster-nodes"}, matchIfMissing = false)
@EnableConfigurationProperties({GoodsElasticsearchProperties.class})
public class GoodsElasticsearchAutoConfiguration {

    private final GoodsElasticsearchProperties goodsElasticsearchProperties;

    public GoodsElasticsearchAutoConfiguration(GoodsElasticsearchProperties goodsElasticsearchProperties) {
        this.goodsElasticsearchProperties = goodsElasticsearchProperties;
    }

    @Bean(name = "client")
    public TransportClient client () throws Exception {
        TransportClientFactoryBean factoryBean = new TransportClientFactoryBean();
        factoryBean.setClusterNodes(goodsElasticsearchProperties.getClusterNodes());
        factoryBean.setProperties(createProperties());
        factoryBean.afterPropertiesSet();
        TransportClient transportClient = factoryBean.getObject();
        log.info("商品Elasticsearch 初始化 {}", transportClient.transportAddresses());
        return transportClient;
    }

    private Properties createProperties () {
        Properties properties = new Properties();
        properties.setProperty("cluster.name", goodsElasticsearchProperties.getClusterName());
        properties.putAll(goodsElasticsearchProperties.getProperties());
        return properties;
    }
}
