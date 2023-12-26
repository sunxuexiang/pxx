package com.wanmi.sbc.marketing.marketing.price;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import com.wanmi.sbc.marketing.reduction.repository.MarketingFullReductionLevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 满减营销活动价格计算器
 */
@Slf4j
@Component
public class ReachAmountMarketingCalculator extends MarketingCalculator {

    @Autowired
    private MarketingFullReductionLevelRepository marketingFullReductionLevelRepository;
    @Autowired
    private MarketingRepository marketingRepository;

    @Override
    public String getMarketingTypeEnum() {
        return MarketingType.REDUCTION.name();
    }

    @Override
    public MarketingCalculatorResult calculate(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing) {

        Boolean checkResult = checkParams(marketing);
        if(BooleanUtils.isFalse(checkResult)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        MarketingSubType subType = marketing.getSubType();

        if(MarketingSubType.REDUCTION_FULL_AMOUNT.equals(subType)){
            return this.reachAmount(devanningGoodsInfoVOList, marketing);
        }

        return this.reachCount(devanningGoodsInfoVOList, marketing);
    }

    @Override
    public MarketingCalculatorResult calculate(MarketingCaculatorData data) {
        log.info("入参==============="+data);
        // 固定营销等级和商品范围进行计算
        TradeMarketingDTO tradeMarketingDTO = data.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = data.getTradeItems();

        if (tradeMarketingDTO == null) {
            return null;
        }

        Marketing marketing = marketingRepository.findById(tradeMarketingDTO.getMarketingId()).get();
        if (marketing.getMarketingType() != MarketingType.REDUCTION) {
            return null;
        }
        MarketingFullReductionLevel level = marketingFullReductionLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));

        BigDecimal price = tradeItems.stream().map(TradeItemInfo::getAllMarketPrice).reduce(
                BigDecimal.ZERO, BigDecimal::add); // 市场价总价
        BigDecimal amount = level.getReduction();  // 满减等级
        // 是否叠加
        BoolFlag isOverlap = marketing.getIsOverlap();
        // 是否是最大的等级
//        List<MarketingFullReductionLevel> levelList = marketingFullReductionLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(tradeMarketingDTO.getMarketingId());
//        MarketingFullReductionLevel maxLevel = levelList.get(levelList.size() - 1);
//        boolean isMax = maxLevel.getReductionLevelId().equals(tradeMarketingDTO.getMarketingLevelId());
        // 叠加倍数
        Long multiple = 0L;
        if (marketing.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT) {
            //满金额减
            log.info("营销商品总价格"+price);
            if (price.compareTo(level.getFullAmount()) < 0) {
                throw new SbcRuntimeException("K-050312");
            }
            // 若可叠加，计算优惠金额
            if(BoolFlag.YES.equals(isOverlap)) {
                multiple = price.divide(level.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                amount = price.divide(level.getFullAmount(), 0, BigDecimal.ROUND_DOWN).multiply(level.getReduction());
            }
        } else if (marketing.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT) {
            Long count = tradeItems.stream().map(TradeItemInfo::getNum).reduce(0L, (a, b) -> a + b);
            //满数量减
            if (count < level.getFullCount()) {
                throw new SbcRuntimeException("K-050312");
            }
            // 用于比较折扣与订单金额
            BigDecimal reduction = level.getReduction();
            // 若可叠加，计算优惠金额
            if (BoolFlag.YES.equals(isOverlap)) {
                multiple = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(level.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                amount = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(level.getFullCount()), 0, BigDecimal.ROUND_DOWN).multiply(level.getReduction());
                reduction = amount;
            }
            if (reduction.compareTo(price) > 0) {
                amount = price;
            }
        }
        MarketingCalculatorResult.MarketingCalculatorResultBuilder builder = MarketingCalculatorResult.builder()
                .currentFullReductionLevel(KsBeanUtil.convert(level, MarketingFullReductionLevelVO.class))
                .profitAmount(amount)
                .payableAmount(price.subtract(amount))
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .goodsInfoIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .isOverlap(marketing.getIsOverlap());

        if (multiple.compareTo(0L) > 0) {
            builder = builder.reMarketingFold(BigDecimal.valueOf(multiple));
        }
        return builder.build();
    }

    @Override
    public MarketingCalculatorResult calculateIgnoreLevelThreshold(DevanningGoodsInfoVO devanningGoodsInfoVO, Marketing marketing) {
        // 满金额减
        List<MarketingFullReductionLevel> marketingFullReductionLevels = marketing.getMarketingFullReductionLevels();
        if(CollectionUtils.isEmpty(marketingFullReductionLevels)){
            return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(BigDecimal.ZERO).build();
        }
        MarketingSubType subType = marketing.getSubType();
        // 满金额减
        if(MarketingSubType.REDUCTION_FULL_AMOUNT.equals(subType)){
            marketingFullReductionLevels = marketingFullReductionLevels.stream()
                    .sorted(Comparator.comparing(MarketingFullReductionLevel::getFullAmount))
                    .collect(Collectors.toList());
            // 获取最低的门槛
            MarketingFullReductionLevel marketingFullReductionLevel = marketingFullReductionLevels.get(0);
            BigDecimal reduction = marketingFullReductionLevel.getReduction();
            return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(reduction).build();
        }
        // 满数量减
        marketingFullReductionLevels = marketingFullReductionLevels.stream()
                .sorted(Comparator.comparing(MarketingFullReductionLevel::getFullCount))
                .collect(Collectors.toList());
        // 获取最低的门槛
        MarketingFullReductionLevel marketingFullReductionLevel = marketingFullReductionLevels.get(0);
        BigDecimal reduction = marketingFullReductionLevel.getReduction();
        return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(reduction).build();
    }


    /**
     * 满金额减
     *
     * @return
     */
    private MarketingCalculatorResult reachAmount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){

        // 营销活动当前达到的最大优惠门槛
        MarketingFullReductionLevel currentFullReductionLevel = null;
        // 营销活动的下一门槛
        MarketingFullReductionLevel nextFullReductionLevel = null;
        // 离下一门槛的差值
        BigDecimal diffNextLevel = null;
        // 优惠前的金额
        BigDecimal beforeAmount = BigDecimal.ZERO;
        // 优惠后的价格
        BigDecimal totalPayableAmount = BigDecimal.ZERO;
        // 优惠价格
        BigDecimal profitAmount = BigDecimal.ZERO;
        // 是否已经达到门槛
        Boolean reachLevel = Boolean.FALSE;
        // 叠加倍数
        BigDecimal reMarketingFold = BigDecimal.ONE;
        // 商品总数量（跨商品）
        BigDecimal totalBuyCount = BigDecimal.ZERO;

        List<MarketingFullReductionLevel> marketingFullReductionLevels = marketing.getMarketingFullReductionLevels();
        marketingFullReductionLevels = marketingFullReductionLevels.stream()
                .sorted(Comparator.comparing(MarketingFullReductionLevel::getFullAmount).reversed())
                .collect(Collectors.toList());

        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());// 购买数量
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice(); // 商品市场价格

            BigDecimal payableAmount = marketPrice.multiply(buyCount);
            beforeAmount = beforeAmount.add(payableAmount);
            totalBuyCount = totalBuyCount.add(buyCount);
        }

        for (MarketingFullReductionLevel marketingFullReductionLevel : marketingFullReductionLevels) {
            BigDecimal thresholdAmount = marketingFullReductionLevel.getFullAmount();
            // 未达到活动门槛
            if(beforeAmount.compareTo(thresholdAmount) < 0){
                nextFullReductionLevel = marketingFullReductionLevel;
                continue;
            }
            // 达到活动门槛
            currentFullReductionLevel = marketingFullReductionLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 计算营销活动叠加倍数
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullReductionLevel)){
            // 当前活动的门槛
            BigDecimal fullAmount = currentFullReductionLevel.getFullAmount();
            // 计算叠加倍数(忽略四舍五入，向下取整)
            reMarketingFold = beforeAmount.divide(fullAmount,0, BigDecimal.ROUND_DOWN);
            log.info("ReachAmountMarketingCalculator reachAmount=>fullAmount:{},reMarketingFold:{}", fullAmount, reMarketingFold);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }

        // 计算优惠金额
        if(Objects.nonNull(currentFullReductionLevel)){
            BigDecimal reduction = currentFullReductionLevel.getReduction();
            BigDecimal multiply = reduction.multiply(reMarketingFold);
            profitAmount = multiply.setScale(2, BigDecimal.ROUND_DOWN);// 直接删除多余的小数
            log.info("ReachAmountMarketingCalculator reachAmount=>reduction:{},multiply:{},profitAmount:{}",reduction, multiply, profitAmount);
        }
        totalPayableAmount = beforeAmount.subtract(profitAmount);

        // 计算距离下一活动门槛的差值, 还差多少钱
        if(Objects.nonNull(nextFullReductionLevel)){
            BigDecimal fullAmount = nextFullReductionLevel.getFullAmount();
            diffNextLevel = fullAmount.subtract(beforeAmount);
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachAmountMarketingCalculator reachAmount=>reMarketingFold:{},beforeAmount:{},totalPayableAmount:{},profitAmount:{}",reMarketingFold,beforeAmount,totalPayableAmount,profitAmount);
        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullReductionLevel(KsBeanUtil.convert(currentFullReductionLevel, MarketingFullReductionLevelVO.class))
                .nextFullReductionLevel(KsBeanUtil.convert(nextFullReductionLevel, MarketingFullReductionLevelVO.class))
                .build();
    }

    /**
     * 满数量减
     * @return
     */
    private MarketingCalculatorResult reachCount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){

        // 营销活动当前达到的最大优惠门槛
        MarketingFullReductionLevel currentFullReductionLevel = null;
        // 营销活动的下一门槛
        MarketingFullReductionLevel nextFullReductionLevel = null;
        // 离下一门槛的差值
        BigDecimal diffNextLevel = null;
        // 优惠前的金额
        BigDecimal beforeAmount = BigDecimal.ZERO;
        // 优惠后的价格
        BigDecimal totalPayableAmount = BigDecimal.ZERO;
        // 优惠价格
        BigDecimal profitAmount = BigDecimal.ZERO;
        // 是否达到门槛
        Boolean reachLevel = Boolean.FALSE;
        // 叠加倍数
        BigDecimal reMarketingFold = BigDecimal.ONE;

        // 商品总数量（跨商品）
        BigDecimal totalBuyCount = BigDecimal.ZERO;

        List<MarketingFullReductionLevel> marketingFullReductionLevels = marketing.getMarketingFullReductionLevels();
        marketingFullReductionLevels = marketingFullReductionLevels.stream()
                .sorted(Comparator.comparing(MarketingFullReductionLevel::getFullCount).reversed())
                .collect(Collectors.toList());

        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();

            BigDecimal payableAmount = marketPrice.multiply(buyCount);
            beforeAmount = beforeAmount.add(payableAmount);
            totalBuyCount = totalBuyCount.add(buyCount);
        }

        for (MarketingFullReductionLevel marketingFullReductionLevel : marketingFullReductionLevels) {
            BigDecimal thresholdCount = BigDecimal.valueOf(marketingFullReductionLevel.getFullCount());
            // 未达到活动门槛
            if(totalBuyCount.compareTo(thresholdCount) < 0){
                nextFullReductionLevel = marketingFullReductionLevel;
                continue;
            }
            // 达到活动门槛
            currentFullReductionLevel = marketingFullReductionLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 计算营销活动叠加的情况
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullReductionLevel)){
            // 当前活动的门槛
            Long threadCount = currentFullReductionLevel.getFullCount();
            // 计算叠加倍数(忽略四舍五入，向下取整)
            reMarketingFold = totalBuyCount.divide(BigDecimal.valueOf(threadCount),0, BigDecimal.ROUND_DOWN);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }

        if(Objects.nonNull(currentFullReductionLevel)){
            BigDecimal reduction = currentFullReductionLevel.getReduction();
            profitAmount = reduction.multiply(reMarketingFold).setScale(2, BigDecimal.ROUND_DOWN);
        }
        totalPayableAmount = beforeAmount.subtract(profitAmount);

        // 计算距离下一活动门槛的差值, 还差多少件
        if(Objects.nonNull(nextFullReductionLevel)){
            Long thresholdCount = nextFullReductionLevel.getFullCount();
            diffNextLevel = BigDecimal.valueOf(thresholdCount).subtract(totalBuyCount);
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachAmountMarketingCalculator reachCount=>beforeAmount:{},payableAmount:{},profitAmount:{}",beforeAmount,totalPayableAmount,profitAmount);
        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullReductionLevel(KsBeanUtil.convert(currentFullReductionLevel, MarketingFullReductionLevelVO.class))
                .nextFullReductionLevel(KsBeanUtil.convert(nextFullReductionLevel, MarketingFullReductionLevelVO.class))
                .build();
    }

    private Boolean checkParams(Marketing marketing){
        MarketingSubType subType = marketing.getSubType();
        List<MarketingSubType> marketingSubTypes = Arrays.asList(
                MarketingSubType.REDUCTION_FULL_COUNT,
                MarketingSubType.REDUCTION_FULL_AMOUNT
        );
        if(!marketingSubTypes.contains(subType)){
            return false;
        }

        List<MarketingFullReductionLevel> marketingFullReductionLevels = marketing.getMarketingFullReductionLevels();
        if(CollectionUtils.isEmpty(marketingFullReductionLevels)){
            return false;
        }

        return true;
    }
}
