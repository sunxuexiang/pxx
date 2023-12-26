package com.wanmi.sbc.order.trade.repository;


import com.wanmi.sbc.order.trade.model.root.ProviderTrade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * 订单repository
 * Created by jinwei on 15/3/2017.
 */
public interface ProviderTradeRepository extends MongoRepository<ProviderTrade, String> {


    List<ProviderTrade> findListByParentId(String parentId);

    Optional<ProviderTrade> findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode
            (String id, String logisticNo);
    List<ProviderTrade> findByParentIdIn(List<String> parentIdList);

    ProviderTrade findFirstById(String id);

}

