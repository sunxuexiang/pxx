package com.wanmi.sbc.goods.log;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 商品审核日志repository
 * Created by daiyitian on 16/11/2017.
 */
public interface GoodsCheckLogRepository extends MongoRepository<GoodsCheckLog, String> {

}
