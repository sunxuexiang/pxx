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
 * <p>多Elasticsearch链接配置：腾讯IM历史消息聊天Elasticsearch配置</p>
 * @Author zhouzhenguo
 * @createDate 2023-08-16 15:54
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({Client.class, TransportClientFactoryBean.class})
@ConditionalOnProperty(prefix = "elasticsearch.message", name = {"cluster-nodes"}, matchIfMissing = false)
@EnableConfigurationProperties({MessageElasticsearchProperties.class})
public class MessageElasticsearchAutoConfiguration {

    private final MessageElasticsearchProperties messageElasticsearchProperties;

    public MessageElasticsearchAutoConfiguration (MessageElasticsearchProperties messageElasticsearchProperties) {
        this.messageElasticsearchProperties = messageElasticsearchProperties;
    }

    @Bean(name = "messageTransportClient")
    public TransportClient messageElasticsearchClient () throws Exception {
        TransportClientFactoryBean factoryBean = new TransportClientFactoryBean();
        factoryBean.setClusterNodes(messageElasticsearchProperties.getClusterNodes());
        factoryBean.setProperties(createProperties());
        factoryBean.afterPropertiesSet();
        TransportClient transportClient = factoryBean.getObject();
        log.info("腾讯IM Elasticsearch 初始化 {}", transportClient.transportAddresses());
        return transportClient;
    }

    private Properties createProperties () {
        Properties properties = new Properties();
        properties.setProperty("cluster.name", messageElasticsearchProperties.getClusterName());
        properties.putAll(messageElasticsearchProperties.getProperties());
        return properties;
    }
}
