package com.wanmi.sbc.returnorder.trade.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.trade.model.root.TradeGroup;
import com.wanmi.sbc.returnorder.trade.repository.TradeGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TradeGroupService {

    @Autowired
    private TradeGroupRepository tradeGroupRepository;


    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     * @param tradeGroup
     */
    @MongoRollback(persistence = TradeGroup.class,operation = Operation.ADD)
    public void addTradeGroup(TradeGroup tradeGroup) {
        tradeGroupRepository.save(tradeGroup);
    }


    public List<TradeGroup> findByIds(List<String> ids){
        Iterable<TradeGroup> allById = tradeGroupRepository.findAllById(ids);
        if(IterableUtils.size(allById) < 1){
            return Lists.newArrayList();
        }else{
            return IteratorUtils.toList(allById.iterator());
        }
    }


}
