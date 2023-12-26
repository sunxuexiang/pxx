package com.wanmi.sbc.order.trade.service.newPileTrade.update;

import com.google.common.collect.Lists;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTradeGroup;
import com.wanmi.sbc.order.trade.model.root.TradeGroup;
import com.wanmi.sbc.order.trade.repository.newPileTrade.NewPileTradeGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NewPileTradeGroupService {

    @Autowired
    private NewPileTradeGroupRepository newPileTradeGroupRepository;


    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     * @param tradeGroup
     */
    @MongoRollback(persistence = TradeGroup.class,operation = Operation.ADD)
    public void addTradeGroup(NewPileTradeGroup tradeGroup) {
        newPileTradeGroupRepository.save(tradeGroup);
    }


    public List<NewPileTradeGroup> findByIds(List<String> ids){
        Iterable<NewPileTradeGroup> allById = newPileTradeGroupRepository.findAllById(ids);
        if(IterableUtils.size(allById) < 1){
            return Lists.newArrayList();
        }else{
            return IteratorUtils.toList(allById.iterator());
        }
    }

}
