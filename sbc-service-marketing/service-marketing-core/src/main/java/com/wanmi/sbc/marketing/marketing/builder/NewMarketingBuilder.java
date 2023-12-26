package com.wanmi.sbc.marketing.marketing.builder;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.api.request.market.latest.*;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class NewMarketingBuilder {

    private SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest;

    private FullMarketData fullMarketData;

    private Marketing marketing;

    public NewMarketingBuilder(SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest, Marketing marketing) {
        this.saveOrUpdateMarketingRequest = saveOrUpdateMarketingRequest;
        this.fullMarketData = new FullMarketData();
        this.marketing = marketing;
    }

    public NewMarketingBuilder(SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest) {
        this.saveOrUpdateMarketingRequest = saveOrUpdateMarketingRequest;
        this.fullMarketData = new FullMarketData();
        this.marketing = new Marketing();
    }


    /**
     * 构建 Marketing 对象
     * @return
     */
    public NewMarketingBuilder buildMarketing() {
        Long marketingId = saveOrUpdateMarketingRequest.getMarketingId();
        if(Objects.nonNull(marketingId)){
            marketing.setMarketingId(marketingId);
        }

        String marketingName = saveOrUpdateMarketingRequest.getMarketingName();
        if(Objects.nonNull(marketingName)){
            marketing.setMarketingName(marketingName);
        }

        MarketingType marketingType = saveOrUpdateMarketingRequest.getMarketingType();
        if(Objects.nonNull(marketingType)){
            marketing.setMarketingType(marketingType);
        }

        MarketingSubType subType = saveOrUpdateMarketingRequest.getSubType();
        if(Objects.nonNull(subType)){
            marketing.setSubType(subType);
        }

        LocalDateTime beginTime = saveOrUpdateMarketingRequest.getBeginTime();
        if(Objects.nonNull(beginTime)){
            marketing.setBeginTime(beginTime);
        }

        LocalDateTime endTime = saveOrUpdateMarketingRequest.getEndTime();
        if(Objects.nonNull(endTime)){
            marketing.setEndTime(endTime);
        }

        MarketingScopeType scopeType = saveOrUpdateMarketingRequest.getScopeType();
        if(Objects.nonNull(scopeType)){
            marketing.setScopeType(scopeType);
        }

        String joinLevel = saveOrUpdateMarketingRequest.getJoinLevel();
        if(Objects.nonNull(joinLevel)){
            marketing.setJoinLevel(joinLevel);
        }

        BoolFlag isBoss = saveOrUpdateMarketingRequest.getIsBoss();
        if(Objects.nonNull(isBoss)){
            marketing.setIsBoss(isBoss);
        }

        Long storeId = saveOrUpdateMarketingRequest.getStoreId();
        if(Objects.nonNull(storeId)){
            marketing.setStoreId(storeId);
        }

        BoolFlag isOverlap = saveOrUpdateMarketingRequest.getIsOverlap();
        if(Objects.nonNull(isOverlap)){
            marketing.setIsOverlap(isOverlap);
        }

        BoolFlag isAddMarketingName = saveOrUpdateMarketingRequest.getIsAddMarketingName();
        if(Objects.nonNull(isAddMarketingName)){
            marketing.setIsAddMarketingName(isAddMarketingName);
        }

        String deletePerson = saveOrUpdateMarketingRequest.getDeletePerson();
        if(Objects.nonNull(deletePerson)){
            marketing.setDeletePerson(deletePerson);
        }

        Long wareId = saveOrUpdateMarketingRequest.getWareId();
        if(Objects.nonNull(wareId)){
            marketing.setWareId(saveOrUpdateMarketingRequest.getWareId());
        }

        if(Objects.nonNull(saveOrUpdateMarketingRequest.getMarketingId())){
            marketing.setUpdateTime(LocalDateTime.now());
            marketing.setUpdatePerson(saveOrUpdateMarketingRequest.getUpdatePerson());
        } else {
            marketing.setCreateTime(LocalDateTime.now());
            marketing.setCreatePerson(saveOrUpdateMarketingRequest.getCreatePerson());
        }

        if(Objects.nonNull(wareId)){
            marketing.setWareId(saveOrUpdateMarketingRequest.getWareId());
        }

        marketing.setTerminationFlag(BoolFlag.NO);
        marketing.setDelFlag(DeleteFlag.NO);
        marketing.setIsPause(BoolFlag.NO);// 这个状态目前废弃了，给个正常状态的默认值吧

        BoolFlag isDraft = saveOrUpdateMarketingRequest.getIsDraft();
        if(Objects.isNull(isDraft)){
            marketing.setIsDraft(BoolFlag.NO);
        } else {
            marketing.setIsDraft(isDraft);
        }

        marketing.setWhetherChoice(BoolFlag.NO);
        List<MarketingGoodsItemRequest> goodsItemRequests = saveOrUpdateMarketingRequest.getGoodsItemRequest();
        if (CollectionUtils.isNotEmpty(goodsItemRequests)) {
            for (MarketingGoodsItemRequest goodsItemRequest : goodsItemRequests) {
                if(BoolFlag.YES == goodsItemRequest.getWhetherChoice()){
                    marketing.setWhetherChoice(BoolFlag.YES);
                    break;
                }
            }
        }
        fullMarketData.setMarketing(marketing);
        return this;
    }

    /**
     * 构建 MarketingScope 对象
     * @return
     */
    public NewMarketingBuilder buildMarketingScopes() {
        List<MarketingGoodsItemRequest> goodsItemRequests = saveOrUpdateMarketingRequest.getGoodsItemRequest();

        List<MarketingScope> marketingScopes = new ArrayList<>();
        for (MarketingGoodsItemRequest goodsItemRequest : goodsItemRequests) {

            MarketingScope marketingScope = new MarketingScope();
            marketingScope.setScopeId(goodsItemRequest.getSkuId());
            marketingScope.setWhetherChoice(goodsItemRequest.getWhetherChoice());
            if(Optional.ofNullable(marketingScope.getPurchaseNum()).orElse(0L) >= 0){
                marketingScope.setPurchaseNum(goodsItemRequest.getPurchaseNum());
            }
            if(Optional.ofNullable(marketingScope.getPerUserPurchaseNum()).orElse(0L) >= 0){
                marketingScope.setPerUserPurchaseNum(goodsItemRequest.getPerUserPurchaseNum());
            }

            marketingScopes.add(marketingScope);
        }

        fullMarketData.setMarketingScopes(marketingScopes);
        return this;
    }

    /**
     * 构建 MarketingFullGiftLevelWrapper 对象
     * @return
     */
    public NewMarketingBuilder buildMarketingFullGiftLevelWrappers() {
        List<ReachGiftLevelRequest> reachGiftLevelRequests = saveOrUpdateMarketingRequest.getReachGiftLevelRequests();

        List<MarketingFullGiftLevelWrapper> marketingFullGiftLevelWrappers = new ArrayList<>();

        for (ReachGiftLevelRequest reachGiftLevelRequest : reachGiftLevelRequests) {
            MarketingFullGiftLevelWrapper marketingFullGiftLevelWrapper = new MarketingFullGiftLevelWrapper();

            MarketingFullGiftLevel marketingFullGiftLevel = new MarketingFullGiftLevel();
            marketingFullGiftLevel.setGiftType(reachGiftLevelRequest.getGiftType());
            MarketingSubType subType = saveOrUpdateMarketingRequest.getSubType();
            // 满金额减
            if(subType == MarketingSubType.GIFT_FULL_AMOUNT){
                marketingFullGiftLevel.setFullAmount(reachGiftLevelRequest.getThreshold());
            // 满数量减
            } else if(subType == MarketingSubType.GIFT_FULL_COUNT){
                marketingFullGiftLevel.setFullCount(reachGiftLevelRequest.getThreshold().longValue());
            }

            // 关联赠品
            List<MarketingGiveGoodItemRequest> marketingGiveGoodItemRequests = reachGiftLevelRequest.getMarketingGiveGoodItemRequest();
            List<MarketingFullGiftDetail> marketingFullGiftDetails = new ArrayList<>();
            for (MarketingGiveGoodItemRequest marketingGiveGoodItemRequest : marketingGiveGoodItemRequests) {
                MarketingFullGiftDetail marketingFullGiftDetail = new MarketingFullGiftDetail();

                marketingFullGiftDetail.setProductId(marketingGiveGoodItemRequest.getSkuId());
                marketingFullGiftDetail.setProductNum(marketingGiveGoodItemRequest.getProductNum());
                marketingFullGiftDetail.setBoundsNum(marketingGiveGoodItemRequest.getBoundsNum());
                marketingFullGiftDetail.setTerminationFlag(BoolFlag.NO);

                marketingFullGiftDetails.add(marketingFullGiftDetail);
            }

            marketingFullGiftLevelWrapper.setMarketingFullGiftLevel(marketingFullGiftLevel);
            marketingFullGiftLevelWrapper.setMarketingFullGiftDetails(marketingFullGiftDetails);
            marketingFullGiftLevelWrappers.add(marketingFullGiftLevelWrapper);
        }
        fullMarketData.setMarketingFullGiftLevelWrappers(marketingFullGiftLevelWrappers);
        return this;
    }


    /**
     * 构建 MarketingFullDiscountLevel 对象
     * @return
     */
    public NewMarketingBuilder buildMarketingFullDiscountLevels() {
        List<ReachDiscountLevelRequest> reachDiscountLevelRequests = saveOrUpdateMarketingRequest.getReachDiscountLevelRequests();

        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = new ArrayList<>();
        for (ReachDiscountLevelRequest reachDiscountLevelRequest : reachDiscountLevelRequests) {

            MarketingFullDiscountLevel marketingFullDiscountLevel = new MarketingFullDiscountLevel();

            marketingFullDiscountLevel.setDiscount(reachDiscountLevelRequest.getDiscountRate());
            MarketingSubType subType = saveOrUpdateMarketingRequest.getSubType();
            // 满金额减
            if(subType == MarketingSubType.DISCOUNT_FULL_AMOUNT){
                marketingFullDiscountLevel.setFullAmount(reachDiscountLevelRequest.getThreshold());
            // 满数量减
            } else if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){
                marketingFullDiscountLevel.setFullCount(reachDiscountLevelRequest.getThreshold().longValue());
            }

            marketingFullDiscountLevels.add(marketingFullDiscountLevel);
        }

        fullMarketData.setMarketingFullDiscountLevels(marketingFullDiscountLevels);
        return this;
    }

    /**
     * 构建 MarketingFullReductionLevel 对象
     * @return
     */
    public NewMarketingBuilder buildMarketingFullReductionLevels() {
        List<ReachAmountLevelRequest> reachAmountLevelRequests = saveOrUpdateMarketingRequest.getReachAmountLevelRequest();

        List<MarketingFullReductionLevel> marketingFullReductionLevels = new ArrayList<>();
        for (ReachAmountLevelRequest reachAmountLevelRequest : reachAmountLevelRequests) {

            MarketingFullReductionLevel marketingFullReductionLevel = new MarketingFullReductionLevel();

            marketingFullReductionLevel.setReduction(reachAmountLevelRequest.getReduceAmount());
            MarketingSubType subType = saveOrUpdateMarketingRequest.getSubType();
            // 满金额减
            if(subType == MarketingSubType.REDUCTION_FULL_AMOUNT){
                marketingFullReductionLevel.setFullAmount(reachAmountLevelRequest.getThreshold());
            // 满数量减
            } else if(subType == MarketingSubType.REDUCTION_FULL_COUNT){
                marketingFullReductionLevel.setFullCount(reachAmountLevelRequest.getThreshold().longValue());
            }

            marketingFullReductionLevels.add(marketingFullReductionLevel);
        }

        fullMarketData.setMarketingFullReductionLevels(marketingFullReductionLevels);
        return this;
    }

    public FullMarketData build() {
        return this.fullMarketData;
    }

}
