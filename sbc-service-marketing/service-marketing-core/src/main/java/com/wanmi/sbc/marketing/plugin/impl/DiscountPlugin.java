package com.wanmi.sbc.marketing.plugin.impl;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.goodsdevanning.GoodsDevanningProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsDevanningQueryRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingReductionLevelVO;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingPluginRequest;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.discount.repository.MarketingFullDiscountLevelRepository;
import com.wanmi.sbc.marketing.plugin.IGoodsDetailPlugin;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.plugin.ITradeCommitPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
 * 营销满折插件
 * Created by dyt on 2016/12/8.
 */
@Repository("discountPlugin")
@Slf4j
public class DiscountPlugin implements IGoodsListPlugin, IGoodsDetailPlugin, ITradeCommitPlugin {


    @Autowired
    private MarketingFullDiscountLevelRepository marketingFullDiscountLevelRepository;

    @Autowired
    private MarketingRepository marketingRepository;

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
                .filter(marketing -> MarketingType.DISCOUNT.equals(marketing.getMarketingType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketingList)) {
            return;
        }

        List<Long> marketingIds = marketingList.stream().map(MarketingResponse::getMarketingId).collect(Collectors.toList());

        Map<Long, MarketingResponse> params = new HashMap<>();
        marketingList.forEach(marketingResponse -> params.put(marketingResponse.getMarketingId(), marketingResponse));
        //填充营销描述<营销编号,描述>
        Map<Long, List<MarketingReductionLevelVO>> labelMap = this.getLabelMap(marketingIds, params);
        log.info("DiscountPlugin.goodsListFilter labelMap:{}", JSONObject.toJSONString(labelMap));
        goodsInfos.stream()
                .filter(goodsInfo -> request.getMarketingMap().containsKey(goodsInfo.getGoodsInfoId()))
                .forEach(goodsInfo -> {
                    /**
                     * 查询营销有效的商品数
                     */
//                    List<String> marketingByGoodsInfoIdsAndNum = marketingRepository.getMarketingByGoodsInfoIdsAndNum(marketingIds);

                    request.getMarketingMap().get(goodsInfo.getGoodsInfoId()).stream()
                            .filter(marketing -> MarketingType.DISCOUNT.equals(marketing.getMarketingType()))
                            .forEach(marketing -> {
                                Integer listSize = labelMap.get(marketing.getMarketingId()).size();
                                MarketingLabelVO label = new MarketingLabelVO();
                                label.setMarketingId(marketing.getMarketingId());
                                label.setMarketingType(marketing.getMarketingType().toValue());
                                label.setMarketingDesc(labelMap.get(marketing.getMarketingId()).get(0).getMarketingDesc());
                                label.setMarketingDescList(labelMap.get(marketing.getMarketingId()).stream().map(marketings->marketings.getMarketingDesc()).collect(Collectors.toList()));
                                label.setWhetherChoice(marketing.getWhetherChoice());
                                label.setSubType(marketing.getSubType().toValue());
                                label.setNumber(labelMap.get(marketing.getMarketingId()).get(0).getNumber());
                                label.setFullFold(labelMap.get(marketing.getMarketingId()).get(0).getFullFold());
                                if (Objects.nonNull(marketing.getMarketingScopeList())){
                                    marketing.getMarketingScopeList().stream().forEach(marketingScope -> {
                                        if (marketingScope.getMarketingId().equals(marketingScope.getMarketingId())
                                                && Objects.nonNull(marketingScope.getPurchaseNum()) && marketingScope.getPurchaseNum() > 0
                                                && marketingScope.getScopeId().equals(goodsInfo.getGoodsInfoId())){
                                            label.setGoodsPurchasingNumber(marketingScope.getPurchaseNum().intValue());
                                        }
                                    });
                                }
                                label.setLevelLabelVOS(KsBeanUtil.convert(labelMap.get(marketing.getMarketingId()), MarketingReductionLevelLabelVO.class));
                                if(!Objects.isNull(marketing.getIsAddMarketingName())
                                        && BoolFlag.YES.toValue() == marketing.getIsAddMarketingName().toValue()){
                                    List<String> desc = labelMap.get(marketing.getMarketingId()).stream().map(marketings ->marketings.getMarketingDesc()).collect(Collectors.toList());
                                    List<String> newDesc = new ArrayList<>();
                                    if(CollectionUtils.isNotEmpty(desc)){
                                        desc.forEach(d->{
                                            newDesc.add(d+" （同种活动可跨单品）");
                                        });
                                    }
                                    label.setMarketingDescList(newDesc);
                                    label.setMarketingDesc(labelMap.get(marketing.getMarketingId()).get(0).getMarketingDesc() + " （同种活动可跨单品）");
                                }
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
                || marketingList.stream().filter(marketing -> MarketingType.DISCOUNT.equals(marketing.getMarketingType())).count() < 1) {
            return;
        }

        MarketingResponse marketingObj = marketingList.stream().filter(marketing -> MarketingType.DISCOUNT.equals(marketing.getMarketingType())).findFirst().get();
        //填充营销描述<营销编号,描述>
        Map<Long, MarketingResponse> params = new HashMap<>();
        params.put(marketingObj.getMarketingId(), marketingObj);
        Map<Long, List<MarketingReductionLevelVO>> labelMap = this.getLabelMap(Collections.singletonList(marketingObj.getMarketingId()), params);
        MarketingLabelVO label = new MarketingLabelVO();
        label.setMarketingId(marketingObj.getMarketingId());
        label.setMarketingType(marketingObj.getMarketingType().toValue());
        label.setMarketingDescList(labelMap.get(marketingObj.getMarketingId()).stream().map(marketings ->marketings.getMarketingDesc()).collect(Collectors.toList()));
        detailResponse.getGoodsInfo().getMarketingLabels().add(label);
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfo(TradeMarketingPluginRequest request) {
        TradeMarketingRequest tradeMarketingDTO = request.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = request.getTradeItems();
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
        if (marketing.getMarketingType() != MarketingType.DISCOUNT) {
            return null;
        }
        MarketingFullDiscountLevel level = marketingFullDiscountLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));
//        BigDecimal price = tradeItems.stream().map(i -> i.getPrice().multiply(new BigDecimal(i.getNum()))).reduce(
//                BigDecimal.ZERO, BigDecimal::add);


        BigDecimal price = tradeItems.stream().map(TradeItemInfo::getAllMarketPrice).reduce(
                BigDecimal.ZERO, BigDecimal::add);

        if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT) {
            //满金额赠
            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
        } else if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT) {
            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);
            //满数量赠
            if (count < level.getFullCount()) {
                throw new SbcRuntimeException("K-050312");
            }
        }
//        else if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER) {
//            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);
//            //满数量赠
//            if (count < level.getFullCount()) {
//                throw new SbcRuntimeException("K-050312");
//            }
//        }

        BigDecimal amount = price.multiply(level.getDiscount()).setScale(2, BigDecimal.ROUND_DOWN);

        TradeMarketingResponse response = new TradeMarketingResponse();
        response.setTradeMarketing(TradeMarketing.builder()
                .fullDiscountLevel(level)
                .discountsAmount(price.subtract(amount))
                .realPayAmount(amount)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .skuIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .build());
        return response;
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfoDevanning(TradeMarketingPluginRequest request) {
        TradeMarketingRequest tradeMarketingDTO = request.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = request.getTradeItems();
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
        if (marketing.getMarketingType() != MarketingType.DISCOUNT) {
            return null;
        }
        MarketingFullDiscountLevel level = marketingFullDiscountLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));
        BigDecimal price = tradeItems.stream().map(i -> i.getPrice().multiply(new BigDecimal(i.getNum()))).reduce(
                BigDecimal.ZERO, BigDecimal::add);
        if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT) {
            //满金额赠
            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
        } else if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT) {
//             Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);

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
            if (count.compareTo(BigDecimal.valueOf(level.getFullCount()))<0 ) {
                throw new SbcRuntimeException("K-050312");
            }
        }
//        else if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER) {
//            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);
//            //满数量赠
//            if (count < level.getFullCount()) {
//                throw new SbcRuntimeException("K-050312");
//            }
//        }

        BigDecimal amount = price.multiply(level.getDiscount()).setScale(2, BigDecimal.ROUND_DOWN);

        TradeMarketingResponse response = new TradeMarketingResponse();
        response.setTradeMarketing(TradeMarketing.builder()
                .fullDiscountLevel(level)
                .discountsAmount(price.subtract(amount))
                .realPayAmount(amount)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .skuIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .build());
        return response;
    }

    /**
     * 获取营销描述<营销编号,描述>
     * desc倒叙
     * @param marketingIds 营销编号
     * @return
     */
    private Map<Long, List<MarketingReductionLevelVO>> getLabelMap(List<Long> marketingIds, Map<Long, MarketingResponse> marketingMap) {
        Map<Long, List<MarketingFullDiscountLevel>> levelsMap = marketingFullDiscountLevelRepository.findAll((root, cquery, cbuild) -> root.get("marketingId").in(marketingIds),
                        Sort.by(Arrays.asList(
                                new Sort.Order(Sort.Direction.ASC, "fullAmount"),
                                new Sort.Order(Sort.Direction.ASC, "fullCount")
//                                new Sort.Order(Sort.Direction.ASC, "discount")
                        )))
                .stream().collect(Collectors.groupingBy(MarketingFullDiscountLevel::getMarketingId));
        Map<Long, List<MarketingReductionLevelVO>> labelMap = new HashMap<>();
        DecimalFormat fmt = new DecimalFormat("#.###");
        levelsMap.forEach((marketingId, levels) -> {
            List<MarketingReductionLevelVO> descs = levels.stream().map(level -> {
                MarketingReductionLevelVO reductionLevelVO = new MarketingReductionLevelVO();
                reductionLevelVO.setFullFold(level.getDiscount().multiply(new BigDecimal(10)));
                reductionLevelVO.setDiscount(level.getDiscount().multiply(new BigDecimal(10)));
                if (MarketingSubType.DISCOUNT_FULL_AMOUNT.equals(marketingMap.get(marketingId).getSubType())) {
                    reductionLevelVO.setMarketingDesc(String.format("满%s元立享%s折", fmt.format(level.getFullAmount()),fmt.format(level.getDiscount().multiply(new BigDecimal(10)))));
                    reductionLevelVO.setAmount(level.getFullAmount());
                    return reductionLevelVO;
                }
//                reductionLevelVO.setFullFold(level.getDiscount().multiply(new BigDecimal(10)));
                reductionLevelVO.setNumber(level.getFullCount().intValue());
                reductionLevelVO.setMarketingDesc(String.format("满%s箱立享%s折", fmt.format(level.getFullCount()),fmt.format(level.getDiscount().multiply(new BigDecimal(10)))));
                BigDecimal disCount = level.getDiscount().multiply(new BigDecimal(100));

                if(2 == level.getFullCount() && BigDecimal.valueOf(75).compareTo(disCount) == 0){
                    reductionLevelVO.setMarketingDesc("第二箱半价");
                }
                return reductionLevelVO;
            }).collect(Collectors.toList());
            labelMap.put(marketingId, descs);
        });
        return labelMap;
    }

}