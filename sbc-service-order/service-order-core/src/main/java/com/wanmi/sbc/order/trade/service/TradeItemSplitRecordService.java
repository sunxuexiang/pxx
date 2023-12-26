package com.wanmi.sbc.order.trade.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.order.refund.service.RefundOrderService;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.model.root.TradeItemSplitRecord;
import com.wanmi.sbc.order.trade.repository.TradeItemSplitRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/8 15:59 
 * @params  
 * @return  
*/
@Service
@Transactional(readOnly = true, timeout = 10)
public class TradeItemSplitRecordService {

    @Autowired
    private TradeItemSplitRecordRepository tradeItemSplitRecordRepository;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Transactional
    @LcnTransaction
    public void save() {

    }

    /**
     * 新增流水单
     *
     * @param tradeItemSplitRecord tradeItemSplitRecord
     * @return Optional<TradeItemSplitRecord>
     */
    @Transactional
    public Optional<TradeItemSplitRecord> save(TradeItemSplitRecord tradeItemSplitRecord) {
        return Optional.ofNullable(tradeItemSplitRecordRepository.save(tradeItemSplitRecord));
    }

    @Transactional
    public Optional<TradeItemSplitRecord> saveAndFlush(TradeItemSplitRecord tradeItemSplitRecord) {
        return Optional.ofNullable(tradeItemSplitRecordRepository.saveAndFlush(tradeItemSplitRecord));
    }

    public List<TradeItemSplitRecord> findListByTradeNo(String tradeNo) {
        return tradeItemSplitRecordRepository.findListByTradeNo(tradeNo);
    }

    public void updateBackFlagById(Long id) {
        tradeItemSplitRecordRepository.updateBackFlagById(id);
    }

    /**
     * 根据退款单删除流水
     * @return rows
     */
    @Transactional
    public void deleteByRefundId(String id) {
        tradeItemSplitRecordRepository.deleteById(id);
    }
}
