package com.wanmi.sbc.returnorder.trade.service;

import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.trade.model.root.PileTradeItemSnapshot;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeItemSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 21:58
 */
@Service
public class PileTradeItemSnapshotService {

    @Autowired
    private PileTradeItemSnapshotRepository pileTradeItemSnapshotRepository;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param pileTradeItemSnapshot
     */
    @MongoRollback(persistence = PileTradeItemSnapshot.class, operation = Operation.ADD)
    public void addTradeItemSnapshot(PileTradeItemSnapshot pileTradeItemSnapshot) {
        pileTradeItemSnapshotRepository.save(pileTradeItemSnapshot);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param pileTradeItemSnapshot
     */
    @MongoRollback(persistence = PileTradeItemSnapshot.class, operation = Operation.UPDATE)
    public void updateTradeItemSnapshot(PileTradeItemSnapshot pileTradeItemSnapshot) {
        pileTradeItemSnapshotRepository.save(pileTradeItemSnapshot);
    }

    /**
     * 删除文档
     *
     * @param id
     */
    @MongoRollback(persistence = PileTradeItemSnapshot.class, idExpress = "id", operation = Operation.UPDATE)
    public void deleteTradeItemSnapshot(String id) {
        pileTradeItemSnapshotRepository.deleteById(id);
    }

    public void updateTradeItemSnapshotNoRollback(PileTradeItemSnapshot pileTradeItemSnapshot){
        pileTradeItemSnapshotRepository.save(pileTradeItemSnapshot);
    }

}
