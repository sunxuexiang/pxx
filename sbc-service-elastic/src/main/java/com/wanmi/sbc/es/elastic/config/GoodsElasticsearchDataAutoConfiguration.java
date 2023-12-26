package com.wanmi.sbc.es.elastic.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * <p>多Elasticsearch链接配置：商品Elasticsearch配置</p>
 * @Author zhouzhenguo
 * @createDate 2023-08-16 15:54
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class})
@AutoConfigureAfter(GoodsElasticsearchAutoConfiguration.class)
@EnableElasticsearchRepositories(basePackages = "com.wanmi.sbc.es.elastic.config", elasticsearchTemplateRef = "elasticsearchTemplate")
public class GoodsElasticsearchDataAutoConfiguration {

    @Bean("elasticsearchTemplate")
    @ConditionalOnBean(Client.class)
    public ElasticsearchTemplate elasticsearchTemplate (@Qualifier("client") Client client,
            ElasticsearchConverter converter) {
        try {
            return new ElasticsearchTemplate(client, converter);
        }
        catch (Exception e) {
            log.error("商品ElasticsearchTemplate初始化异常", e);
            throw new IllegalStateException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchConverter elasticsearchConverter (SimpleElasticsearchMappingContext mappingContext) {
        return new MappingElasticsearchConverter(mappingContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public SimpleElasticsearchMappingContext mappingContext () {
        return new SimpleElasticsearchMappingContext();
    }

}
