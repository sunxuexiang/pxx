package com.wanmi.sbc.order.trade.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingTradePluginProvider;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingTradeBatchTryCatchWrapperResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.vo.UnsatisfiedMarketingVO;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.trade.model.entity.TradeGrouponCommitForm;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.order.trade.model.root.TradeItemSnapshot;
import com.wanmi.sbc.order.trade.repository.TradeItemSnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>订单商品操作Service</p>
 * Created by of628-wenzhi on 2017-07-13-上午10:48.
 */
@Service
@Slf4j
public class TradeItemService {

    @Resource
    private TradeItemSnapshotRepository repository;

    @Autowired
    private TradeItemSnapshotService tradeItemSnapshotService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private MarketingTradePluginProvider marketingTradePluginProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private OrderCommonService orderCommonService;

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public TradeItemSnapshot findTradeItemSnapshot(String customerId) {
        Optional<TradeItemSnapshot> optional = repository.findByBuyerId(customerId);
        if (!optional.isPresent()) {
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
        Optional<TradeItemSnapshot> optional = repository.findByBuyerId(customerId);
        return optional.map(TradeItemSnapshot::getItemGroups).orElseThrow(() -> new SbcRuntimeException("K-050201"));
    }

    /**
     * 获取用户已确认订单的商品快照
     *
     * @param customerId 客户id
     * @return 按照商家、店铺分组后的商品快照，只包含skuId与购买数量
     */
    public List<TradeItemGroup> findAll(String customerId) {
        Optional<TradeItemSnapshot> optional = repository.findByBuyerId(customerId);
        List<TradeItemGroup> groupList = new ArrayList<>();
        if(optional.isPresent()){
            if (CollectionUtils.isNotEmpty(optional.get().getItemGroups())) {
                groupList.addAll(optional.get().getItemGroups());
            }
            if (CollectionUtils.isNotEmpty(optional.get().getRetailItemGroups())) {
                groupList.addAll(optional.get().getRetailItemGroups());
            }
            if (CollectionUtils.isNotEmpty(optional.get().getBulkItemGroups())){
                groupList.addAll(optional.get().getBulkItemGroups());
            }
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
        List<TradeItemGroup> re = new LinkedList<>();
        Optional<TradeItemSnapshot> optional = repository.findByBuyerId(customerId);
        if (optional.isPresent()){
            TradeItemSnapshot tradeItemSnapshot = optional.get();
            if (CollectionUtils.isNotEmpty(tradeItemSnapshot.getItemGroups())){
                re.addAll(tradeItemSnapshot.getItemGroups());
            }
            if (CollectionUtils.isNotEmpty(tradeItemSnapshot.getBulkItemGroups())){
                re.addAll(tradeItemSnapshot.getBulkItemGroups());
            }


        }else {
            throw new SbcRuntimeException("K-260001");
        }

        return re;
//
//        return optional.map(TradeItemSnapshot::getItemGroups).orElse(Lists.newArrayList());
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
        TradeItemSnapshot tradeItemSnapshot = repository.findByBuyerId(customerId).orElse(null);
        boolean commit = false;
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
                                tradeItem.setNum(first.get().getStock().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                            } else if (first.get().getStock().compareTo(BigDecimal.valueOf(tradeItem.getNum())) < 0) {
                                GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                                goodsInfoVO.setStock(first.get().getStock());
                                goodsInfoVO.setGoodsInfoImg(goodsInfoName.map(GoodsInfoVO::getGoodsInfoImg).orElse(null));
                                goodsInfoVO.setGoodsInfoNo(first.get().getGoodsInfoNo());
                                goodsInfoVO.setGoodsInfoId(first.get().getGoodsInfoId());
                                goodsInfoVO.setGoodsInfoName(goodsInfoName.map(GoodsInfoVO::getGoodsInfoName).orElse(null));
                                goodsInfoVOS.add(goodsInfoVO);
                                tradeItem.setNum(first.get().getStock().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                            } else {
                                commit = true;
                            }
                            goodsInfoName.ifPresent(infoVO -> {
                                Optional<GoodsVO> first1 = idsResponse.getGoodses().stream().filter(param -> param.getGoodsInfoIds().contains(goodsInfoName.get().getGoodsInfoId())).findFirst();
                                merge(tradeItem, infoVO, first1.get(), storeId);
                                calcPrice(tradeItem, infoVO);
                            });
                        }

                    }
                    TradeMarketingWrapperTryCatchVO inFactMarketing = wrapperMarketingForConfirm(inner.getTradeItems(), inner.getTradeMarketingList());
                    stockOut.addAll(goodsInfoVOS);
                    //计算失效营销和数量
                    //1 失效营销
                    if (CollectionUtils.isNotEmpty(inner.getTradeMarketings())) {
                        if (CollectionUtils.isNotEmpty(inFactMarketing.getMarketingIds())) {
                            List<TradeMarketingVO> unsatisfiedMarketing = inner.getTradeMarketings().stream().filter(param -> inFactMarketing.getMarketingIds().contains(param.getMarketingId())).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(unsatisfiedMarketing)) {
                                BigDecimal discountsMoney = unsatisfiedMarketing.stream().filter(param -> Objects.nonNull(param.getDiscountsAmount()))
                                        .map(TradeMarketingVO::getDiscountsAmount).reduce(BigDecimal::add).get();
                                resultTradeMarketings.addAll(unsatisfiedMarketing);
                                resultMoney = resultMoney.add(discountsMoney);
                                List<String> gifskus = new ArrayList<>(10);
                                unsatisfiedMarketing.forEach(unsatisfied -> {
                                    if (CollectionUtils.isNotEmpty(unsatisfied.getGiftIds())) {
                                        gifskus.addAll(unsatisfied.getGiftIds());
                                    }
                                });
                                //组装失效赠品
                                if (CollectionUtils.isNotEmpty(gifskus)) {
                                    List<GoodsInfoVO> giftGoodsInfos = goodsInfoQueryProvider
                                            .listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(gifskus).build()).getContext().getGoodsInfos();
                                    List<TradeMarketingVO> giftMarketing = unsatisfiedMarketing.stream()
                                            .filter(param -> Objects.nonNull(param.getGiftLevel())).collect(Collectors.toList());
                                    List<MarketingFullGiftDetailVO> giftNum = new ArrayList<>(10);
                                    if (CollectionUtils.isNotEmpty(giftMarketing)) {
                                        giftMarketing.forEach(param -> {
                                            giftNum.addAll(param.getGiftLevel().getFullGiftDetailList());
                                        });
                                        if (CollectionUtils.isNotEmpty(giftNum)) {
                                            giftGoodsInfos.forEach(goodsInfoVO -> {
                                                Optional<MarketingFullGiftDetailVO> first = giftNum.stream().filter(param -> param
                                                        .getProductId().equals(goodsInfoVO.getGoodsInfoId())).findFirst();
                                                first.ifPresent(marketingFullGiftDetailVO -> goodsInfoVO.setBuyCount(marketingFullGiftDetailVO.getProductNum()));
                                            });
                                        }
                                    }
                                    gift.addAll(giftGoodsInfos);
                                }
                            }
                        }
                        // 2  降级营销
                        if (CollectionUtils.isNotEmpty(inFactMarketing.getTradeMarketingWrapperVOS())) {
                            List<TradeMarketingVO> marketingVOS = new ArrayList<>(10);
                            List<TradeMarketingWrapperVO> tradeMarketingWrapperVOS = inFactMarketing.getTradeMarketingWrapperVOS();
                            tradeMarketingWrapperVOS.forEach(param -> {
                                marketingVOS.add(param.getTradeMarketing());
                            });
                            Set<String> selectGiftSkuIds = new HashSet<>(20);//订单选中的赠品
                            //满赠营销信息
                            List<MarketingFullGiftLevelVO> giftLevelVOS = new ArrayList<>(10);
                            for (TradeMarketingVO param : marketingVOS) {
                                Optional<TradeMarketingVO> first = inner.getTradeMarketings().stream().filter(marketingVO -> marketingVO
                                        .getMarketingId().equals(param.getMarketingId())).findFirst();
                                if (first.isPresent()) {
                                    if (first.get().getDiscountsAmount().compareTo(param.getDiscountsAmount()) != 0) {
                                        resultMoney = resultMoney.add(first.get().getDiscountsAmount().subtract(param.getDiscountsAmount()));
                                    }
                                    if (Objects.nonNull(first.get().getGiftLevel())) {
                                        giftLevelVOS.add(param.getGiftLevel());
                                        selectGiftSkuIds.addAll(first.get().getGiftIds());
                                    }
                                }
                            }
                            List<String> skuGiftids = new ArrayList<>(10); //降级的满赠营销gift的skuId
                            Map<String, Long> skuNum = new HashMap<>(10); //降级的gift变化
                            if (CollectionUtils.isNotEmpty(giftLevelVOS)) {
                                giftLevelVOS.forEach(param -> {
                                    Optional<TradeMarketingVO> first = inner.getTradeMarketings().stream().filter(marketingVO ->
                                            Objects.nonNull(marketingVO.getGiftLevel()) && marketingVO.getGiftLevel().getMarketingId()
                                                    .equals(param.getMarketingId())).findFirst();
                                    first.ifPresent(marketingVO -> {//比较降级gift塞入降级的信息和数量
                                        List<MarketingFullGiftDetailVO> fullGiftDetailList = marketingVO.getGiftLevel().getFullGiftDetailList();
                                        List<MarketingFullGiftDetailVO> unsatisfiedGift = param.getFullGiftDetailList();
                                        unsatisfiedGift.forEach(unsatisfiedGiftInner -> {
                                            Optional<MarketingFullGiftDetailVO> notEqueal = fullGiftDetailList.stream().filter(fullGiftDetailVO ->
                                                    !fullGiftDetailVO.getProductNum().equals(unsatisfiedGiftInner.getProductNum())
                                                            && unsatisfiedGiftInner.getProductId().equals(fullGiftDetailVO.getProductId())).findFirst();
                                            if (notEqueal.isPresent() && selectGiftSkuIds.contains(notEqueal.get().getProductId())) {
                                                skuNum.put(notEqueal.get().getProductId(), notEqueal.get().getProductNum() - unsatisfiedGiftInner.getProductNum());
                                                skuGiftids.add(notEqueal.get().getProductId());
                                            }
                                        });
                                    });
                                });
                            }
                            //组装降级gift
                            if (CollectionUtils.isNotEmpty(skuGiftids)) {
                                List<GoodsInfoVO> giftGoodsInfos = goodsInfoQueryProvider
                                        .listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuGiftids).build()).getContext().getGoodsInfos();
                                giftGoodsInfos.forEach(goodsInfoVO -> {
                                    if (Objects.nonNull(skuNum) && Objects.nonNull(skuNum.get(goodsInfoVO.getGoodsInfoId()))) {
                                        goodsInfoVO.setBuyCount(skuNum.get(goodsInfoVO.getGoodsInfoId()));
                                    } else {
                                        goodsInfoVO.setBuyCount(1L);
                                    }
                                });

                                gift.addAll(giftGoodsInfos);
                            }
                        }
                    }
                }
            }
            //针对赠品没有图片的情况重新取spu
            if (CollectionUtils.isNotEmpty(gift)) {
                List<String> spu = gift.stream().filter(param -> Objects.isNull(param.getGoodsInfoImg())).map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
                List<GoodsVO> goodsVOList = goodsQueryProvider.listByCondition(GoodsByConditionRequest.builder().goodsIds(spu).build()).getContext().getGoodsVOList();
                gift.forEach(param -> {
                    Optional<GoodsVO> first = goodsVOList.stream().filter(goodsVO -> goodsVO.getGoodsId().equals(param.getGoodsId())).findFirst();
                    if (first.isPresent()) {
                        param.setGoodsInfoImg(first.get().getGoodsImg());
                    }
                });
            }
            UnsatisfiedMarketingVO unsatisfiedMarketingVO = new UnsatisfiedMarketingVO();
            unsatisfiedMarketingVO.setGift(gift);
            unsatisfiedMarketingVO.setResultMoney(resultMoney);
            unsatisfiedMarketingVO.setResultTradeMarketings(resultTradeMarketings);
            unsatisfiedMarketingVO.setStockOut(stockOut);
            boolean haveStockFlag = false;
            for (GoodsInfoVO goodsInfoVO : stockOut) {
                if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    haveStockFlag = true;
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
    public void updateUnStock(String customerId, Long wareId, Long storeId) {
        TradeItemSnapshot tradeItemSnapshot = repository.findByBuyerId(customerId).orElse(null);
        if (Objects.nonNull(tradeItemSnapshot)) {
            List<TradeItemGroup> itemGroups = tradeItemSnapshot.getItemGroups();
            for (TradeItemGroup inner : itemGroups) {
                if (inner.getSupplier().getStoreId().equals(storeId)) {
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
                            if (first.get().getStock().compareTo(BigDecimal.ZERO) <= 0) {
                                iterator.remove();
                            } else if (BigDecimal.valueOf(tradeItem.getNum()).compareTo(first.get().getStock()) > 0) {
                                tradeItem.setNum(first.get().getStock().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                            }
                        }
                        goodsInfoName.ifPresent(infoVO -> {
                            Optional<GoodsVO> first1 = idsResponse.getGoodses().stream().filter(param -> param.getGoodsInfoIds().contains(goodsInfoName.get().getGoodsInfoId())).findFirst();
                            merge(tradeItem, infoVO, first1.get(), storeId);
                            calcPrice(tradeItem, infoVO);
                        });
                    }
                    TradeMarketingWrapperTryCatchVO marketing = wrapperMarketingForConfirm(inner.getTradeItems(), inner.getTradeMarketingList());
                    if (CollectionUtils.isNotEmpty(marketing.getMarketingIds())) {
                        inner.getTradeMarketingList().removeIf(next -> marketing.getMarketingIds().contains(next.getMarketingId()));
                    }
                }
            }
            //更新订单快照
            tradeItemSnapshotService.updateTradeItemSnapshot(tradeItemSnapshot);
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
                    .isSelf(orderCommonService.isSelf(store.getCompanyType()))
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
                        .getSnapshotType(), null, null, null, SaleType.WHOLESALE));
            } else {
                itemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(), null, null, null, null, SaleType.WHOLESALE));
            }
        });
        //快照生成采用幂等操作
        TradeItemSnapshot tradeItemSnapshot = repository.findByBuyerId(customerId).orElse(null);
        if (Objects.nonNull(tradeItemSnapshot)) {
            tradeItemSnapshotService.deleteTradeItemSnapshot(tradeItemSnapshot.getId());
        }
        //生成快照
        TradeItemSnapshot snapshot = TradeItemSnapshot.builder()
                .id(UUIDUtil.getUUID())
                .buyerId(customerId)
                .itemGroups(itemGroups)
                .suitBuyCount(request.getSuitBuyCount())
                .snapshotType(request.getSnapshotType())
                .marketingId(request.getMarketingId())
                .build();
        tradeItemSnapshotService.addTradeItemSnapshot(snapshot);
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
        HashMap<Long,StoreVO> storeSimpleResponseHashMap = new HashMap<>(100);
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
                    .isSelf(orderCommonService.isSelf(store.getCompanyType()))
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

//            TradeGrouponCommitForm grouponForm = null;
//            if (Objects.nonNull(request.getOpenGroupon())) {
//                // 当为拼团单时，设置拼团form
//                grouponForm = new TradeGrouponCommitForm();
//                grouponForm.setOpenGroupon(request.getOpenGroupon());
//                grouponForm.setGrouponNo(request.getGrouponNo());
//            }

            if (StringUtils.isNotBlank(request.getSnapshotType())) {
                itemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(), request
                        .getSnapshotType(), null, null, null, SaleType.WHOLESALE));
            } else {
                itemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(),
                        null, null, null, null, SaleType.WHOLESALE));
            }
        });
        log.info("=================itemGroups信息" + itemGroups);
        //商品按店铺分组(零售)
        Map<Long, Map<String, List<GoodsInfoDTO>>> retailSkuMap = request.getRetailSkuList().stream().collect(
                Collectors.groupingBy(GoodsInfoDTO::getStoreId,
                        Collectors.groupingBy(GoodsInfoDTO::getGoodsInfoId)));
        List<TradeItemGroup> retailItemGroups = new ArrayList<>();
        retailSkuMap.forEach((key, value) -> {
            //获取商品所属商家，店铺信息
            StoreVO store = storeSimpleResponseHashMap.get(key);
            BigDecimal shareRatio = null;
            if (Objects.nonNull(store.getShareRatio())) {
                shareRatio = store.getShareRatio().divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
            }
            Supplier supplier = Supplier.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .isSelf(orderCommonService.isSelf(store.getCompanyType()))
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
            List<TradeItem> items = KsBeanUtil.convert(request.getRetailTradeItems(), TradeItem.class).stream()
                    .filter(i -> value.containsKey(i.getSkuId())).collect(Collectors.toList());

            //分组后的营销快照
            List<String> ids = items.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
            //增加一个订单满赠的SkuId
            ids.add(Constant.FULL_GIT_ORDER_GOODS);
            List<TradeMarketingDTO> marketings = request.getRetailtTradeMarketingList().stream().filter(i -> i.getSkuIds().stream()
                    .anyMatch(ids::contains)).collect(Collectors.toList());

//            TradeGrouponCommitForm grouponForm = null;
//            if (Objects.nonNull(request.getOpenGroupon())) {
//                // 当为拼团单时，设置拼团form
//                grouponForm = new TradeGrouponCommitForm();
//                grouponForm.setOpenGroupon(request.getOpenGroupon());
//                grouponForm.setGrouponNo(request.getGrouponNo());
//            }

            if (StringUtils.isNotBlank(request.getSnapshotType())) {
                retailItemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(), request
                        .getSnapshotType(), null, null, null, SaleType.RETAIL));
            } else {
                retailItemGroups.add(new TradeItemGroup(items, supplier, marketings, request.getStoreBagsFlag(),
                        null, null, null, null, SaleType.RETAIL));
            }
        });
        //商品按店铺分组(散批)
        Map<Long, Map<String, List<GoodsInfoDTO>>> bulkSkuMap = request.getBulkSkuList().stream().collect(
                Collectors.groupingBy(GoodsInfoDTO::getStoreId,
                        Collectors.groupingBy(GoodsInfoDTO::getGoodsInfoId)));
        List<TradeItemGroup> bulkItemGroups = new ArrayList<>();
        bulkSkuMap.forEach((key, value) -> {
            //获取商品所属商家，店铺信息
            StoreVO store = storeSimpleResponseHashMap.get(key);
            BigDecimal shareRatio = null;
            if (Objects.nonNull(store.getShareRatio())) {
                shareRatio = store.getShareRatio().divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
            }
            Supplier supplier = Supplier.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .isSelf(orderCommonService.isSelf(store.getCompanyType()))
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
            List<TradeItem> items = KsBeanUtil.convert(request.getBulkTradeItems(), TradeItem.class).stream()
                    .filter(i -> value.containsKey(i.getSkuId())).collect(Collectors.toList());


            if (StringUtils.isNotBlank(request.getSnapshotType())) {
                bulkItemGroups.add(new TradeItemGroup(items, supplier, null, request.getStoreBagsFlag(), request
                        .getSnapshotType(), null, null, null, SaleType.BULK));
            } else {
                bulkItemGroups.add(new TradeItemGroup(items, supplier, null, request.getStoreBagsFlag(),
                        null, null, null, null, SaleType.BULK));
            }
        });


        //快照生成采用幂等操作
        TradeItemSnapshot tradeItemSnapshot = repository.findByBuyerId(customerId).orElse(null);
        if (Objects.nonNull(tradeItemSnapshot)) {
            tradeItemSnapshotService.deleteTradeItemSnapshot(tradeItemSnapshot.getId());
        }
        Long wareId = 1L;
        Optional<TradeItemGroup> any = itemGroups.stream().findAny();
        if (any.isPresent()) {
            wareId = any.get().getWareId();
        }
        //生成快照
        TradeItemSnapshot snapshot = TradeItemSnapshot.builder()
                .wareId(wareId)
                .id(UUIDUtil.getUUID())
                .buyerId(customerId)
                .itemGroups(itemGroups)
                .retailItemGroups(retailItemGroups)
                .bulkItemGroups(bulkItemGroups)
                .suitBuyCount(request.getSuitBuyCount())
                .snapshotType(request.getSnapshotType())
                .marketingId(request.getMarketingId())
                .build();
        tradeItemSnapshotService.addTradeItemSnapshot(snapshot);
    }

    /**
     * 保存提货订单商品快照
     *
     * @param request            客户id、是否开店礼包
     * @param tradeItems         商品快照，只包含skuId与购买数量
     * @param tradeMarketingList
     * @param skuList            快照商品详细信息，包含所属商家，店铺等信息
     */
    @LcnTransaction
    @TccTransaction
    public void takeSnapshot(TradeItemSnapshotRequest request, List<TradeItem> tradeItems, List<TradeMarketingDTO> tradeMarketingList,
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
                    .isSelf(orderCommonService.isSelf(store.getCompanyType()))
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
//            List<String> ids = items.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
//            //增加一个订单满赠的SkuId
//            ids.add(Constant.FULL_GIT_ORDER_GOODS);
//            List<TradeMarketingDTO> marketings = tradeMarketingList.stream().filter(i -> i.getSkuIds().stream()
//                    .anyMatch(ids::contains)).collect(Collectors.toList());

//            TradeGrouponCommitForm grouponForm = null;
//            if (Objects.nonNull(request.getOpenGroupon())) {
//                // 当为拼团单时，设置拼团form
//                grouponForm = new TradeGrouponCommitForm();
//                grouponForm.setOpenGroupon(request.getOpenGroupon());
//                grouponForm.setGrouponNo(request.getGrouponNo());
//            }

            if (StringUtils.isNotBlank(request.getSnapshotType())) {
                itemGroups.add(new TradeItemGroup(items, supplier, null, request.getStoreBagsFlag(), request
                        .getSnapshotType(), null, null, null, SaleType.WHOLESALE));
            } else {
                itemGroups.add(new TradeItemGroup(items, supplier, null, request.getStoreBagsFlag(), null, null, null, null, SaleType.WHOLESALE));
            }
        });
        //快照生成采用幂等操作
        TradeItemSnapshot tradeItemSnapshot = repository.findByBuyerId(customerId).orElse(null);
        if (Objects.nonNull(tradeItemSnapshot)) {
            tradeItemSnapshotService.deleteTradeItemSnapshot(tradeItemSnapshot.getId());
        }
        //生成快照
        TradeItemSnapshot snapshot = TradeItemSnapshot.builder()
                .id(UUIDUtil.getUUID())
                .buyerId(customerId)
                .itemGroups(itemGroups)
                .snapshotType(request.getSnapshotType())
                .build();
        tradeItemSnapshotService.addTradeItemSnapshot(snapshot);
    }

    /**
     * 删除订单商品快照
     *
     * @param customerId 用户id
     */
    public void remove(String customerId) {
        repository.deleteByBuyerId(customerId);
    }


    /**
     * 计算平摊价
     *
     * @param tradeItems 订单商品
     * @param newTotal   新的总价
     */
    public void clacSplitPrice(List<TradeItem> tradeItems, BigDecimal newTotal) {
        // 1.如果新的总价为0或空，设置所有商品均摊价为0
        if (newTotal == null || BigDecimal.ZERO.equals(newTotal)) {
            tradeItems.forEach(tradeItem -> tradeItem.setSplitPrice(BigDecimal.ZERO));
            return;
        }

        // 2.计算商品旧的总价
        BigDecimal total = this.calcSkusTotalPrice(tradeItems);

        // 3.计算商品均摊价
        this.calcSplitPrice(tradeItems, newTotal, total);
    }

    /**
     * 计算商品集合的均摊总价
     */
    public BigDecimal calcSkusTotalPrice(List<TradeItem> tradeItems) {
        /*return tradeItems.stream()
                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);*/
        if(!tradeItems.get(0).getWalletSettlements().isEmpty()){
            BigDecimal total = BigDecimal.ZERO;
            for(int i=0;i<tradeItems.size();i++){
                total = total.add(tradeItems.get(i).getWalletSettlements().stream()
                        .filter(walletSettlement -> walletSettlement.getSplitPrice() != null)
                        .map(TradeItem.WalletSettlement::getSplitPrice)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO));

                total = total.add(tradeItems.get(i).getWalletSettlements().stream()
                        .filter(walletSettlement -> walletSettlement.getReduceWalletPrice() != null)
                        .map(TradeItem.WalletSettlement::getReduceWalletPrice)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            }
            return total;
        }else{
            return tradeItems.stream()
                    .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                    .map(TradeItem::getSplitPrice)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        }
    }

    /**
     * 计算商品集合的积分抵扣均摊总价
     */
    public BigDecimal calcSkusTotalPointsPrice(List<TradeItem> tradeItems) {
        return tradeItems.stream()
                .filter(tradeItem -> tradeItem.getPointsPrice() != null && tradeItem.getPointsPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeItem::getPointsPrice)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 计算商品集合的积分抵扣均摊总数
     */
    public Long calcSkusTotalPoints(List<TradeItem> tradeItems) {
        return tradeItems.stream()
                .filter(tradeItem -> tradeItem.getPoints() != null && tradeItem.getPoints() > 0)
                .map(TradeItem::getPoints)
                .reduce(0L, (a, b) -> a + b);
    }

    /**
     * 计算商品均摊价
     *
     * @param tradeItems 待计算的商品列表
     * @param newTotal   新的总价
     * @param total      旧的商品总价
     */
    public void calcSplitPrice(List<TradeItem> tradeItems, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return;
        }

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        Long totalNum = tradeItems.stream().map(tradeItem -> tradeItem.getNum()).reduce(0L, Long::sum);

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                tradeItem.setSplitPrice(newTotal.subtract(splitPriceTotal));
            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                //全是零元商品按数量均摊
                if (BigDecimal.ZERO.equals(total)) {
                    tradeItem.setSplitPrice(
                            newTotal.multiply(BigDecimal.valueOf(tradeItem.getNum()))
                                    .divide(BigDecimal.valueOf(totalNum), 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    tradeItem.setSplitPrice(
                            splitPrice
                                    .divide(total, 10, BigDecimal.ROUND_DOWN)
                                    .multiply(newTotal)
                                    .setScale(2, BigDecimal.ROUND_DOWN));
                }
                splitPriceTotal = splitPriceTotal.add(tradeItem.getSplitPrice());
            }
        }
    }

    /**
     * 计算商品均摊价
     * --添加商品最小金额准则(商品数量*0.02)
     *
     * @param tradeItems 待计算的商品列表
     * @param newTotal   新的总价
     * @param total      旧的商品总价
     */
    public void calcSplitPriceMinimumAmount(List<TradeItem> tradeItems, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return;
        }

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        Long totalNum = tradeItems.stream().map(tradeItem -> tradeItem.getNum()).reduce(0L, Long::sum);

        //平均值
//        BigDecimal divide = newTotal.divide(BigDecimal.valueOf(totalNum), 6, BigDecimal.ROUND_DOWN);

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                tradeItem.setSplitPrice(newTotal.subtract(splitPriceTotal));
            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                //全是零元商品按数量均摊
                if (BigDecimal.ZERO.equals(total)) {
                    BigDecimal newSplitPrice = newTotal.multiply(BigDecimal.valueOf(tradeItem.getNum()))
                            .divide(BigDecimal.valueOf(totalNum), 4, BigDecimal.ROUND_DOWN);

                    tradeItem.setSplitPrice(newSplitPrice);

                } else {
                    BigDecimal newSplitPrice = splitPrice
                            .divide(total, 10, BigDecimal.ROUND_DOWN)
                            .multiply(newTotal)
                            .setScale(4, BigDecimal.ROUND_DOWN);
                    tradeItem.setSplitPrice(newSplitPrice);

//                    //最小金额准则, 平摊价格小于商品 数量*0.02, 则以 商品数量*0.02 为准
//                    BigDecimal minimumAmountPrice = new BigDecimal("0.02").multiply(tradeItem.getBNum()).setScale(2, BigDecimal.ROUND_DOWN);
//                    newSplitPrice = (newSplitPrice.compareTo(minimumAmountPrice) < 0) ? minimumAmountPrice : newSplitPrice;
////                    newSplitPrice = (divide.multiply(BigDecimal.valueOf(tradeItem.getNum())).compareTo(newSplitPrice) < 0) ? divide : newSplitPrice;
//                    tradeItem.setSplitPrice(newSplitPrice.setScale(2, BigDecimal.ROUND_DOWN));
                }
                splitPriceTotal = splitPriceTotal.add(tradeItem.getSplitPrice());
            }
        }
    }

    /**
     * 计算积分抵扣均摊价、均摊数量
     *
     * @param tradeItems       待计算的商品列表
     * @param pointsPriceTotal 积分抵扣总额
     * @param pointsTotal      积分抵扣总数
     */
    public void calcPoints(List<TradeItem> tradeItems, BigDecimal pointsPriceTotal, Long pointsTotal, BigDecimal pointWorth) {
        BigDecimal totalPrice = tradeItems.stream()
                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积积分平摊价，将剩余扣给最后一个元素
        Long splitPointsTotal = 0L;//累积积分数量，将剩余扣给最后一个元素

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                tradeItem.setPointsPrice(pointsPriceTotal.subtract(splitPriceTotal));
                tradeItem.setPoints(pointsTotal - splitPointsTotal);
            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                tradeItem.setPointsPrice(
                        splitPrice
                                .divide(totalPrice, 10, BigDecimal.ROUND_DOWN)
                                .multiply(pointsPriceTotal)
                                .setScale(2, BigDecimal.ROUND_HALF_UP));
                splitPriceTotal = splitPriceTotal.add(tradeItem.getPointsPrice());

                tradeItem.setPoints(tradeItem.getPointsPrice().multiply(pointWorth).longValue());
                splitPointsTotal = splitPointsTotal + tradeItem.getPoints();
            }
        }
    }

    public TradeItemSnapshot findByCustomerId(String customerId) {
        return repository.findByBuyerId(customerId).orElse(null);
    }

    public void updateTradeItemSnapshotNoRollback(TradeItemSnapshot tradeItemSnapshot) {
        tradeItemSnapshotService.updateTradeItemSnapshotNoRollback(tradeItemSnapshot);
    }

    /**
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

    /**
     * 包装营销信息(供确认订单使用)
     */
    private TradeMarketingWrapperTryCatchVO wrapperMarketingForConfirm(List<TradeItem> skus, List<TradeMarketingDTO>
            tradeMarketingRequests) {

        // 1.构建营销插件请求对象
        List<TradeMarketingWrapperDTO> requests = new ArrayList<>();
        List<TradeMarketingVO> tradeMarketings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tradeMarketingRequests)) {
            tradeMarketingRequests.forEach(tradeMarketing -> {
                List<TradeItemInfoDTO> tradeItems = skus.stream()
                        .filter(s -> tradeMarketing.getSkuIds().contains(s.getSkuId()))
                        .map(t -> TradeItemInfoDTO.builder()
                                .num(t.getNum())
                                .price(t.getPrice())
                                .skuId(t.getSkuId())
                                .storeId(t.getStoreId())
                                .distributionGoodsAudit(t.getDistributionGoodsAudit())
                                .build())
                        .collect(Collectors.toList());
                requests.add(TradeMarketingWrapperDTO.builder()
                        .tradeMarketingDTO(tradeMarketing)
                        .tradeItems(tradeItems).build());
            });


        }
        TradeMarketingWrapperTryCatchVO result = new TradeMarketingWrapperTryCatchVO();
        // 2.调用营销插件，并设置满系营销信息
        if (CollectionUtils.isNotEmpty(requests)) {
            BaseResponse<MarketingTradeBatchTryCatchWrapperResponse> response = marketingTradePluginProvider.batchWrapperTryCatch(MarketingTradeBatchWrapperRequest.builder()
                    .wraperDTOList(requests).build());
            List<TradeMarketingWrapperVO> voList = response.getContext().getWraperVOList();
            if (CollectionUtils.isNotEmpty(voList)) {
                voList.forEach(tradeMarketingWrapperVO -> {
                    tradeMarketings.add(tradeMarketingWrapperVO.getTradeMarketing());
                });
            }
            result.setMarketingIds(response.getContext().getMarketingIds());
            result.setTradeMarketingWrapperVOS(response.getContext().getWraperVOList());
        }

        return result;
    }

    /**
     * 计算商品余额抵扣均摊价
     * --添加商品最小金额准则(商品数量*0.02)
     *
     * @param tradeItems 待计算的商品列表
     * @param newTotal   新的余额抵扣总价
     * @param total      旧的余额抵扣总价
     */
    public List<BigDecimal> calcSplitWalletPrice(List<TradeItem> tradeItems, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return Collections.emptyList();
        }

        BigDecimal deductPriceTotal = total.subtract(newTotal);

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        long totalNum = tradeItems.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
        List<BigDecimal> splitPrices = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                splitPrices.add(deductPriceTotal.subtract(splitPriceTotal));
                continue;
            }

            BigDecimal newSplitPrice;
            //全是零元商品按数量均摊
            if (BigDecimal.ZERO.equals(total)) {
                newSplitPrice = deductPriceTotal
                        .multiply(BigDecimal.valueOf(tradeItem.getNum()))
                        .divide(BigDecimal.valueOf(totalNum), 4, RoundingMode.DOWN);
                splitPrices.add(newSplitPrice);

            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                newSplitPrice = splitPrice
                        .divide(total, 10, RoundingMode.DOWN)
                        .multiply(deductPriceTotal)
                        .setScale(4, RoundingMode.DOWN);

//                    //最小金额准则, 平摊价格小于商品 数量*0.02, 则以 商品数量*0.02 为准
//                    BigDecimal minimumAmountPrice = new BigDecimal("0.02").multiply(tradeItem.getBNum()).setScale(2, BigDecimal.ROUND_DOWN);
//                    newSplitPrice = (newSplitPrice.compareTo(minimumAmountPrice) < 0) ? minimumAmountPrice : newSplitPrice;
                splitPrices.add(newSplitPrice.setScale(4, RoundingMode.DOWN));
            }
            splitPriceTotal = splitPriceTotal.add(newSplitPrice);
        }
        return splitPrices;
    }

}
