package com.wanmi.sbc.order.trade.repository.newPileTrade;


import com.wanmi.sbc.order.trade.model.newPileTrade.NewPilePickTradeItemSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>订单商品快照数据持久化操作</p>
 * Created by of628-wenzhi on 2017-07-13-上午10:50.
 */
@Repository
public interface NewPilePickTradeItemSnapshotRepository extends MongoRepository<NewPilePickTradeItemSnapshot, String> {

    Optional<NewPilePickTradeItemSnapshot> findByBuyerId(String buyerId);

    void deleteByBuyerId(String buyerId);
}
