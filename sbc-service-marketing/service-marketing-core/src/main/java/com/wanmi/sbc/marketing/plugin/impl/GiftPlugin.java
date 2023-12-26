package com.wanmi.sbc.marketing.plugin.impl;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.goodsdevanning.GoodsDevanningProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsDevanningQueryRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsDevanningVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.MarketingLabelVO;
import com.wanmi.sbc.marketing.bean.enums.GiftType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingPluginRequest;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftLevelRepository;
import com.wanmi.sbc.marketing.gift.service.MarketingFullGiftService;
import com.wanmi.sbc.marketing.plugin.IGoodsDetailPlugin;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.plugin.ITradeCommitPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 营销满赠插件
 * Created by dyt on 2016/12/8.
 */
@Slf4j
@Repository("giftPlugin")
public class GiftPlugin implements IGoodsListPlugin, IGoodsDetailPlugin, ITradeCommitPlugin {


    @Autowired
    private MarketingFullGiftLevelRepository marketingFullGiftLevelRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private MarketingFullGiftService marketingFullGiftService;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    /**
     * 商品列表处理
     *
     * @param goodsInfos 商品数据
     * @param request    参数
     */
    @Override
    public void goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) {
        List<MarketingResponse> marketingList = request.getMarketingMap().values().stream()
                .flatMap(Collection::stream)
                .filter(marketing -> MarketingType.GIFT.equals(marketing.getMarketingType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketingList)) {
            return;
        }

        Map<Long, MarketingResponse> params = new HashMap<>();
        marketingList.forEach(marketingResponse -> params.put(marketingResponse.getMarketingId(), marketingResponse));
        List<Long> marketingIds = marketingList.stream().map(MarketingResponse::getMarketingId).collect(Collectors.toList());
        //填充营销描述<营销编号,描述>
        Map<Long, String> labelMap = this.getLabelMap(marketingIds, params);

        goodsInfos.stream()
                .filter(goodsInfo -> request.getMarketingMap().containsKey(goodsInfo.getGoodsInfoId()))
                .forEach(goodsInfo -> {
                    /**
                     * 查询营销有效的商品数
                     */
//                    List<String> marketingByGoodsInfoIdsAndNum =  marketingRepository.getMarketingByGoodsInfoIdsAndNum(marketingIds);

                    request.getMarketingMap().get(goodsInfo.getGoodsInfoId()).stream()
                            .filter(marketing -> MarketingType.GIFT.equals(marketing.getMarketingType()))
                            .forEach(marketing -> {
                                MarketingLabelVO label = new MarketingLabelVO();
                                label.setMarketingId(marketing.getMarketingId());
                                label.setMarketingType(marketing.getMarketingType().toValue());
                                label.setMarketingDesc(labelMap.get(marketing.getMarketingId()));
                                if (Objects.nonNull(marketing.getMarketingScopeList())){
                                    marketing.getMarketingScopeList().stream().forEach(marketingScope -> {
                                        if (marketingScope.getMarketingId().equals(marketingScope.getMarketingId())
                                                && Objects.nonNull(marketingScope.getPurchaseNum()) && marketingScope.getPurchaseNum() > 0
                                                && marketingScope.getScopeId().equals(goodsInfo.getGoodsInfoId())){
                                            label.setGoodsPurchasingNumber(marketingScope.getPurchaseNum().intValue());
                                        }
                                    });
                                }
                                if(!Objects.isNull(marketing.getIsAddMarketingName())
                                        && BoolFlag.YES.toValue() == marketing.getIsAddMarketingName().toValue()){
                                    label.setMarketingDesc(labelMap.get(marketing.getMarketingId()) + " （同种活动可跨单品）");
                                }
                                label.setSubType(marketing.getSubType().toValue());
                                goodsInfo.getMarketingLabels().add(label);
                            });
                });
    }

    /**
     * 商品详情处理
     *
     * @param detailResponse 商品详情数据
     * @param request        参数
     */
    @Override
    public void goodsDetailFilter(GoodsInfoDetailByGoodsInfoResponse detailResponse, MarketingPluginRequest request) {
        List<MarketingResponse> marketingList = request.getMarketingMap().get(detailResponse.getGoodsInfo().getGoodsInfoId());

        if (CollectionUtils.isEmpty(marketingList)
                || marketingList.stream().filter(marketing -> MarketingType.GIFT.equals(marketing.getMarketingType())).count() < 1) {
            return;
        }

        MarketingResponse marketingObj = marketingList.stream().filter(marketing -> MarketingType.GIFT.equals(marketing.getMarketingType())).findFirst().get();
        //填充营销描述<营销编号,描述>
        Map<Long, MarketingResponse> params = new HashMap<>();
        params.put(marketingObj.getMarketingId(), marketingObj);
        Map<Long, String> labelMap = this.getLabelMap(Collections.singletonList(marketingObj.getMarketingId()), params);
        MarketingLabelVO label = new MarketingLabelVO();
        label.setMarketingId(marketingObj.getMarketingId());
        label.setMarketingType(marketingObj.getMarketingType().toValue());
        label.setMarketingDesc(labelMap.get(marketingObj.getMarketingId()));
        detailResponse.getGoodsInfo().getMarketingLabels().add(label);
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfo(TradeMarketingPluginRequest marketingRequest) {
        TradeMarketingRequest tradeMarketingDTO = marketingRequest.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = marketingRequest.getTradeItems();
        if (tradeMarketingDTO == null) {
            return null;
        }

        // 校验营销关联商品中，是否存在分销商品
        List<String> distriSkuIds = tradeItems.stream()
                .filter(item -> item.getDistributionGoodsAudit() == DistributionGoodsAudit.CHECKED)
                .map(TradeItemInfo::getSkuId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(distriSkuIds)) {
            tradeMarketingDTO.getSkuIds().forEach(skuId -> {
                if (distriSkuIds.contains(skuId)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            });
        }

        Marketing marketing = marketingRepository.findById(tradeMarketingDTO.getMarketingId()).get();
        if (marketing.getMarketingType() != MarketingType.GIFT) {
            return null;
        }
        MarketingFullGiftLevel level = marketingFullGiftLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));
        if (level.getGiftType() == GiftType.ONE && tradeMarketingDTO.getGiftSkuIds().size() > 1) {
            throw new SbcRuntimeException("K-050312");
        }
        log.info("营销活动商品实体list"+tradeItems);
//        BigDecimal price = tradeItems.stream().map(i -> i.getPrice().multiply(new BigDecimal(i.getNum()))).reduce(
//                BigDecimal.ZERO, BigDecimal::add);
        BigDecimal price = tradeItems.stream().map(TradeItemInfo::getAllMarketPrice).reduce(
                BigDecimal.ZERO, BigDecimal::add);

        List<MarketingFullGiftDetail> giftList = marketingFullGiftService.getGiftList(marketing.getMarketingId(), level.getGiftLevelId());
        level.setFullGiftDetailList(giftList);
        // 是否叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        // 是否是最大的等级
        List<MarketingFullGiftLevel> levelList = marketingFullGiftLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(tradeMarketingDTO.getMarketingId());
        MarketingFullGiftLevel maxLevel = levelList.get(levelList.size() - 1);
        boolean isMax = maxLevel.getGiftLevelId().equals(tradeMarketingDTO.getMarketingLevelId());

        // 叠加倍数
        Long multiple = 0L;
        if (marketing.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
            //满金额赠
            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap) && isMax) {
                // 倍数
                multiple = price.divide(level.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();
                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    // 赠品数量
                    detail.setProductNum(detail.getProductNum() * multiple);
                }
            }
        } else if (marketing.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);
            //满数量赠
            if (count < level.getFullCount()) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap) && isMax) {
                // 倍数
                multiple = BigDecimal.valueOf(count)
                        .divide(BigDecimal.valueOf(level.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();
                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    // 赠品数量
                    detail.setProductNum(detail.getProductNum() * multiple);
                }
            }
        }

        TradeMarketingResponse response = new TradeMarketingResponse();
        TradeMarketing tradeMarketing = TradeMarketing.builder()
                .giftLevel(level)
                .discountsAmount(BigDecimal.ZERO)
                .realPayAmount(price)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .skuIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .giftIds(tradeMarketingDTO.getGiftSkuIds())
                .isOverlap(marketing.getIsOverlap())
                .build();
        if (multiple.compareTo(0L) > 0) {
            tradeMarketing.setMultiple(multiple);
        }
        response.setTradeMarketing(tradeMarketing);
        return response;
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfoDevanning(TradeMarketingPluginRequest marketingRequest) {
        TradeMarketingRequest tradeMarketingDTO = marketingRequest.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = marketingRequest.getTradeItems();
        if (tradeMarketingDTO == null) {
            return null;
        }

        // 校验营销关联商品中，是否存在分销商品
        List<String> distriSkuIds = tradeItems.stream()
                .filter(item -> item.getDistributionGoodsAudit() == DistributionGoodsAudit.CHECKED)
                .map(TradeItemInfo::getSkuId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(distriSkuIds)) {
            tradeMarketingDTO.getSkuIds().forEach(skuId -> {
                if (distriSkuIds.contains(skuId)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            });
        }

        Marketing marketing = marketingRepository.findById(tradeMarketingDTO.getMarketingId()).get();
        if (marketing.getMarketingType() != MarketingType.GIFT) {
            return null;
        }
        MarketingFullGiftLevel level = marketingFullGiftLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));
        if (level.getGiftType() == GiftType.ONE && tradeMarketingDTO.getGiftSkuIds().size() > 1) {
            throw new SbcRuntimeException("K-050312");
        }
        BigDecimal price = tradeItems.stream().map(i -> i.getPrice().multiply(new BigDecimal(i.getNum()))).reduce(
                BigDecimal.ZERO, BigDecimal::add);
        List<MarketingFullGiftDetail> giftList = marketingFullGiftService.getGiftList(marketing.getMarketingId(), level.getGiftLevelId());
        level.setFullGiftDetailList(giftList);
        // 是否叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        // 是否是最大的等级
        List<MarketingFullGiftLevel> levelList = marketingFullGiftLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(tradeMarketingDTO.getMarketingId());
        MarketingFullGiftLevel maxLevel = levelList.get(levelList.size() - 1);
        boolean isMax = maxLevel.getGiftLevelId().equals(tradeMarketingDTO.getMarketingLevelId());

        // 叠加倍数
        Long multiple = 0L;
        if (marketing.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
            //满金额赠
            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap) && isMax) {
                // 倍数
                multiple = price.divide(level.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();
                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    // 赠品数量
                    detail.setProductNum(detail.getProductNum() * multiple);
                }
            }
        } else if (marketing.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
//            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);

            AtomicReference<BigDecimal> newcount = new AtomicReference<>(BigDecimal.ZERO);
            //通过Spuid获取最小单位的步长
            List<String> SpuId = tradeItems.stream().map(TradeItemInfo::getSpuId).collect(Collectors.toList());
            List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getmaxdata(DevanningGoodsInfoPageRequest.builder().goodsIds(SpuId).build()).getContext().getDevanningGoodsInfoVOS();
            Map<String, DevanningGoodsInfoVO> collect = devanningGoodsInfoVOS.stream().collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsId, Function.identity()));

            tradeItems.stream().forEach(tradeItemInfo -> {
                if (Objects.nonNull(collect.get(tradeItemInfo.getSpuId()))){
                    BigDecimal step = collect.get(tradeItemInfo.getSpuId()).getAddStep();
                    newcount.set(newcount.get().add (BigDecimal.valueOf(tradeItemInfo.getNum()).divide(step,2,BigDecimal.ROUND_DOWN) ));
                }
            });
            BigDecimal count = newcount.get();

            if (count.compareTo(BigDecimal.ZERO)<=0){
                //说明是散批
                count=BigDecimal.valueOf(tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b));
            }


            //满数量赠
            if (count.compareTo(BigDecimal.valueOf(level.getFullCount())) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap) && isMax) {
                // 倍数
                multiple =count
                        .divide(BigDecimal.valueOf(level.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();
                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    // 赠品数量
                    detail.setProductNum(detail.getProductNum() * multiple);
                }
            }
        }

        TradeMarketingResponse response = new TradeMarketingResponse();
        TradeMarketing tradeMarketing = TradeMarketing.builder()
                .giftLevel(level)
                .discountsAmount(BigDecimal.ZERO)
                .realPayAmount(price)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .skuIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .giftIds(tradeMarketingDTO.getGiftSkuIds())
                .isOverlap(marketing.getIsOverlap())
                .build();
        if (multiple.compareTo(0L) > 0) {
            tradeMarketing.setMultiple(multiple);
        }
        response.setTradeMarketing(tradeMarketing);
        return response;
    }

    /**
     * 获取营销描述<营销编号,描述>
     *
     * @param marketingIds 营销编号
     * @return
     */
    private Map<Long, String> getLabelMap(List<Long> marketingIds, Map<Long, MarketingResponse> marketingMap) {
        Map<Long, List<MarketingFullGiftLevel>> levelsMap = marketingFullGiftLevelRepository.findAll((root, cquery, cbuild) -> root.get("marketingId").in(marketingIds),  Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "fullAmount"), new Sort.Order(Sort.Direction.ASC, "fullCount"))))
                .stream().collect(Collectors.groupingBy(MarketingFullGiftLevel::getMarketingId));
        Map<Long, String> labelMap = new HashMap<>();
        DecimalFormat fmt = new DecimalFormat("#.##");
        levelsMap.forEach((marketingId, levels) -> {
            if (MarketingSubType.GIFT_FULL_COUNT.equals(marketingMap.get(marketingId).getSubType())) {
//                List<String> count = levels.stream().filter(level -> Objects.nonNull(level.getFullCount())).map(level -> ObjectUtils.toString(level.getFullCount())).collect(Collectors.toList());

                String fullCount = levels.get(0).getFullCount().toString();
                if (levels.get(0).getGiftType().equals(GiftType.ALL)) {
                    Long productNum = levels.get(0).getFullGiftDetailList().stream().mapToLong(MarketingFullGiftDetail::getProductNum).sum();
                    labelMap.put(marketingId, String.format("买%s赠%s",fullCount,productNum));
                } else {
                    Long productNum = levels.get(0).getFullGiftDetailList().get(0).getProductNum();
                    labelMap.put(marketingId, String.format("买%s赠%s",fullCount,productNum));
                }


            } else if(MarketingSubType.GIFT_FULL_ORDER.equals(marketingMap.get(marketingId).getSubType())) {
                List<String> count = levels.stream().filter(level -> Objects.nonNull(level.getFullCount())).map(level -> ObjectUtils.toString(level.getFullCount())).collect(Collectors.toList());

                labelMap.put(marketingId, String.format("送指定商品", StringUtils.join(Objects.nonNull(count.get(0))?count.get(0):"-")));
            } else if(MarketingSubType.GIFT_FULL_AMOUNT.equals(marketingMap.get(marketingId).getSubType())){
                String fullAmount = levels.get(0).getFullAmount().toString();
                String productNum = levels.get(0).getFullGiftDetailList().get(0).getProductNum().toString();
                labelMap.put(marketingId,String.format("满%s元赠%s箱",fullAmount,productNum));
            }else {
                List<String> amount = levels.stream().filter(level -> Objects.nonNull(level.getFullAmount())).map(level -> fmt.format(level.getFullAmount())).collect(Collectors.toList());

                labelMap.put(marketingId, String.format("送指定商品", StringUtils.join(Objects.nonNull(amount.get(0))?amount.get(0):"-")));
            }
        });
        return labelMap;
    }

}