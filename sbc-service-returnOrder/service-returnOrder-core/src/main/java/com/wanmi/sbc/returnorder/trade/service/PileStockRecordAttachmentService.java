package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.returnorder.trade.model.root.StockRecordAttachment;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordAttachmentRepostory;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.returnorder.trade.repository.TradeCachePushKingdeeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提货与囤货关联
 *
 * @author yitang
 * @version 1.0
 */
@Service
@Slf4j
public class PileStockRecordAttachmentService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PileStockRecordAttachmentRepostory pileStockRecordAttachmentRepostory;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private TradeCachePushKingdeeOrderRepository tradeCachePushKingdeeOrderRepository;

    /**
     * 通过提货单获取囤货单
     * @param tids
     * @return
     */
    public List<StockRecordAttachment> getStockAssociatedOrderTid(List<String> tids){
        List<StockRecordAttachment> stockRecordAttachmentList = new ArrayList<>();
        List<Object> stockRecord = pileStockRecordAttachmentRepostory.findStockAssociatedOrderTid(tids);
        if (CollectionUtils.isNotEmpty(stockRecord)){
            stockRecordAttachmentList.addAll(stockRecord.stream().map(StockRecordAttachment::convertFromNativeSQLResult).collect(Collectors.toList()));
        }
        logger.info("PileStockRecordAttachmentService.getStockAssociatedOrderTid stockRecord:{}", JSONObject.toJSONString(stockRecord),
                    JSONObject.toJSONString(stockRecordAttachmentList));
        return stockRecordAttachmentList;
    }

    /**
     * 通过囤货拿到提货订单
     * @param orderCodes
     * @return
     */
    public List<StockRecordAttachment> getStockAssociatedOrderCode(List<String> orderCodes){
        List<StockRecordAttachment> stockRecordAttachmentList = new ArrayList<>();
        List<Object> stockRecord = pileStockRecordAttachmentRepostory.findStockAssociatedOrderCode(orderCodes);
        if (CollectionUtils.isNotEmpty(stockRecord)){
            stockRecordAttachmentList.addAll(stockRecord.stream().map(StockRecordAttachment::convertFromNativeSQLResult).collect(Collectors.toList()));
        }
        logger.info("PileStockRecordAttachmentService.getStockAssociatedOrderCode stockRecord:{}",JSONObject.toJSONString(stockRecord),
                    JSONObject.toJSONString(stockRecordAttachmentList));
        return stockRecordAttachmentList;
    }

    /**
     * 囤货对应的商品
     * @param orderCode
     * @param skuList
     * @return
     */
    public List<PileStockRecord> getPileStockRecordByOrderCodeAll(String orderCode,List<String> skuList){
        logger.info("PileStockRecordAttachmentService.getPileStockRecordByOrderCodeAll orderCode:{}",orderCode);
        return pileStockRecordRepository.getPileStockRecordByOrderCodeAll(orderCode,skuList);
    }

    /**
     * 批量查询拦截订单
     * @param orders
     * @return
     */
    public List<String> getCachePushKingdeeOrder(List<String> orders){
        return tradeCachePushKingdeeOrderRepository.getCachePushKingdeeOrder(orders);
    }
}
