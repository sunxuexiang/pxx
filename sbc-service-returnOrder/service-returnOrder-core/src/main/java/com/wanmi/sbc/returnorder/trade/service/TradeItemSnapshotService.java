package com.wanmi.sbc.returnorder.trade.service;

import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemSnapshot;
import com.wanmi.sbc.returnorder.trade.repository.TradeItemSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 21:58
 */
@Service
public class TradeItemSnapshotService {

    @Autowired
    private TradeItemSnapshotRepository tradeItemSnapshotRepository;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.ADD)
    public void addTradeItemSnapshot(TradeItemSnapshot tradeItemSnapshot) {
        tradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.UPDATE)
    public void updateTradeItemSnapshot(TradeItemSnapshot tradeItemSnapshot) {
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

    public void updateTradeItemSnapshotNoRollback(TradeItemSnapshot tradeItemSnapshot){
        tradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

}
