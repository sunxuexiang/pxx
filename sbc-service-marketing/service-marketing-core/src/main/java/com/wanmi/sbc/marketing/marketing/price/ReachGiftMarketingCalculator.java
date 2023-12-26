package com.wanmi.sbc.marketing.marketing.price;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.enums.GiftType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftLevelRepository;
import com.wanmi.sbc.marketing.gift.service.MarketingFullGiftService;
import com.wanmi.sbc.marketing.redis.RedisService;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 满赠营销活动优惠价格计算器
 */
@Slf4j
@Component
public class ReachGiftMarketingCalculator extends MarketingCalculator {

    @Autowired
    private MarketingRepository marketingRepository;
    @Autowired
    private MarketingFullGiftLevelRepository marketingFullGiftLevelRepository;
    @Autowired
    private MarketingFullGiftService marketingFullGiftService;
    @Autowired
    private RedisService redisService;

    @Override
    public String getMarketingTypeEnum() {
        return MarketingType.GIFT.name();
    }

    @Override
    public MarketingCalculatorResult calculate(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing) {
        Boolean checkResult = checkParams(marketing);
        if(BooleanUtils.isFalse(checkResult)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        // 格式化参数
        List<MarketingFullGiftLevel> marketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        if(!CollectionUtils.isEmpty(marketingFullGiftLevels)){
            for (MarketingFullGiftLevel marketingFullGiftLevel : marketingFullGiftLevels){
                List<MarketingFullGiftDetail> marketingFullGiftDetails = marketingFullGiftLevel.getMarketingFullGiftDetails();
                List<MarketingFullGiftDetail> fullGiftDetailList = marketingFullGiftLevel.getFullGiftDetailList();
                if(CollectionUtils.isEmpty(fullGiftDetailList)){
                    marketingFullGiftLevel.setFullGiftDetailList(marketingFullGiftDetails);
                }
            }
        }

        MarketingSubType subType = marketing.getSubType();

        log.info("ReachGiftMarketingCalculator calculate:{}",devanningGoodsInfoVOList);
        log.info("ReachGiftMarketingCalculator calculate marketing:{}",marketing);
        // 满金额减
        if(MarketingSubType.GIFT_FULL_AMOUNT.equals(subType)){
            return this.reachAmount(devanningGoodsInfoVOList, marketing);
        }
        // 满数量减
        return this.reachCount(devanningGoodsInfoVOList, marketing);
    }

    @Override
    public MarketingCalculatorResult calculate(MarketingCaculatorData data)
    {
        log.info("满赠MarketingCaculatorData：{}",data);
        TradeMarketingDTO tradeMarketingDTO = data.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = data.getTradeItems();
        if (tradeMarketingDTO == null) {
            return null;
        }

        Marketing marketing = marketingRepository.findById(tradeMarketingDTO.getMarketingId()).get();
        if (!Objects.equals(marketing.getMarketingType(), MarketingType.GIFT)) {
            return null;
        }
        MarketingFullGiftLevel level = marketingFullGiftLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));

        log.info("营销活动商品实体list"+tradeItems);
        BigDecimal price = tradeItems.stream().map(TradeItemInfo::getAllMarketPrice).reduce(
                BigDecimal.ZERO, BigDecimal::add);

        // 赠品列表
        List<String> giftSkuIds = tradeMarketingDTO.getGiftSkuIds();
        List<MarketingFullGiftDetail> originGiftList = marketingFullGiftService.getGiftList(marketing.getMarketingId(), level.getGiftLevelId());
        List<MarketingFullGiftDetail> giftList = originGiftList.stream().filter(x -> {
            String productId = x.getProductId();
            if(Objects.isNull(productId)){return false;}
            if(CollectionUtils.isEmpty(giftSkuIds)){ return false; }
            BoolFlag terminationFlag = x.getTerminationFlag();
            return giftSkuIds.contains(productId) && BoolFlag.NO.equals(terminationFlag);
        }).collect(Collectors.toList());
        level.setFullGiftDetailList(giftList);
        // 是否叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        // 计算叠加倍数
        Long multiple = 0L;

        // 满金额赠
        if (marketing.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {

            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap)) {
                // 叠加的倍数
                multiple = price.divide(level.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();

                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    Long boundsNum = detail.getBoundsNum();
                    Long outStock = 0L;
                    if(Objects.nonNull(boundsNum)){
                        Long marketingId = marketing.getMarketingId();
                        Long giftLevelId = detail.getGiftLevelId();
                        String productId = detail.getProductId();
                        String key = marketingId + giftLevelId + productId;
                        if(!redisService.hasKey(key)){
                            Object o = redisService.get(key);
                            if(Objects.nonNull(o)){
                                // 已赠送赠品
                                outStock = Long.parseLong(o.toString());
                            }
                        }
                        Long l = detail.getProductNum() * multiple;
                        Long stock = boundsNum - outStock;// 可赠库存
                        Long min = Long.min(stock, l);
                        detail.setProductNum(min);
                    } else {
                        detail.setProductNum(detail.getProductNum() * multiple);
                    }
                }
            }
        } else if (marketing.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);

            if (count < level.getFullCount()) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算赠品数量
            if(BoolFlag.YES.equals(isOverlap)) {
                // 倍数
                multiple = BigDecimal.valueOf(count)
                        .divide(BigDecimal.valueOf(level.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                List<MarketingFullGiftDetail> fullGiftDetailList = level.getFullGiftDetailList();

                for (MarketingFullGiftDetail detail : fullGiftDetailList) {
                    Long boundsNum = detail.getBoundsNum();
                    Long outStock = 0L;
                    if(Objects.nonNull(boundsNum)){
                        Long marketingId = marketing.getMarketingId();
                        Long giftLevelId = detail.getGiftLevelId();
                        String productId = detail.getProductId();
                        String key = marketingId + giftLevelId + productId;
                        if(!redisService.hasKey(key)){
                            Object o = redisService.get(key);
                            if(Objects.nonNull(o)){
                                // 已赠送赠品
                                outStock = Long.parseLong(o.toString());
                            }
                        }
                        Long l = detail.getProductNum() * multiple;
                        Long stock = boundsNum - outStock;// 可赠库存
                        Long min = Long.min(stock, l);
                        detail.setProductNum(min);
                    } else {
                        detail.setProductNum(detail.getProductNum() * multiple);
                    }
                }
            }
        }

        MarketingCalculatorResult.MarketingCalculatorResultBuilder builder = MarketingCalculatorResult.builder()
                .currentFullGiftLevel(KsBeanUtil.convert(level, MarketingFullGiftLevelVO.class))
                .profitAmount(BigDecimal.ZERO)
                .payableAmount(price)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .goodsInfoIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .giftIds(tradeMarketingDTO.getGiftSkuIds())
                .isOverlap(marketing.getIsOverlap());

        if (multiple.compareTo(0L) > 0) {
            builder = builder.reMarketingFold(BigDecimal.valueOf(multiple));
        }
        return builder.build();
    }

    @Override
    public MarketingCalculatorResult calculateIgnoreLevelThreshold(DevanningGoodsInfoVO devanningGoodsInfoVO, Marketing marketing) {
        return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(BigDecimal.ZERO).build();
    }

    /**
     * 满金额增
     * @param devanningGoodsInfoVOList
     * @param marketing
     * @return
     */
    private MarketingCalculatorResult reachAmount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){
        // 营销活动当前达到的最大优惠门槛
        MarketingFullGiftLevel currentFullGiftLevel = null;
        // 营销活动的下一门槛
        MarketingFullGiftLevel nextFullGiftLevel = null;
        // 离下一门槛的差值
        BigDecimal diffNextLevel = null;
        // 市场总价（跨商品）
        BigDecimal totalPayableAmount = BigDecimal.ZERO;
        // 优惠前的金额
        BigDecimal beforeAmount = BigDecimal.ZERO;
        // 优惠价格
        BigDecimal profitAmount = BigDecimal.ZERO;
        // 是否达到门槛
        Boolean reachLevel = Boolean.FALSE;
        // 叠加倍数
        BigDecimal reMarketingFold = BigDecimal.ONE;

        List<MarketingFullGiftLevel> originMarketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        List<MarketingFullGiftLevel> marketingFullGiftLevels = originMarketingFullGiftLevels.stream()
                .sorted(Comparator.comparing(MarketingFullGiftLevel::getFullAmount).reversed())
                .collect(Collectors.toList());

        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();
            BigDecimal payableAmount = marketPrice.multiply(buyCount);
            beforeAmount = beforeAmount.add(payableAmount);
        }
        totalPayableAmount = beforeAmount.subtract(profitAmount);

        for (MarketingFullGiftLevel marketingFullGiftLevel : marketingFullGiftLevels) {
            BigDecimal thresholdAmount = marketingFullGiftLevel.getFullAmount();
            // 未达到活动门槛
            if(beforeAmount.compareTo(thresholdAmount) < 0){
                nextFullGiftLevel = marketingFullGiftLevel;
                continue;
            }
            // 达到活动门槛
            currentFullGiftLevel = marketingFullGiftLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 计算营销活动叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullGiftLevel)){
            // 当前活动的门槛
            BigDecimal thresholdAmount = currentFullGiftLevel.getFullAmount();
            // 计算叠加倍数(忽略四舍五入，向下取整)，叠加赠送
            reMarketingFold = beforeAmount.divide(thresholdAmount,0, BigDecimal.ROUND_DOWN);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }

        /**
         * 所有的满赠门槛都没达到，将currentFullGiftLevel赋值为未达到的营销等级
         * 目的是方便购物车那边做数据格式化
         * 所以前端只能用reachLevel来判断是否达到营销活动的门槛
         */
        if(Objects.equals(Boolean.FALSE, reachLevel)){

            List<MarketingFullGiftLevel> collect = originMarketingFullGiftLevels.stream()
                    .sorted(Comparator.comparing(MarketingFullGiftLevel::getFullAmount))
                    .collect(Collectors.toList());
            MarketingFullGiftLevel first = collect.get(0);
            if(Objects.nonNull(first)){
                currentFullGiftLevel = first;
            }
            if(collect.size() > 1){
                MarketingFullGiftLevel second = collect.get(1);
                if(Objects.nonNull(second)){
                    nextFullGiftLevel = second;
                }
            } else {
                nextFullGiftLevel = null;
            }
        }

        // 计算距离下一活动门槛的差值，还差多少钱
        if(Objects.equals(Boolean.FALSE, reachLevel)){
            // 未达到任何活动门槛
            BigDecimal thresholdAmount = currentFullGiftLevel.getFullAmount();
            diffNextLevel = thresholdAmount.subtract(beforeAmount);
        } else {
            // 已达到活动门槛
            if(Objects.nonNull(nextFullGiftLevel)){
                BigDecimal thresholdAmount = nextFullGiftLevel.getFullAmount();
                diffNextLevel = thresholdAmount.subtract(beforeAmount);
            }
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachGiftMarketingCalculator reachAmount=>beforeAmount:{},payableAmount:{},profitAmount:{}",beforeAmount,totalPayableAmount,profitAmount);

        // 过滤已终止的赠品，已经终止的赠品App不需要展示
        List<MarketingFullGiftDetail> currentMarketingFullGiftDetails = Optional.ofNullable(currentFullGiftLevel)
                .map(MarketingFullGiftLevel::getMarketingFullGiftDetails)
                .orElse(Lists.newArrayList());
        currentMarketingFullGiftDetails = currentMarketingFullGiftDetails.stream().filter(x -> BoolFlag.NO.equals(x.getTerminationFlag())).collect(Collectors.toList());
        currentFullGiftLevel.setMarketingFullGiftDetails(currentMarketingFullGiftDetails);
        currentFullGiftLevel.setFullGiftDetailList(currentMarketingFullGiftDetails);

        if(Objects.nonNull(nextFullGiftLevel)){
            List<MarketingFullGiftDetail> nextMarketingFullGiftDetails = Optional.ofNullable(nextFullGiftLevel)
                    .map(MarketingFullGiftLevel::getMarketingFullGiftDetails)
                    .orElse(Lists.newArrayList());
            nextMarketingFullGiftDetails = nextMarketingFullGiftDetails.stream().filter(x -> BoolFlag.NO.equals(x.getTerminationFlag())).collect(Collectors.toList());
            nextFullGiftLevel.setMarketingFullGiftDetails(nextMarketingFullGiftDetails);
        }

        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullGiftLevel(KsBeanUtil.convert(currentFullGiftLevel, MarketingFullGiftLevelVO.class))
                .nextFullGiftLevel(KsBeanUtil.convert(nextFullGiftLevel, MarketingFullGiftLevelVO.class))
                .build();
    }

    /**
     * 满数量赠
     * @param devanningGoodsInfoVOList
     * @param marketing
     * @return
     */
    private MarketingCalculatorResult reachCount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){

        // 营销活动当前达到的最大优惠门槛
        MarketingFullGiftLevel currentFullGiftLevel = null;
        // 营销活动的下一门槛
        MarketingFullGiftLevel nextFullGiftLevel = null;
        // 离下一门槛的差值
        BigDecimal diffNextLevel = null;
        // 市场总价（跨商品）
        BigDecimal totalPayableAmount = BigDecimal.ZERO;
        // 优惠前的金额
        BigDecimal beforeAmount = BigDecimal.ZERO;
        // 优惠价格
        BigDecimal profitAmount = BigDecimal.ZERO;
        // 是否达到门槛
        Boolean reachLevel = Boolean.FALSE;
        // 叠加倍数
        BigDecimal reMarketingFold = BigDecimal.ONE;
        // 商品总数量（跨商品）
        BigDecimal totalBuyCount = BigDecimal.ZERO;

        List<MarketingFullGiftLevel> originMarketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        List<MarketingFullGiftLevel> marketingFullGiftLevels = originMarketingFullGiftLevels.stream()
                .sorted(Comparator.comparing(MarketingFullGiftLevel::getFullCount).reversed())
                .collect(Collectors.toList());

        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();
            BigDecimal payableAmount = marketPrice.multiply(buyCount);

            beforeAmount = beforeAmount.add(payableAmount);
            totalBuyCount = totalBuyCount.add(buyCount);
        }
        totalPayableAmount = beforeAmount.subtract(profitAmount);


        for (MarketingFullGiftLevel marketingFullGiftLevel : marketingFullGiftLevels) {
            BigDecimal thresholdCount = BigDecimal.valueOf(marketingFullGiftLevel.getFullCount());
            // 未达到活动门槛
            if(totalBuyCount.compareTo(thresholdCount) < 0){
                nextFullGiftLevel = marketingFullGiftLevel;
                continue;
            }
            // 达到活动门槛
            currentFullGiftLevel = marketingFullGiftLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 计算营销活动叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullGiftLevel)){
            // 当前活动的门槛
            BigDecimal thresholdCount = BigDecimal.valueOf(currentFullGiftLevel.getFullCount());
            // 计算叠加倍数(忽略四舍五入，向下取整)，叠加赠送
            reMarketingFold = totalBuyCount.divide(thresholdCount,0, BigDecimal.ROUND_DOWN);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }


        // 未达到任何活动门槛
        if(Objects.equals(Boolean.FALSE, reachLevel)){
            List<MarketingFullGiftLevel> collect = originMarketingFullGiftLevels.stream()
                    .sorted(Comparator.comparing(MarketingFullGiftLevel::getFullCount))
                    .collect(Collectors.toList());
            MarketingFullGiftLevel first = collect.get(0);
            if(Objects.nonNull(first)){
                currentFullGiftLevel = first;
            }
            if(collect.size() > 1){
                MarketingFullGiftLevel second = collect.get(1);
                if(Objects.nonNull(second)){
                    nextFullGiftLevel = second;
                }
            } else {
                nextFullGiftLevel = null;
            }
        }

        // 计算距离下一活动门槛的差值，还差多少件
        if(Objects.equals(Boolean.FALSE, reachLevel)){
            // 未达到任何活动门槛
            BigDecimal thresholdCount = BigDecimal.valueOf(currentFullGiftLevel.getFullCount());
            diffNextLevel = thresholdCount.subtract(totalBuyCount);
        } else {
            // 已达到活动门槛
            if(Objects.nonNull(nextFullGiftLevel)){
                BigDecimal thresholdCount = BigDecimal.valueOf(nextFullGiftLevel.getFullCount());
                diffNextLevel = thresholdCount.subtract(totalBuyCount);
            }
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachGiftMarketingCalculator reachCount=>beforeAmount:{},payableAmount:{},profitAmount:{},totalBuyCount:{}",beforeAmount,totalPayableAmount,profitAmount,totalBuyCount);

        // 过滤已终止的赠品，已经终止的赠品App不需要展示
        List<MarketingFullGiftDetail> currentMarketingFullGiftDetails = Optional.ofNullable(currentFullGiftLevel)
                .map(MarketingFullGiftLevel::getMarketingFullGiftDetails)
                .orElse(Lists.newArrayList());
        currentMarketingFullGiftDetails = currentMarketingFullGiftDetails.stream().filter(x -> BoolFlag.NO.equals(x.getTerminationFlag())).collect(Collectors.toList());
        currentFullGiftLevel.setMarketingFullGiftDetails(currentMarketingFullGiftDetails);
        currentFullGiftLevel.setFullGiftDetailList(currentMarketingFullGiftDetails);

        if(Objects.nonNull(nextFullGiftLevel)){
            List<MarketingFullGiftDetail> nextMarketingFullGiftDetails = Optional.ofNullable(nextFullGiftLevel)
                    .map(MarketingFullGiftLevel::getMarketingFullGiftDetails)
                    .orElse(Lists.newArrayList());
            nextMarketingFullGiftDetails = nextMarketingFullGiftDetails.stream().filter(x -> BoolFlag.NO.equals(x.getTerminationFlag())).collect(Collectors.toList());
            nextFullGiftLevel.setMarketingFullGiftDetails(nextMarketingFullGiftDetails);
        }

        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullGiftLevel(KsBeanUtil.convert(currentFullGiftLevel, MarketingFullGiftLevelVO.class))
                .nextFullGiftLevel(KsBeanUtil.convert(nextFullGiftLevel, MarketingFullGiftLevelVO.class))
                .build();
    }

    private Boolean checkParams(Marketing marketing){
        MarketingSubType subType = marketing.getSubType();
        MarketingSubType[] marketingSubTypes = {
                MarketingSubType.GIFT_FULL_AMOUNT,
                MarketingSubType.GIFT_FULL_COUNT
        };
        if(!ArrayUtils.contains(marketingSubTypes, subType)){
            return false;
        }

        List<MarketingFullGiftLevel> marketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        if(CollectionUtils.isEmpty(marketingFullGiftLevels)){
            return false;
        }

        return true;
    }
}
