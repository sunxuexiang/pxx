package com.wanmi.sbc.returnorder.trade.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.trade.model.root.PileTradeGroup;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeGroupRepository;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PileTradeGroupService {

    @Autowired
    private PileTradeGroupRepository pileTradeGroupRepository;


    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     * @param tradeGroup
     */
    @MongoRollback(persistence = PileTradeGroup.class,operation = Operation.ADD)
    public void addTradeGroup(PileTradeGroup tradeGroup) {
        pileTradeGroupRepository.save(tradeGroup);
    }

    public List<PileTradeGroup> findByIds(List<String> ids){
        Iterable<PileTradeGroup> allById = pileTradeGroupRepository.findAllById(ids);
        if(IterableUtils.size(allById) < 1){
            return Lists.newArrayList();
        }
        return IteratorUtils.toList(allById.iterator());
    }



}
