package com.wanmi.sbc.es.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description: ES 零售商品数据源操作接口
 * @Author: XinJiang
 * @Date: 2022/4/15 14:16
 */
@Repository
public interface EsRetailGoodsInfoElasticRepository extends ElasticsearchRepository<EsRetailGoodsInfo,String> {
}
