package com.wanmi.sbc.order.inventorydetailsamount.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.order.inventorydetailsamount.repository.InventoryDetailSamountRepository;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryDetailSamountService {

    @Autowired
    private InventoryDetailSamountRepository inventoryDetailSamountRepository;


    /**
     * 插入分摊数据方法
     * @param tradeItems 订单商品集合
     * @param oid 订单id
     */
    public void saveGoodsShareMoney(List<TradeItem> tradeItems,String oid){
        List<InventoryDetailSamount> list = new LinkedList<>();
        tradeItems.forEach(v->{
            list.addAll(shareItems(v,oid));
        });
        inventoryDetailSamountRepository.saveAll(list);
    }
    public  List<InventoryDetailSamount>    shareItems(TradeItem tradeItem,String oid){
        String skuId = tradeItem.getSkuId();
        List<InventoryDetailSamount> list = new LinkedList<>();
        if (tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) >= 0) {
            //现金
            BigDecimal allmoney = tradeItem.getSplitPrice();
            BigDecimal readyallmoney = BigDecimal.ZERO;
            int num = tradeItem.getNum().intValue();
            for (int i =0 ;i<num;i++){
                if (i==num-1){
                    list.add(InventoryDetailSamount.builder()
                            .createTime(LocalDateTime.now())
                            .moneyType(1)
                            .amortizedExpenses(allmoney.subtract(readyallmoney))
                            .goodsInfoId(skuId)
                            .newPileTradeId(oid)
                            .takeFlag(0)
                            .build());
                    continue;
                }
                BigDecimal bigDecimal = allmoney.divide(BigDecimal.valueOf(num),2,BigDecimal.ROUND_DOWN);
                list.add(InventoryDetailSamount.builder()
                        .createTime(LocalDateTime.now())
                        .moneyType(1)
                        .amortizedExpenses(bigDecimal)
                        .goodsInfoId(skuId)
                        .newPileTradeId(oid)
                        .takeFlag(0)
                        .build());
                readyallmoney=readyallmoney.add(bigDecimal);
            }

        }
        if (CollectionUtils.isNotEmpty(tradeItem.getWalletSettlements())  && Objects.nonNull(tradeItem.getWalletSettlements().get(0).getReduceWalletPrice())){
            //余额
            BigDecimal allmoney = tradeItem.getWalletSettlements().get(0).getReduceWalletPrice();
            BigDecimal readyallmoney = BigDecimal.ZERO;
            int num = tradeItem.getNum().intValue();
            for (int i =0 ;i<num;i++){
                if (i==num-1){
                    list.add(InventoryDetailSamount.builder()
                            .createTime(LocalDateTime.now())
                            .moneyType(0)
                            .amortizedExpenses(allmoney.subtract(readyallmoney))
                            .goodsInfoId(skuId)
                            .newPileTradeId(oid)
                            .takeFlag(0)
                            .build());
                    continue;
                }
                BigDecimal bigDecimal = allmoney.divide(BigDecimal.valueOf(num),2,BigDecimal.ROUND_DOWN);
                list.add(InventoryDetailSamount.builder()
                        .createTime(LocalDateTime.now())
                        .moneyType(0)
                        .amortizedExpenses(bigDecimal)
                        .goodsInfoId(skuId)
                        .newPileTradeId(oid)
                        .takeFlag(0)
                        .build());
                readyallmoney=readyallmoney.add(bigDecimal);
            }
        }
        return list;
    }

    /**
     * 提货方法(作废)
     * @param oid 囤货id
     * @param goodsInfoId 商品id
     * @param num 数量
     */
    /*public void updateInventoryDetailSamountFlag(String oid,String goodsInfoId,int num,String takeId){
        List<InventoryDetailSamount> bytidAndGoodsInfoId = inventoryDetailSamountRepository.getBytidAndGoodsInfoId(goodsInfoId, oid,0);
        //分组
        Map<Integer, List<InventoryDetailSamount>> collect = bytidAndGoodsInfoId.stream().collect(Collectors.groupingBy(InventoryDetailSamount::getMoneyType));
        for (Map.Entry<Integer, List<InventoryDetailSamount>> a:collect.entrySet()){
                List<InventoryDetailSamount> collect1 = a.getValue().stream().sorted(Comparator.comparing(InventoryDetailSamount::getAmortizedExpenses)).collect(Collectors.toList());
                List<InventoryDetailSamount> list = collect1.subList(0, num);
                list.forEach(v->{
                    v.setTakeFlag(1);
                    v.setTakeId(takeId);
                });
                inventoryDetailSamountRepository.saveAll(list);
        }
    }*/

    /**
     * 获取提货使用商品
     * @param oid
     * @param goodsInfoId
     * @param num
     */
    public List<InventoryDetailSamount> getInventoryDetailSamountFlag(String oid,String goodsInfoId,int num){
        log.info("囤货提货数据========：{}，{},{}", oid,goodsInfoId,num);
        List<InventoryDetailSamount> result = new ArrayList<>();

        List<InventoryDetailSamount> bytidAndGoodsInfoId = inventoryDetailSamountRepository.getBytidAndGoodsInfoId(goodsInfoId, oid,0);
        //分组
        Map<Integer, List<InventoryDetailSamount>> collect = bytidAndGoodsInfoId.stream().collect(Collectors.groupingBy(InventoryDetailSamount::getMoneyType));
        for (Map.Entry<Integer, List<InventoryDetailSamount>> a:collect.entrySet()){
            List<InventoryDetailSamount> collect1 = a.getValue().stream().sorted(Comparator.comparing(InventoryDetailSamount::getAmortizedExpenses)).collect(Collectors.toList());
            List<InventoryDetailSamount> list = collect1.subList(0, num);
            result.addAll(list);
        }
        return result;
    }
    /**
     * 实际扣减
     * @param inventoryDetailSamountList
     */
    public void subInventoryDetailSamountFlag(List<InventoryDetailSamount> inventoryDetailSamountList,String takeId){
        inventoryDetailSamountList.forEach(var->{
            var.setTakeFlag(1);
            var.setTakeId(takeId);
        });
        inventoryDetailSamountRepository.saveAll(inventoryDetailSamountList);
    }



    public void updateInventoryByTakeId(String takeId){
        inventoryDetailSamountRepository.updateInventory(takeId);
    }

    public List<InventoryDetailSamount> getInventoryByTakeId(String takeId){
        return  inventoryDetailSamountRepository.getByTakeId(takeId);
    }

    public List<InventoryDetailSamount> getInventoryByTakeIds(List<String> takeIds){
        return  inventoryDetailSamountRepository.getByTakeIds(takeIds);
    }

    public List<InventoryDetailSamount> getInventoryByOId(String newPileTradeId){
        return  inventoryDetailSamountRepository.getInventoryByOId(newPileTradeId,0);
    }

    public List<InventoryDetailSamount> getInventoryByReturnId(String rid){
        return  inventoryDetailSamountRepository.findAllByReturnId(rid);
    }

    public List<InventoryDetailSamount> getNoTiinventoryDetailSamount(String oid,String goodsInfoId){
      return  inventoryDetailSamountRepository.getBytidAndGoodsInfoId(goodsInfoId, oid,0);
    }
    public List<InventoryDetailSamount> getNoTiinventoryDetailSamounts(String oid,List<String> goodsInfoIds){
        return  inventoryDetailSamountRepository.getBytidAndGoodsInfoIds(goodsInfoIds, oid,0);
    }

    /**
     * 更新囤货退款占用金额
     *
     * @param newPileReturnOrder
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNewPileReturnAmount(NewPileReturnOrder newPileReturnOrder) {
        Assert.notNull(newPileReturnOrder, "returnOrder 不能为null");
        Assert.hasText(newPileReturnOrder.getId(), "退单号不能为空");
        Assert.hasText(newPileReturnOrder.getTid(), "囤货单号不能为空");
        inventoryDetailSamountRepository.updateNewPileReturnRelation(newPileReturnOrder.getTid(),newPileReturnOrder.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNewPickReturnAmount(String returnId, List<InventoryDetailSamountVO> assignedAmountList, int returnFlag) {
        Assert.hasText(returnId, "退单号不能为空");
        Assert.notEmpty(assignedAmountList, "分摊列表不能为空");

        List<Long> ids = assignedAmountList.stream()
                .map(InventoryDetailSamountVO::getInventoryDetailsAmountId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Assert.notEmpty(ids, "分摊列表id不能为空");
        inventoryDetailSamountRepository.updateNewPickReturnRelation(returnId, ids, returnFlag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlockAmountByRid(String rid) {
        Assert.hasText(rid, "退单号不能为空");
        inventoryDetailSamountRepository.unlockAmountByRid(rid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void returnAmountByRid(String rid) {
        Assert.hasText(rid, "退单号不能为空");
        inventoryDetailSamountRepository.returnAmountByRid(rid);
    }

    public List<InventoryDetailSamount> getInventoryByPileId(String tid) {
        return inventoryDetailSamountRepository.findAllByNewPileTradeId(tid);
    }
}
