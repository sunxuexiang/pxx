package com.wanmi.sbc.order.trade.service.newPileTrade;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.FindPickTradeRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPickTradeResponse;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.UnsatisfiedMarketingVO;
import com.wanmi.sbc.order.trade.model.entity.TradeGrouponCommitForm;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPilePickTradeItemSnapshot;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.order.trade.repository.TradeRepository;
import com.wanmi.sbc.order.trade.repository.newPileTrade.NewPilePickTradeItemSnapshotRepository;
import com.wanmi.sbc.order.trade.service.newPileTrade.update.NewPilePickTradeItemSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewPilePickTradeItemService {

    @Resource
    private NewPilePickTradeItemSnapshotRepository newPilePickTradeItemSnapshotRepository;

    @Autowired
    private NewPilePickTradeItemSnapshotService newPilePickTradeItemSnapshotService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Resource
    private TradeRepository tradeRepository;

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public NewPilePickTradeItemSnapshot findTradeItemSnapshot(String customerId) {
        Optional<NewPilePickTradeItemSnapshot> optional = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId);
        if(!optional.isPresent()){
            throw new SbcRuntimeException("K-050201");
        }
        return optional.get();
    }

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public List<TradeItemGroup> find(String customerId) {
        Optional<NewPilePickTradeItemSnapshot> optional = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId);
        return optional.map(NewPilePickTradeItemSnapshot::getItemGroups).orElseThrow(() -> new SbcRuntimeException("K-050201"));
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

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public List<TradeItemGroup> findItemByCustomerId(String customerId) {
        Optional<NewPilePickTradeItemSnapshot> optional = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId);
        return optional.map(NewPilePickTradeItemSnapshot::getItemGroups).orElse(Lists.newArrayList());
    }


    @Resource
    private MongoTccHelper mongoTccHelper;

    @SuppressWarnings("unused")
    public void confirmSnapshot(TradeItemSnapshotRequest request, List<TradeItem> tradeItems, List<TradeMarketingDTO> tradeMarketingList,
                                List<GoodsInfoDTO> skuList) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelSnapshot(TradeItemSnapshotRequest request, List<TradeItem> tradeItems, List<TradeMarketingDTO> tradeMarketingList,
                               List<GoodsInfoDTO> skuList) {
        mongoTccHelper.cancel();
    }

    /**
     * 功能描述: 返回缺货商品
     */
    public UnsatisfiedMarketingVO queryStockNum(String customerId, Long wareId, Long storeId) {
        NewPilePickTradeItemSnapshot tradeItemSnapshot = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId).orElse(null);
        boolean commit=false;
        if (Objects.nonNull(tradeItemSnapshot)) {
            List<TradeItemGroup> itemGroups = tradeItemSnapshot.getItemGroups();

            List<GoodsInfoVO> stockOut = new ArrayList<>(20);
            List<TradeMarketingVO> resultTradeMarketings = new ArrayList<>(10);
            BigDecimal resultMoney = BigDecimal.ZERO;
            List<GoodsInfoVO> gift = new ArrayList<>();

            for (TradeItemGroup inner : itemGroups) {
                if (inner.getSupplier().getStoreId().equals(storeId)) {
                    List<String> collect = inner.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
                    List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider.list(GoodsWareStockListRequest.builder().storeId(inner.getSupplier().getStoreId())
                            .wareId(wareId).goodsInfoIds(collect).build()).getContext().getGoodsWareStockVOList();
                    List<GoodsInfoVO> goodsInfoVOS = new ArrayList<>(20);
                    GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
                    goodsInfoRequest.setGoodsInfoIds(collect);
                    goodsInfoRequest.setIsHavSpecText(Constants.yes);
                    goodsInfoRequest.setWareId(wareId);
                    GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
                    List<GoodsInfoVO> goodsInfos = idsResponse.getGoodsInfos();
                    for (TradeItem tradeItem : inner.getTradeItems()) {
                        Optional<GoodsWareStockVO> first = goodsWareStockVOList.stream()
                                .filter(param -> param.getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
                        if (first.isPresent()) {
                            Optional<GoodsInfoVO> goodsInfoName = goodsInfos.stream()
                                    .filter(param -> param.getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
                            if (first.get().getStock().compareTo(BigDecimal.ZERO) <= 0) {
                                GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                                goodsInfoVO.setStock(first.get().getStock());
                                goodsInfoVO.setGoodsInfoImg(goodsInfoName.map(GoodsInfoVO::getGoodsInfoImg).orElse(null));
                                goodsInfoVO.setGoodsInfoNo(first.get().getGoodsInfoNo());
                                goodsInfoVO.setGoodsInfoId(first.get().getGoodsInfoId());
                                goodsInfoVO.setGoodsInfoName(goodsInfoName.map(GoodsInfoVO::getGoodsInfoName).orElse(null));
                                goodsInfoVOS.add(goodsInfoVO);
                                tradeItem.setNum(first.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                            } else if (first.get().getStock().compareTo(BigDecimal.valueOf(tradeItem.getNum())) < 0) {
                                GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                                goodsInfoVO.setStock(first.get().getStock());
                                goodsInfoVO.setGoodsInfoImg(goodsInfoName.map(GoodsInfoVO::getGoodsInfoImg).orElse(null));
                                goodsInfoVO.setGoodsInfoNo(first.get().getGoodsInfoNo());
                                goodsInfoVO.setGoodsInfoId(first.get().getGoodsInfoId());
                                goodsInfoVO.setGoodsInfoName(goodsInfoName.map(GoodsInfoVO::getGoodsInfoName).orElse(null));
                                goodsInfoVOS.add(goodsInfoVO);
                                tradeItem.setNum(first.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                            }else {
                                commit=true;
                            }
                            goodsInfoName.ifPresent(infoVO -> {
                                Optional<GoodsVO> first1 = idsResponse.getGoodses().stream().filter(param -> param.getGoodsInfoIds().contains(goodsInfoName.get().getGoodsInfoId())).findFirst();
                                merge(tradeItem, infoVO, first1.get(), storeId);
                                calcPrice(tradeItem, infoVO);
                            });
                        }

                    }
                    stockOut.addAll(goodsInfoVOS);
                }
            }
            //针对赠品没有图片的情况重新取spu
            if (CollectionUtils.isNotEmpty(gift)){
                List<String> spu = gift.stream().filter(param->Objects.isNull(param.getGoodsInfoImg())).map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
                List<GoodsVO> goodsVOList = goodsQueryProvider.listByCondition(GoodsByConditionRequest.builder().goodsIds(spu).build()).getContext().getGoodsVOList();
                gift.forEach(param->{
                    Optional<GoodsVO> first = goodsVOList.stream().filter(goodsVO -> goodsVO.getGoodsId().equals(param.getGoodsId())).findFirst();
                    if (first.isPresent()){
                        param.setGoodsInfoImg(first.get().getGoodsImg());
                    }
                });
            }
            UnsatisfiedMarketingVO unsatisfiedMarketingVO = new UnsatisfiedMarketingVO();
            unsatisfiedMarketingVO.setGift(gift);
            unsatisfiedMarketingVO.setResultMoney(resultMoney);
            unsatisfiedMarketingVO.setResultTradeMarketings(resultTradeMarketings);
            unsatisfiedMarketingVO.setStockOut(stockOut);
            boolean haveStockFlag=false;
            for (GoodsInfoVO goodsInfoVO : stockOut) {
                if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO)>0){
                    haveStockFlag=true;
                    break;
                }
            }
            //塞入是否继续提交标志位
            if (commit) {
                unsatisfiedMarketingVO.setCommit(true);
            } else {
                if (haveStockFlag) {
                    unsatisfiedMarketingVO.setCommit(true);
                } else {
                    unsatisfiedMarketingVO.setCommit(false);
                }
            }
            if (resultMoney.equals(BigDecimal.ZERO) && CollectionUtils.isEmpty(gift)) {
                unsatisfiedMarketingVO.setChangeMarketingFlag(false);
            } else {
                unsatisfiedMarketingVO.setChangeMarketingFlag(true);
            }
            return unsatisfiedMarketingVO;
        }
        return new UnsatisfiedMarketingVO();
    }

    @LcnTransaction
    @Transactional
    public void updateUnStock(String customerId,Long wareId,Long storeId) {
        NewPilePickTradeItemSnapshot tradeItemSnapshot = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId).orElse(null);
        if (Objects.nonNull(tradeItemSnapshot)) {
            List<TradeItemGroup> itemGroups = tradeItemSnapshot.getItemGroups();
            for (TradeItemGroup inner : itemGroups) {
                if (inner.getSupplier().getStoreId().equals(storeId)){
                    List<String> collect = inner.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
                    List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider.list(GoodsWareStockListRequest.builder().storeId(inner.getSupplier().getStoreId())
                            .wareId(wareId).goodsInfoIds(collect).build()).getContext().getGoodsWareStockVOList();
                    GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
                    goodsInfoRequest.setGoodsInfoIds(collect);
                    goodsInfoRequest.setIsHavSpecText(Constants.yes);
                    goodsInfoRequest.setWareId(wareId);
                    GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
                    List<GoodsInfoVO> goodsInfos = idsResponse.getGoodsInfos();
                    Iterator<TradeItem> iterator = inner.getTradeItems().iterator();
                    while (iterator.hasNext()) {
                        TradeItem tradeItem = iterator.next();
                        Optional<GoodsInfoVO> goodsInfoName = goodsInfos.stream()
                                .filter(param -> param.getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
                        Optional<GoodsWareStockVO> first = goodsWareStockVOList.stream()
                                .filter(param -> param.getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
                        if (first.isPresent()) {
                            if (first.get().getStock().compareTo(BigDecimal.ZERO)<=0) {
                                iterator.remove();
                            }else if(BigDecimal.valueOf(tradeItem.getNum()).compareTo(first.get().getStock())> 0){
                                tradeItem.setNum(first.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                            }
                        }
                        goodsInfoName.ifPresent(infoVO -> {
                            Optional<GoodsVO> first1 = idsResponse.getGoodses().stream().filter(param -> param.getGoodsInfoIds().contains(goodsInfoName.get().getGoodsInfoId())).findFirst();
                            merge(tradeItem, infoVO, first1.get(), storeId);
                            calcPrice(tradeItem, infoVO);
                        });
                    }
                }
            }
            //更新订单快照
            newPilePickTradeItemSnapshotService.updateTradeItemSnapshot(tradeItemSnapshot);
        }
    }

    /**
     * 保存订单商品快照
     *
     * @param request            客户id、是否开店礼包
     * @param tradeItems         商品快照，只包含skuId与购买数量
     * @param tradeMarketingList
     * @param skuList            快照商品详细信息，包含所属商家，店铺等信息
     */
    @LcnTransaction
    @TccTransaction
    public void snapshot(TradeItemSnapshotRequest request, List<TradeItem> tradeItems, List<TradeMarketingDTO> tradeMarketingList,
                         List<GoodsInfoDTO> skuList) {
        String customerId = request.getCustomerId();
        //商品按店铺分组
        Map<Long, Map<String, List<GoodsInfoDTO>>> skuMap = skuList.stream().collect(
                Collectors.groupingBy(GoodsInfoDTO::getStoreId,
                        Collectors.groupingBy(GoodsInfoDTO::getGoodsInfoId)));
        List<TradeItemGroup> itemGroups = new ArrayList<>();
        skuMap.forEach((key, value) -> {
            //获取商品所属商家，店铺信息
            StoreVO store = storeQueryProvider.getNoDeleteStoreById(NoDeleteStoreByIdRequest.builder().storeId(key)
                    .build())
                    .getContext().getStoreVO();
            BigDecimal shareRatio = null;
            if (Objects.nonNull(store.getShareRatio())) {
                shareRatio = store.getShareRatio().divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
            }
            Supplier supplier = Supplier.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .isSelf(store.getCompanyType() == CompanyType.PLATFORM)
                    .supplierCode(store.getCompanyInfo().getCompanyCode())
                    .supplierId(store.getCompanyInfo().getCompanyInfoId())
                    .supplierName(store.getCompanyInfo().getSupplierName())
                    .freightTemplateType(store.getFreightTemplateType())
                    .companyType(store.getCompanyType())
                    .ccbMerchantNumber(store.getConstructionBankMerchantNumber())
                    .shareRatio(shareRatio)
                    .clearingDay(store.getSettlementCycle())
                    .build();
            //分组后的商品快照
            List<TradeItem> items = tradeItems.stream().filter(i -> value.containsKey(i.getSkuId()))
                    .collect(Collectors.toList());

            //分组后的营销快照
            List<String> ids = items.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
            //增加一个订单满赠的SkuId
            ids.add(Constant.FULL_GIT_ORDER_GOODS);
            List<TradeMarketingDTO> marketings = tradeMarketingList.stream().filter(i -> i.getSkuIds().stream()
                    .anyMatch(ids::contains)).collect(Collectors.toList());

            TradeGrouponCommitForm grouponForm = null;
            if (Objects.nonNull(request.getOpenGroupon())) {
                // 当为拼团单时，设置拼团form
                grouponForm = new TradeGrouponCommitForm();
                grouponForm.setOpenGroupon(request.getOpenGroupon());
                grouponForm.setGrouponNo(request.getGrouponNo());
            }

            if (StringUtils.isNotBlank(request.getSnapshotType())) {
                itemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(), request
                        .getSnapshotType(), null, null,null, SaleType.WHOLESALE));
            } else {
                itemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(), null, null, null,null,SaleType.WHOLESALE));
            }
        });
        //快照生成采用幂等操作
        NewPilePickTradeItemSnapshot tradeItemSnapshot = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId).orElse(null);
        if(Objects.nonNull(tradeItemSnapshot)){
            newPilePickTradeItemSnapshotService.deleteTradeItemSnapshot(tradeItemSnapshot.getId());
        }
        //生成快照
        NewPilePickTradeItemSnapshot snapshot = NewPilePickTradeItemSnapshot.builder()
                .id(UUIDUtil.getUUID())
                .buyerId(customerId)
                .itemGroups(itemGroups)
                .suitBuyCount(request.getSuitBuyCount())
                .snapshotType(request.getSnapshotType())
                .build();
        newPilePickTradeItemSnapshotService.addTradeItemSnapshot(snapshot);
    }

    /**
     * 保存订单商品快照
     *
     * @param request            客户id、是否开店礼包
     * @param tradeItems         商品快照，只包含skuId与购买数量
     * @param tradeMarketingList
     * @param skuList            快照商品详细信息，包含所属商家，店铺等信息
     */
    @LcnTransaction
    @TccTransaction
    public void snapshotRetail(TradeItemSnapshotRequest request, List<TradeItem> tradeItems, List<TradeMarketingDTO> tradeMarketingList,
                               List<GoodsInfoDTO> skuList) {
        String customerId = request.getCustomerId();
        //商品按店铺分组(批发)
        Map<Long, Map<String, List<GoodsInfoDTO>>> skuMap = skuList.stream().collect(
                Collectors.groupingBy(GoodsInfoDTO::getStoreId,
                        Collectors.groupingBy(GoodsInfoDTO::getGoodsInfoId)));
        List<TradeItemGroup> itemGroups = new ArrayList<>();
        skuMap.forEach((key, value) -> {
            //获取商品所属商家，店铺信息
            StoreVO store = storeQueryProvider.getNoDeleteStoreById(NoDeleteStoreByIdRequest.builder().storeId(key)
                    .build())
                    .getContext().getStoreVO();
            BigDecimal shareRatio = null;
            if (Objects.nonNull(store.getShareRatio())) {
                shareRatio = store.getShareRatio().divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
            }
            Supplier supplier = Supplier.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .isSelf(store.getCompanyType() == CompanyType.PLATFORM)
                    .supplierCode(store.getCompanyInfo().getCompanyCode())
                    .supplierId(store.getCompanyInfo().getCompanyInfoId())
                    .supplierName(store.getCompanyInfo().getSupplierName())
                    .freightTemplateType(store.getFreightTemplateType())
                    .companyType(store.getCompanyType())
                    .ccbMerchantNumber(store.getConstructionBankMerchantNumber())
                    .shareRatio(shareRatio)
                    .clearingDay(store.getSettlementCycle())
                    .build();
            //分组后的商品快照
            List<TradeItem> items = tradeItems.stream().filter(i -> value.containsKey(i.getSkuId()))
                    .collect(Collectors.toList());

            //分组后的营销快照
            List<String> ids = items.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
            //增加一个订单满赠的SkuId
            ids.add(Constant.FULL_GIT_ORDER_GOODS);

            itemGroups.add(new TradeItemGroup(items, supplier, null, request.getStoreBagsFlag(),
                    null, null, null,null,SaleType.WHOLESALE));
        });
        log.info("=================itemGroups信息"+itemGroups);

        //快照生成采用幂等操作
        NewPilePickTradeItemSnapshot tradeItemSnapshot = newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId).orElse(null);
        if(Objects.nonNull(tradeItemSnapshot)){
            newPilePickTradeItemSnapshotService.deleteTradeItemSnapshot(tradeItemSnapshot.getId());
        }
        Long wareId = 1L;
        Optional<TradeItemGroup> any = itemGroups.stream().findAny();
        if (any.isPresent()){
            wareId=any.get().getWareId();
        }
        //生成快照
        NewPilePickTradeItemSnapshot snapshot = NewPilePickTradeItemSnapshot.builder()
                .wareId(wareId)
                .id(UUIDUtil.getUUID())
                .buyerId(customerId)
                .itemGroups(itemGroups)
                .suitBuyCount(request.getSuitBuyCount())
                .snapshotType(request.getSnapshotType())
                .build();
        newPilePickTradeItemSnapshotService.addTradeItemSnapshot(snapshot);
    }

    /**
     * 删除订单商品快照
     *
     * @param customerId 用户id
     */
    public void remove(String customerId) {
        newPilePickTradeItemSnapshotRepository.deleteByBuyerId(customerId);
    }

    public NewPilePickTradeItemSnapshot findByCustomerId(String customerId) {
        return newPilePickTradeItemSnapshotRepository.findByBuyerId(customerId).orElse(null);
    }

    public void updateTradeItemSnapshotNoRollback(NewPilePickTradeItemSnapshot tradeItemSnapshot){
        newPilePickTradeItemSnapshotService.updateTradeItemSnapshotNoRollback(tradeItemSnapshot);
    }

    /**
     *
     * @param tradeItem
     * @param goodsInfo
     */
    private void calcPrice(TradeItem tradeItem, GoodsInfoVO goodsInfo) {
        tradeItem.setLevelPrice(goodsInfo.getSalePrice());
        tradeItem.setOriginalPrice(goodsInfo.getMarketPrice());
        //判断是否为秒杀抢购商品
        if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
            tradeItem.setPrice(goodsInfo.getSalePrice());
            tradeItem.setSplitPrice(tradeItem.getLevelPrice().multiply(
                    new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP)
            );
        } else {
            tradeItem.setSplitPrice(tradeItem.getPrice().multiply(
                    new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    /**
     *
     * @param tradeItem
     * @param goodsInfo
     * @param goods
     */
    private void merge(TradeItem tradeItem, GoodsInfoVO goodsInfo, GoodsVO goods, Long storeId) {
        tradeItem.setSkuName(goodsInfo.getGoodsInfoName());
        tradeItem.setSpuName(goods.getGoodsName());
        tradeItem.setPic(goodsInfo.getGoodsInfoImg());
        tradeItem.setBrand(goods.getBrandId());
        tradeItem.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
        tradeItem.setCateId(goods.getCateId());
        tradeItem.setSkuNo(goodsInfo.getGoodsInfoNo());
        tradeItem.setSpuId(goods.getGoodsId());
        tradeItem.setUnit(goods.getGoodsUnit());
        tradeItem.setGoodsWeight(goods.getGoodsWeight());
        tradeItem.setGoodsCubage(goods.getGoodsCubage());
        tradeItem.setFreightTempId(goods.getFreightTempId());
        tradeItem.setStoreId(storeId);
        tradeItem.setDistributionGoodsAudit(goodsInfo.getDistributionGoodsAudit());
        tradeItem.setCommissionRate(goodsInfo.getCommissionRate());
        tradeItem.setDistributionCommission(goodsInfo.getDistributionCommission());
        tradeItem.setOriginalPrice(BigDecimal.ZERO);
        tradeItem.setLevelPrice(BigDecimal.ZERO);
        tradeItem.setSplitPrice(BigDecimal.ZERO);
        tradeItem.setEnterPriseAuditState(goodsInfo.getEnterPriseAuditState());
        tradeItem.setVipPrice(goodsInfo.getVipPrice());//订单的vipPrice
        tradeItem.setGoodsInfoType(goodsInfo.getGoodsInfoType());
        tradeItem.setSpecialPrice(goodsInfo.getSpecialPrice());
        tradeItem.setGoodsBatchNo(goodsInfo.getGoodsInfoBatchNo());
        tradeItem.setErpSkuNo(goodsInfo.getErpGoodsInfoNo());
        tradeItem.setAddStep(goodsInfo.getAddStep());
        tradeItem.setGoodsSubtitle(goods.getGoodsSubtitle());
        if (StringUtils.isBlank(tradeItem.getSpecDetails())) {
            tradeItem.setSpecDetails(goodsInfo.getSpecText());
        }
    }

    public FindPickTradeResponse findPickTrade(FindPickTradeRequest request) {
//        List<PickOrderItemDO> daoItems = tradeRepository.findPickOrder(request.getValue());
//TODO PILE
        List<Trade> daoItems = tradeRepository.findListByActivityTypeAndPileOrderNo(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType(), request.getPileTradeNo());

        List<FindPickTradeResponse.PickOrderItem> items = daoItems.stream().map(daoItem -> {
            FindPickTradeResponse.PickOrderItem item = new FindPickTradeResponse.PickOrderItem();
            item.setPickOrderNo(daoItem.getId());
            //TODO PICK TIME
            item.setPickTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            item.setDeliverWayText(Objects.nonNull(daoItem.getDeliverWay()) ? daoItem.getDeliverWay().getDesc() : "");
            item.setNum(daoItem.getGoodsTotalNum());

            TradePrice tradePrice = daoItem.getTradePrice();
            if (Objects.nonNull(tradePrice)) {
                item.setOrderGoodsPrice(tradePrice.getGoodsPrice());
                item.setFreight(tradePrice.getDeliveryPrice());
                item.setDeliveryDiscountPrice(tradePrice.getDiscountsPrice());
                //TODO 扣款金额
                item.setPayPrice(tradePrice.getTotalPrice());
                item.setActualPrice(tradePrice.getTotalPrice());
            }
            return item;
        }).collect(Collectors.toList());

        FindPickTradeResponse response = new FindPickTradeResponse();
        response.setPickOrderItems(items);
        return response;
    }
}
