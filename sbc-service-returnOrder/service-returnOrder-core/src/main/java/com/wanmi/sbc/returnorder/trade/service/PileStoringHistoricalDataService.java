package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.returnorder.bean.enums.AuditState;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchase;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.returnorder.pilepurchase.model.StoreUserInformation;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Service
@Slf4j
public class PileStoringHistoricalDataService {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private PileTradeService pileTradeService;

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    public void storingHistoricalData(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        long start,end;
                        start = System.currentTimeMillis();
                        log.info("PileStoringHistoricalDataService.storingHistoricalData start:{}",start);
                        Long purchaseId = 0L;
                        do {
                            //获取已囤用户信息
                            List<Object> customerIds = pilePurchaseRepository.getPilePurchaseCustomerId(purchaseId);
                            List<StoreUserInformation> storeUserInformationList = customerIds.stream().map(StoreUserInformation::convertFromNativeSQLResult).collect(Collectors.toList());
                            log.info("PileStoringHistoricalDataService.storingHistoricalData customerIds:{}",JSONObject.toJSONString(customerIds));
                            log.info("PileStoringHistoricalDataService.storingHistoricalData storeUserInformationList:{}",JSONObject.toJSONString(storeUserInformationList));
                            if (storeUserInformationList.size()>0) {
                                storeUserInformationList.stream().forEach(purchase -> {
                                    Integer pageNum = 0;
                                    do {
                                        //获取用户的所有已付款囤货订单
                                        TradeState tradeState = TradeState.builder()
                                                                          .payState(PayState.PAID)
                                                                          .auditState(AuditState.CHECKED)
                                                                          .flowState(FlowState.AUDIT)
                                                                          .build();
                                        TradeQueryRequest queryRequest = new TradeQueryRequest();
                                        queryRequest.setBuyerId(purchase.getCustomerId());
                                        queryRequest.setTradeState(tradeState);
                                        queryRequest.setPageNum(pageNum);
                                        queryRequest.setPageSize(10);
                                        Criteria criteria = queryRequest.getWhereCriteria();
                                        log.info("PileStoringHistoricalDataService.storingHistoricalData criteria:{}", JSONObject.toJSONString(criteria));
                                        List<PileTrade> pileTrades = pileTradeService.page(criteria, queryRequest).getContent();
                                        List<String> tradeId = pileTrades.stream().map(PileTrade::getId).collect(Collectors.toList());
                                        log.info("PileStoringHistoricalDataService.storingHistoricalData pileTrades.size:{} customerId:{}", pileTrades.size(), purchase.getCustomerId());
                                        log.info("PileStoringHistoricalDataService.storingHistoricalData tradeId:{}",JSONObject.toJSONString(tradeId));
                                        //获取用户的囤货剩余数据
                                        List<PilePurchase> pileTradeList = pilePurchaseRepository.getPilePurchaseByCustomerId(purchase.getCustomerId());
                                        List<PileStockRecord> stockRecordList = new ArrayList<>();
                                        //商品使用的数量集合
                                        Map<String, Long> goodsNum = new HashMap<>();
                                        pileTradeList.stream().forEach(pileTrade -> {
                                            if (pileTrade.getGoodsNum() > 0) {
                                                pileTrades.stream().forEach(trade -> {
                                                    trade.getTradeItems().stream().forEach(tradeItem -> {
                                                        if (pileTrade.getGoodsInfoId().equals(tradeItem.getSkuId())) {
                                                            Long num = 0L;
                                                            if (goodsNum.get(pileTrade.getGoodsInfoId()) != null) {
                                                                num = goodsNum.get(pileTrade.getGoodsInfoId());
                                                            }
                                                            Long reductionNum = pileTrade.getGoodsNum() - num;
                                                            if (pileTrade.getGoodsNum() > 0L && pileTrade.getGoodsNum() >= num
                                                                    && reductionNum >= tradeItem.getNum()) {
                                                                BigDecimal realPay = BigDecimal.ZERO;
                                                                if (tradeItem.getSplitPrice() != null && tradeItem.getNum() != null) {
                                                                    realPay = tradeItem.getSplitPrice().divide(new BigDecimal(tradeItem.getNum()),2, BigDecimal.ROUND_HALF_UP);
                                                                }
                                                                PileStockRecord record = PileStockRecord.builder()
                                                                                                        .goodsId(tradeItem.getSpuId())
                                                                                                        .customerId(trade.getBuyer().getId())
                                                                                                        .orderCode(trade.getId())
                                                                                                        .goodsInfoId(tradeItem.getSkuId())
                                                                                                        .stockRecordNum(tradeItem.getNum())
                                                                                                        .stockRecordRemainingNum(0L)
                                                                                                        .stockRecordPrice(realPay)
                                                                                                        .isUse(0L)
                                                                                                        .createTime(LocalDateTime.now())
                                                                                                        .updateTime(LocalDateTime.now())
                                                                                                        .build();
                                                                goodsNum.put(pileTrade.getGoodsInfoId(), num + tradeItem.getNum());
                                                                stockRecordList.add(record);
                                                            } else if (pileTrade.getGoodsNum() > 0L && pileTrade.getGoodsNum() >= num
                                                                    && reductionNum < tradeItem.getNum() && reductionNum > 0L) {
                                                                BigDecimal realPay = BigDecimal.ZERO;
                                                                if (tradeItem.getSplitPrice() != null && tradeItem.getNum() != null) {
                                                                    log.info("PileStoringHistoricalDataService.storingHistoricalData splitPrice:{} num:{}",tradeItem.getSplitPrice()
                                                                            ,tradeItem.getNum());
                                                                    realPay = tradeItem.getSplitPrice().divide(new BigDecimal(tradeItem.getNum()),2, BigDecimal.ROUND_HALF_UP);
                                                                }
                                                                PileStockRecord record = PileStockRecord.builder()
                                                                                                        .goodsId(tradeItem.getSpuId())
                                                                                                        .customerId(trade.getBuyer().getId())
                                                                                                        .orderCode(trade.getId())
                                                                                                        .goodsInfoId(tradeItem.getSkuId())
                                                                                                        .stockRecordNum(reductionNum)
                                                                                                        .stockRecordRemainingNum(0L)
                                                                                                        .stockRecordPrice(realPay)
                                                                                                        .isUse(0L)
                                                                                                        .createTime(LocalDateTime.now())
                                                                                                        .updateTime(LocalDateTime.now())
                                                                                                        .build();
                                                                goodsNum.put(pileTrade.getGoodsInfoId(), num + reductionNum);
                                                                stockRecordList.add(record);
                                                            }
                                                        }
                                                    });
                                                });
                                            }
                                        });
                                        pileStockRecordRepository.saveAll(stockRecordList);
                                        if (pileTrades.size()>0){
                                            pageNum = pageNum + 1;
                                        }else {
                                            pageNum = 0;
                                        }
                                    }while (pageNum > 0);
                                });
                                purchaseId = storeUserInformationList.get(storeUserInformationList.size()-1).getPurchaseId();
                            }else {
                                purchaseId = 0L;
                            }
                        }while (purchaseId > 0L);
                        end = System.currentTimeMillis();
                        log.info("PileStoringHistoricalDataService.storingHistoricalData end:{}",end-start);
                    }
                }
        ).start();
    }
//
//    public void pileStoringHistoricalData(){
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        Long pageNum = 0L;
//                        Boolean scanning = true;
//                        do {
//                            List<PilePurchaseAction> pilePurchaseActionList = pilePurchaseActionRepository.getPilePurchaseAction(pageNum);
//                            if (pilePurchaseActionList.size() > 0){
//                                List<String> orders = pilePurchaseActionList.stream().map(PilePurchaseAction::getOrderCode).collect(Collectors.toList());
//                                if (orders.size() > 0) {
//                                    List<PileTrade> pileTradeList = pileTradeService
//                                            .queryAll(TradeQueryRequest.builder().ids(orders.toArray(new String[orders.size()])).orderType(OrderType.ALL_ORDER).build())
//                                            .stream()
//                                            .collect(Collectors.toList());
//                                    pileTradeList.stream().forEach(pileTrade -> {
//                                        pileTrade.getTradeItems().stream().forEach(tradeItem -> {
//                                            pilePurchaseActionList.stream().forEach(pilePurchaseAction -> {
//                                                if (pilePurchaseAction.getGoodsInfoId().equals(tradeItem.getSkuId())){
//                                                    //计算实付金额
//                                                    BigDecimal realPay = tradeItem.getSettlementPrice().divide(new BigDecimal(tradeItem.getNum()));
//                                                    pilePurchaseAction.setGoodsSplitPrice(realPay);
//                                                }
//                                            });
//
//                                        });
//                                    });
//                                }
//                            }else {
//                                scanning = false;
//                            }
//                        }while (scanning);
//                    }
//                }
//        ).start();
//    }
}
