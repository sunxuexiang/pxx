package com.wanmi.sbc.order.trade.service.newPileTrade.update;

import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTradeItemSnapshot;
import com.wanmi.sbc.order.trade.model.root.TradeItemSnapshot;
import com.wanmi.sbc.order.trade.repository.newPileTrade.NewPileTradeItemSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewPileTradeItemSnapshotService {

    @Autowired
    private NewPileTradeItemSnapshotRepository tradeItemSnapshotRepository;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.ADD)
    public void addTradeItemSnapshot(NewPileTradeItemSnapshot tradeItemSnapshot) {
        tradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.UPDATE)
    public void updateTradeItemSnapshot(NewPileTradeItemSnapshot tradeItemSnapshot) {
        tradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 删除文档
     *
     * @param id
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, idExpress = "id", operation = Operation.UPDATE)
    public void deleteTradeItemSnapshot(String id) {
        tradeItemSnapshotRepository.deleteById(id);
    }

    public void updateTradeItemSnapshotNoRollback(NewPileTradeItemSnapshot tradeItemSnapshot){
        tradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

}
