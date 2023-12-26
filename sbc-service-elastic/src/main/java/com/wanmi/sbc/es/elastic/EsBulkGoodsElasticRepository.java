package com.wanmi.sbc.es.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description: ES 零售商品spu数据源操作接口
 * @Author: XinJiang
 * @Date: 2022/4/15 14:18
 */
@Repository
public interface EsBulkGoodsElasticRepository extends ElasticsearchRepository<EsBulkGoods,String> {
}
