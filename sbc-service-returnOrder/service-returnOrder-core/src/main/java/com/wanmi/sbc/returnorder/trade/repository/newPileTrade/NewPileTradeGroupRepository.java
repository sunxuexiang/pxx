package com.wanmi.sbc.returnorder.trade.repository.newPileTrade;

import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTradeGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewPileTradeGroupRepository extends MongoRepository<NewPileTradeGroup, String> {
}
