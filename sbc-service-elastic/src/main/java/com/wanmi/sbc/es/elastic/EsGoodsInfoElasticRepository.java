package com.wanmi.sbc.es.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ES商品信息数据源操作
 * Created by daiyitian on 2017/4/21.
 */
@Repository
public interface EsGoodsInfoElasticRepository extends ElasticsearchRepository<EsGoodsInfo, String> {

}
