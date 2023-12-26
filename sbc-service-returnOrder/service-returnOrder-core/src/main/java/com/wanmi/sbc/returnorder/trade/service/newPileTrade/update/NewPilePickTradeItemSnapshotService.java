package com.wanmi.sbc.returnorder.trade.service.newPileTrade.update;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPilePickTradeItemSnapshot;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemSnapshot;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPilePickTradeItemSnapshotRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NewPilePickTradeItemSnapshotService {

    @Autowired
    private NewPilePickTradeItemSnapshotRepository newPilePickTradeItemSnapshotRepository;

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.UPDATE)
    public void updateTradeItemSnapshot(NewPilePickTradeItemSnapshot tradeItemSnapshot) {
        newPilePickTradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param tradeItemSnapshot
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, operation = Operation.ADD)
    public void addTradeItemSnapshot(NewPilePickTradeItemSnapshot tradeItemSnapshot) {
        newPilePickTradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 删除文档
     *
     * @param id
     */
    @MongoRollback(persistence = TradeItemSnapshot.class, idExpress = "id", operation = Operation.UPDATE)
    public void deleteTradeItemSnapshot(String id) {
        newPilePickTradeItemSnapshotRepository.deleteById(id);
    }

    public void updateTradeItemSnapshotNoRollback(NewPilePickTradeItemSnapshot tradeItemSnapshot){
        newPilePickTradeItemSnapshotRepository.save(tradeItemSnapshot);
    }

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public List<TradeItemGroup> findAll(String customerId) {
        Optional<NewPilePickTradeItemSnapshot> optional = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId);
        List<TradeItemGroup> groupList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(optional.get().getItemGroups())) {
            groupList.addAll(optional.get().getItemGroups());
        }
        if (CollectionUtils.isNotEmpty(optional.get().getRetailItemGroups())) {
            groupList.addAll(optional.get().getRetailItemGroups());
        }
        if (CollectionUtils.isEmpty(groupList)) {
            throw new SbcRuntimeException("K-050201");
        }
        return groupList;
    }
}
