package com.wanmi.sbc.returnorder.trade.service.newPileTrade;

import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.response.customer.NoDeleteCustomerGetByAccountResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.returnorder.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.returnorder.inventorydetailsamount.repository.InventoryDetailSamountRepository;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.GoodsPickStock;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.GoodsPickStockRepository;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chenchang
 * @since 2023/05/26 9:25
 */
@Service
@Slf4j
public class NewPileTradeDataHandleService {
    @Autowired
    private NewPileTradeService newPileTradeService;
    @Autowired
    private GoodsPickStockRepository goodsPickStockRepository;

    @Autowired
    private InventoryDetailSamountRepository inventoryDetailSamountRepository;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private TradeService tradeService;

    /**
     * 处理南昌仓囤货数据
     */
    public void handleNanChangPileData(String customerAccount) {

        NoDeleteCustomerGetByAccountRequest request = new NoDeleteCustomerGetByAccountRequest();
        request.setCustomerAccount(customerAccount);
        NoDeleteCustomerGetByAccountResponse customer = customerQueryProvider.getNoDeleteCustomerByAccount(request).getContext();

        List<NewPileTrade> newPileTrades = newPileTradeService.getAllDetailByCustomerIdAndWareId(customer.getCustomerId(), 46L);
        if (CollectionUtils.isEmpty(newPileTrades)) {
            log.info("CollectionUtils.isEmpty(newPileTrades) return...");
            return;
        }

        List<String> pileNos = newPileTrades.stream().map(NewPileTrade::getId).collect(Collectors.toList());
        log.info("pileNos.size={}",pileNos.size());

        log.info("1.更新囤货订单上的商品信息");
        Map<String,Map<String,String>> changShaSkuInfoByNanChangSkuIdMap = getChangShaSkuInfoByNanChangSkuIdMap();
        Map<String,Map<String,String>> changShaSkuInfoBySkuIdMap = getChangShaSkuInfoBySkuIdMap();
        //更新囤货订单上的商品信息
        //更新为长沙仓skuId
//        spuId: "2c97d6e287cb971f01880862e7ed0442",
//        spuName: "李子园·280mL瓜子花生矿泉水",
//        skuId: "2c97d6e287cb971f01880862e8350443",
//        devanningId: NumberLong("29551"),
//        skuName: "李子园·280mL瓜子花生矿泉水",
//        skuNo: "8767879545",
//        wareId: 1,
//        parentGoodsInfoId: "7ffffe78738963cc7dad09b1ae82ecce",
        newPileTrades.forEach(newPileTrade -> {
            for (TradeItem tradeItem : newPileTrade.getTradeItems()) {
                Map<String, String> skuInfoMap = changShaSkuInfoByNanChangSkuIdMap.get(tradeItem.getSkuId());
                if (Objects.isNull(skuInfoMap)) {
                    skuInfoMap = changShaSkuInfoBySkuIdMap.get(tradeItem.getSkuId());
                }
                if (Objects.isNull(skuInfoMap)) {
                    continue;
                }
                tradeItem.setSkuId(skuInfoMap.get("skuId"));
                tradeItem.setSkuNo(skuInfoMap.get("skuNo"));
                tradeItem.setSpuId(skuInfoMap.get("spuId"));
                tradeItem.setParentGoodsInfoId(skuInfoMap.get("parentGoodsInfoId"));
                tradeItem.setDevanningId(Long.valueOf(skuInfoMap.get("devanningId")));
                tradeItem.setErpSkuNo(skuInfoMap.get("erpSkuNo"));
            }
            newPileTrade.setWareId(1L);
            newPileTrade.setWareHouseCode("WH01");
        });

        newPileTrades.forEach(trade->{
            newPileTradeService.updateTrade(trade);
        });


        //更新goods_pick_stock为长沙仓sku信息：避免提货数量不足
        log.info("2.更新goods_pick_stock为长沙仓sku信息");
        List<GoodsPickStock> goodsPickStocks = goodsPickStockRepository.findByNewPileTradeNos(pileNos);
        List<GoodsPickStock> goodsPickStocksUpd = new ArrayList<>();
        for (GoodsPickStock goodsPickStock : goodsPickStocks) {
            Map<String, String> skuInfoMap = changShaSkuInfoByNanChangSkuIdMap.get(goodsPickStock.getGoodsInfoId());
            if (Objects.isNull(skuInfoMap)) {
                continue;
            }
            goodsPickStock.setGoodsInfoId(skuInfoMap.get("skuId"));
            goodsPickStock.setGoodsId(skuInfoMap.get("spuId"));
            goodsPickStock.setGoodsInfoNo(skuInfoMap.get("skuNo"));
            goodsPickStock.setWareId(1L);
            goodsPickStocksUpd.add(goodsPickStock);
        }

        if (CollectionUtils.isNotEmpty(goodsPickStocksUpd)) {
            log.info("2.更新goods_pick_stock为长沙仓sku信息 saveAll");
            goodsPickStockRepository.saveAll(goodsPickStocks);
        }

        log.info("3.更新分摊表为长沙仓sku信息");
        //更新分摊表为长沙仓sku信息，避免退货取不到金额
        List<InventoryDetailSamount> inventoryDetailSamounts = inventoryDetailSamountRepository.findAllByNewPileTradeIdIn(pileNos);
        List<InventoryDetailSamount> inventoryDetailSamountsUpd = new ArrayList<>();
        for (InventoryDetailSamount item : inventoryDetailSamounts) {
            Map<String, String> skuInfoMap = changShaSkuInfoByNanChangSkuIdMap.get(item.getGoodsInfoId());
            if (Objects.isNull(skuInfoMap)) {
                continue;
            }
            item.setGoodsInfoId(skuInfoMap.get("skuId"));
            inventoryDetailSamountsUpd.add(item);
        }

        if (CollectionUtils.isNotEmpty(inventoryDetailSamountsUpd)) {
            log.info("3.更新分摊表为长沙仓sku信息 saveAll");
            inventoryDetailSamountRepository.saveAll(inventoryDetailSamounts);
        }

        //更新提货订单中的sku信息
//        log.info("4.更新提货订单中的sku信息");
//        Set<String> takeIds = inventoryDetailSamounts.stream().map(InventoryDetailSamount::getTakeId).collect(Collectors.toSet());
//        handleNanChangTakeData(new ArrayList<>(takeIds));

        log.info("5.转换sku信息结束");
    }


    public void handleNanChangTakeData(List<String> takeIds) {
        log.info("1.updateOpkTradeSkuInfo");
        if (CollectionUtils.isEmpty(takeIds)) {
            log.info("2.校验：takeIds为空，结束");
            return;
        }

        List<Trade> trades = tradeService.getListByIds(takeIds);
        if (CollectionUtils.isEmpty(trades)) {
            log.info("2.校验：trades为空，结束");
        }

        log.info("3.转换sku");
        Map<String,Map<String,String>> changShaSkuInfoByNanChangSkuIdMap = getChangShaSkuInfoByNanChangSkuIdMap();
        Map<String,Map<String,String>> changShaSkuInfoBySkuIdMap = getChangShaSkuInfoBySkuIdMap();

        trades.forEach(trade -> {
            for (TradeItem tradeItem : trade.getTradeItems()) {
                Map<String, String> skuInfoMap = changShaSkuInfoByNanChangSkuIdMap.get(tradeItem.getSkuId());
                if (Objects.isNull(skuInfoMap)) {
                    skuInfoMap = changShaSkuInfoBySkuIdMap.get(tradeItem.getSkuId());
                }
                if (Objects.isNull(skuInfoMap)) {
                    continue;
                }
                tradeItem.setSkuId(skuInfoMap.get("skuId"));
                tradeItem.setSkuNo(skuInfoMap.get("skuNo"));
                tradeItem.setSpuId(skuInfoMap.get("spuId"));
                tradeItem.setParentGoodsInfoId(skuInfoMap.get("parentGoodsInfoId"));
                tradeItem.setDevanningId(Long.valueOf(skuInfoMap.get("devanningId")));
                tradeItem.setErpSkuNo(skuInfoMap.get("erpSkuNo"));
            }
            trade.setWareId(1L);
            trade.setWareHouseCode("WH01");
        });

        log.info("4.更新订单sku");
        trades.forEach(trade -> {
            tradeService.updateTrade(trade);
        });


        log.info("5.更新分摊表为长沙仓sku信息");
        //更新分摊表为长沙仓sku信息，避免退货取不到金额
        List<InventoryDetailSamount> inventoryDetailSamounts = inventoryDetailSamountRepository.findAllByTakeIdIn(takeIds);
        List<InventoryDetailSamount> inventoryDetailSamountsUpd = new ArrayList<>();
        for (InventoryDetailSamount item : inventoryDetailSamounts) {
            Map<String, String> skuInfoMap = changShaSkuInfoByNanChangSkuIdMap.get(item.getGoodsInfoId());
            if (Objects.isNull(skuInfoMap)) {
                continue;
            }
            item.setGoodsInfoId(skuInfoMap.get("skuId"));
            inventoryDetailSamountsUpd.add(item);
        }

        if (CollectionUtils.isNotEmpty(inventoryDetailSamountsUpd)) {
            log.info("3.更新分摊表为长沙仓sku信息 saveAll");
            inventoryDetailSamountRepository.saveAll(inventoryDetailSamounts);
        }
        log.info("6.updateOpkTradeSkuInfo 结束");
    }


    @Autowired
    GoodsInfoQueryProvider goodsInfoQueryProvider;

    private Map<String, Map<String, String>> getChangShaSkuInfoByNanChangSkuIdMap() {
        //南昌 skuId -> erpNo --> 长沙skuInfo --> 长沙spuId --> 长沙spuInfo
        //长沙skuId -> devaningInfo
        return goodsInfoQueryProvider.getChangShaSkuInfoByNanChangSkuIdMap().getContext();
    }

    private Map<String, Map<String, String>> getChangShaSkuInfoBySkuIdMap() {
        return goodsInfoQueryProvider.getChangShaSkuInfoBySkuIdMap().getContext();
    }

}