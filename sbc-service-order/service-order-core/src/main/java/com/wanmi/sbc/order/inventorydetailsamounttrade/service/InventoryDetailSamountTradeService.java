package com.wanmi.sbc.order.inventorydetailsamounttrade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.request.InventoryDetailSamountTrade.InventoryDetailSamountTradeRequest;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountTradeVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import com.wanmi.sbc.order.inventorydetailsamounttrade.repository.InventoryDetailSamountTradeRepository;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryDetailSamountTradeService {

    @Autowired
    private InventoryDetailSamountTradeRepository inventoryDetailSamountTradeRepository;


    @Autowired
    private TradeService tradeService;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;


    /**
     * 插入分摊数据方法
     *
     * @param tradeItems 订单商品集合
     * @param oid        订单id
     */
    public List<InventoryDetailSamountTrade> saveGoodsShareMoney(List<TradeItem> tradeItems, String oid) {
        List<InventoryDetailSamountTrade> byTradeId = inventoryDetailSamountTradeRepository.findByTradeId(oid);
        if (CollectionUtils.isNotEmpty(byTradeId)) {
            return byTradeId;
        }
        //商品数据分摊
        List<InventoryDetailSamountTrade> list = new LinkedList<>();
        tradeItems.forEach(v -> {
            list.addAll(shareItems(v, oid));
        });
        inventoryDetailSamountTradeRepository.saveAll(list);
        return list;
    }

    /**
     * @desc  改价后修改分摊价格
     * @author shiy  2023/8/2 11:04
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsShareMoney(List<TradeItem> tradeItems, String oid){
        inventoryDetailSamountTradeRepository.deleteByTradeId(oid);
        saveGoodsShareMoney(tradeItems,oid);
        log.info("订单[{}]改价后先删除再重建商品分摊现金",oid);
    }

    /**
     * 获取分摊数据
     *
     * @param tradeItem
     * @param oid
     * @return
     */
    public List<InventoryDetailSamountTrade> shareItems(TradeItem tradeItem, String oid) {
        String skuId = tradeItem.getSkuId();
        List<InventoryDetailSamountTrade> list = new LinkedList<>();

        //对老数据进行初始化
        shareItemsIsolation(tradeItem, oid, list);

        //对现金进行价格分摊到商品
        if (tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) >= 0) {
            //现金
            BigDecimal allmoney = tradeItem.getSplitPrice();
            BigDecimal readyallmoney = BigDecimal.ZERO;
            int num = tradeItem.getNum().intValue();
            for (int i = 0; i < num; i++) {
                if (i == num - 1) {
                    list.add(InventoryDetailSamountTrade.builder()
                            .createTime(LocalDateTime.now())
                            .moneyType(1)
                            .amortizedExpenses(allmoney.subtract(readyallmoney))
                            .goodsInfoId(skuId)
                            .tradeId(oid)
                            .returnFlag(0)
                            .build());
                    continue;
                }
                BigDecimal bigDecimal = allmoney.divide(BigDecimal.valueOf(num), 2, BigDecimal.ROUND_DOWN);
                list.add(InventoryDetailSamountTrade.builder()
                        .createTime(LocalDateTime.now())
                        .moneyType(1)
                        .amortizedExpenses(bigDecimal)
                        .goodsInfoId(skuId)
                        .tradeId(oid)
                        .returnFlag(0)
                        .build());
                readyallmoney = readyallmoney.add(bigDecimal);
            }

        }
        //对鲸币进行价格分摊到商品
        if (CollectionUtils.isNotEmpty(tradeItem.getWalletSettlements()) && Objects.nonNull(tradeItem.getWalletSettlements().get(0).getReduceWalletPrice())) {
            //余额
            BigDecimal allmoney = tradeItem.getWalletSettlements().get(0).getReduceWalletPrice();
            BigDecimal readyallmoney = BigDecimal.ZERO;
            int num = tradeItem.getNum().intValue();
            for (int i = 0; i < num; i++) {
                if (i == num - 1) {
                    list.add(InventoryDetailSamountTrade.builder()
                            .createTime(LocalDateTime.now())
                            .moneyType(0)
                            .amortizedExpenses(allmoney.subtract(readyallmoney))
                            .goodsInfoId(skuId)
                            .tradeId(oid)
                            .returnFlag(0)
                            .build());
                    continue;
                }
                BigDecimal bigDecimal = allmoney.divide(BigDecimal.valueOf(num), 2, BigDecimal.ROUND_DOWN);
                list.add(InventoryDetailSamountTrade.builder()
                        .createTime(LocalDateTime.now())
                        .moneyType(0)
                        .amortizedExpenses(bigDecimal)
                        .goodsInfoId(skuId)
                        .tradeId(oid)
                        .returnFlag(0)
                        .build());
                readyallmoney = readyallmoney.add(bigDecimal);
            }
        }
        return list;
    }

    /**
     * 对老数据进行补充
     *
     * @param tradeItem
     * @param oid
     * @return
     */
    public void shareItemsIsolation(TradeItem tradeItem, String oid, List<InventoryDetailSamountTrade> list) {
        List<ReturnOrder> returnOrderList = returnOrderRepository.findByTid(oid);
        if (CollectionUtils.isEmpty(returnOrderList)) {
            return;
        }

        //筛选出受理中的退单列表
        List<ReturnOrder> acceptanceReturnOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                ReturnFlowState.COMPLETED
                && item.getReturnFlowState() != ReturnFlowState.VOID
                && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                .collect(Collectors.toList());
        createInventoryDetailSamountTrade(tradeItem, acceptanceReturnOrders, oid, list, ReturnFlowState.AUDIT);

        //筛选出已完成的退单列表
        List<ReturnOrder> completedReturnOrders = returnOrderList.stream().filter(allOrder -> allOrder
                .getReturnFlowState() == ReturnFlowState.COMPLETED)
                .collect(Collectors.toList());
        createInventoryDetailSamountTrade(tradeItem, completedReturnOrders, oid, list, ReturnFlowState.COMPLETED);
    }

    /**
     * 添加模板数据
     *
     * @param tradeItem
     * @param returnOrders
     * @param oid
     * @param list
     */
    private void createInventoryDetailSamountTrade(TradeItem tradeItem, List<ReturnOrder> returnOrders, String oid, List<InventoryDetailSamountTrade> list, ReturnFlowState returnFlowState) {
        if (CollectionUtils.isEmpty(returnOrders)) {
            return;
        }
        //筛选出受理中的退单商品列表 和已完成的退单商品列表
        List<ReturnItem> returnItems = returnOrders.stream().flatMap(returnOrder -> returnOrder.getReturnItems().stream()).collect(Collectors.toList())
                .stream().filter(returnItem -> StringUtils.equals(returnItem.getSkuId(), tradeItem.getSkuId())).collect(Collectors.toList());

        //获取此商品退货中和已退货的商品数量
        if (CollectionUtils.isEmpty(returnItems)) {
            return;
        }
        //商品单价
        BigDecimal oneSplitPrice = tradeItem.getSplitPrice().divide(BigDecimal.valueOf(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN);
        //商品单价
//        BigDecimal oneReduceWalletPrice = CollectionUtils.isEmpty(tradeItem.getWalletSettlements())
//                ? BigDecimal.ZERO
//                : tradeItem.getWalletSettlements().get(0).getReduceWalletPrice().divide(BigDecimal.valueOf(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN);

        //退单中的商品退货数量
        BigDecimal reduceNum = returnItems.stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);

        //todo: 待明确这里的现金计算会不会有别的问题
        //退单中的商品现金
        BigDecimal reduceSplitPrice = BigDecimal.valueOf(tradeItem.getNum()).compareTo(reduceNum) == 0
                ? tradeItem.getSplitPrice()
                : oneSplitPrice.multiply(reduceNum);

        //退单中的商品鲸币 /已退鲸贴
        BigDecimal reduceReduceWalletPrice = CollectionUtils.isEmpty(tradeItem.getWalletSettlements())
                ? BigDecimal.ZERO
                : returnItems.stream().map(ReturnItem::getBalancePrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        for (ReturnItem returnItem : returnItems) {
            list.add(InventoryDetailSamountTrade.builder()
                    .createTime(LocalDateTime.now())
                    .moneyType(1)
                    .amortizedExpenses(returnItem.getSplitPrice())
                    .goodsInfoId(tradeItem.getSkuId())
                    .tradeId(oid)
                    .returnFlag(returnFlowState == ReturnFlowState.COMPLETED ? 1 : 2)
                    .build());
            if (CollectionUtils.isNotEmpty(tradeItem.getWalletSettlements()) && reduceReduceWalletPrice.compareTo(BigDecimal.ZERO) != 0) {
                list.add(InventoryDetailSamountTrade.builder()
                        .createTime(LocalDateTime.now())
                        .moneyType(0)
                        .amortizedExpenses(returnItem.getBalancePrice())
                        .goodsInfoId(tradeItem.getSkuId())
                        .tradeId(oid)
                        .returnFlag(returnFlowState == ReturnFlowState.COMPLETED ? 1 : 2)
                        .build());
            }
        }
        tradeItem.setNum(tradeItem.getNum() - reduceNum.longValue());
        tradeItem.setSplitPrice(tradeItem.getSplitPrice().subtract(reduceSplitPrice));
        if (reduceReduceWalletPrice.compareTo(BigDecimal.ZERO) != 0) {
            tradeItem.getWalletSettlements().get(0).setReduceWalletPrice(tradeItem.getWalletSettlements().get(0).getReduceWalletPrice().subtract(reduceReduceWalletPrice));
        }
        log.info("====================>>tradeItem: {}", JSONObject.toJSONString(tradeItem));
    }

    /**
     * 修改退货退款状态
     */
    public void updateInventoryDetailSamountReturnFlag(List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS) {
        inventoryDetailSamountTradeRepository.saveAll(KsBeanUtil.convert(inventoryDetailSamountTradeVOS, InventoryDetailSamountTrade.class));
    }

    /**
     * 修改退货退款状态
     *
     * @param oid         订单id
     * @param goodsInfoId 商品id
     * @param num         数量
     */
    public void updateInventoryDetailSamountTradeFlag(String oid, String goodsInfoId, int num, String returnId, int returnFlag) {
        List<InventoryDetailSamountTrade> bytidAndGoodsInfoId = inventoryDetailSamountTradeRepository.getBytidAndGoodsInfoId(goodsInfoId, oid, 0);
        //分组
        Map<Integer, List<InventoryDetailSamountTrade>> collect = bytidAndGoodsInfoId.stream().collect(Collectors.groupingBy(InventoryDetailSamountTrade::getMoneyType));
        for (Map.Entry<Integer, List<InventoryDetailSamountTrade>> a : collect.entrySet()) {
            List<InventoryDetailSamountTrade> collect1 = a.getValue().stream().sorted(Comparator.comparing(InventoryDetailSamountTrade::getAmortizedExpenses)).collect(Collectors.toList());
            List<InventoryDetailSamountTrade> list = collect1.subList(0, num);
            list.forEach(v -> {
                v.setReturnFlag(1);
                v.setReturnId(returnId);
            });
            inventoryDetailSamountTradeRepository.saveAll(list);
        }
    }

    public List<InventoryDetailSamountTrade> findByTradeId(String tradeId) {
        List<InventoryDetailSamountTrade> byTradeId = inventoryDetailSamountTradeRepository.findByTradeId(tradeId);
        if (CollectionUtils.isEmpty(byTradeId)) {
            return saveInitializeGoodsShareMoney(tradeId, null);
        }
        return byTradeId;
    }

    public void updateInventoryByReturnId(String returnId) {
        inventoryDetailSamountTradeRepository.updateInventory(returnId);
    }

    public List<InventoryDetailSamountTrade> getInventoryByReturnId(String returnId) {
        return inventoryDetailSamountTradeRepository.getByReturnId(returnId);
    }

    public List<InventoryDetailSamountTrade> getInventoryByOId(String tradeId) {
        return inventoryDetailSamountTradeRepository.getInventoryByOId(tradeId, 0);
    }

    public List<InventoryDetailSamountTrade> getNoTiinventoryDetailSamount(String oid, String goodsInfoId) {
        return inventoryDetailSamountTradeRepository.getBytidAndGoodsInfoId(goodsInfoId, oid, 0);
    }

    public List<InventoryDetailSamountTrade> getNoTiinventoryDetailSamounts(String oid, List<String> goodsInfoIds) {
        return inventoryDetailSamountTradeRepository.getBytidAndGoodsInfoIds(goodsInfoIds, oid, 0);
    }

    /**
     * 查询时初始化 商品价格数据分摊
     * -- 老数据没有做商品价格数据分摊, 因此需要对老数据做初始化, (兼容老数据)
     *
     * @return
     */
    public List<InventoryDetailSamountTrade> saveInitializeGoodsShareMoney(String tradeId, String returnId) {
        List<InventoryDetailSamountTrade> inventoryDetailSamountTradesTem = new ArrayList<>();
        if (StringUtils.isEmpty(tradeId) && StringUtils.isEmpty(returnId)) {
            return null;
        }
        if (StringUtils.isEmpty(tradeId) && StringUtils.isNotEmpty(returnId)) {
            //1、查找退单信息
            ReturnOrder returnOrder = returnOrderRepository.findById(returnId).orElseThrow(() -> new SbcRuntimeException("K-050003"));
            tradeId = returnOrder.getTid();
        }
        //获取订单信息
        Trade trade = tradeService.detail(tradeId);
        if (trade == null || CollectionUtils.isEmpty(trade.getTradeItems())) {
            throw new SbcRuntimeException("K-050141");
        }

        //初始化商品 (已退单的商品暂时没有初始化的做初始化)
        inventoryDetailSamountTradesTem = this.saveGoodsShareMoney(trade.getTradeItems(), tradeId);
        return inventoryDetailSamountTradesTem;
    }

}
