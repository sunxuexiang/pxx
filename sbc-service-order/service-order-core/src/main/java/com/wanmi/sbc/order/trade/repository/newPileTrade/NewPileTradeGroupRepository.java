package com.wanmi.sbc.order.trade.repository.newPileTrade;

import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTradeGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewPileTradeGroupRepository extends MongoRepository<NewPileTradeGroup, String> {
}
