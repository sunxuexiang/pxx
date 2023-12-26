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
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.discount.repository.MarketingFullDiscountLevelRepository;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import com.wanmi.sbc.marketing.reduction.repository.MarketingFullReductionLevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 满折营销活动价格计算器
 * 全部都是跨单品的
 */
@Slf4j
@Component
public class ReachDiscountMarketingCalculator extends MarketingCalculator {

    @Autowired
    private MarketingFullDiscountLevelRepository marketingFullDiscountLevelRepository;
    @Autowired
    private MarketingRepository marketingRepository;

    @Override
    public String getMarketingTypeEnum() {
        return MarketingType.DISCOUNT.name();
    }

    @Override
    public MarketingCalculatorResult calculate(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing) {
        Boolean checkResult = checkParams( marketing);
        if(BooleanUtils.isFalse(checkResult)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        MarketingSubType subType = marketing.getSubType();

        // 满金额减
        if(MarketingSubType.DISCOUNT_FULL_AMOUNT.equals(subType)){
            return this.reachAmount(devanningGoodsInfoVOList, marketing);
        }
        // 满数量减
        return this.reachCount(devanningGoodsInfoVOList, marketing);
    }

    @Override
    public MarketingCalculatorResult calculate(MarketingCaculatorData data) {
        log.info("满折MarketingCaculatorData：{}",data);
        TradeMarketingDTO tradeMarketingDTO = data.getTradeMarketingDTO();
        List<TradeItemInfo> tradeItems = data.getTradeItems();

        if (tradeMarketingDTO == null) {
            return null;
        }

        Marketing marketing = marketingRepository.findById(tradeMarketingDTO.getMarketingId()).get();
        if (marketing.getMarketingType() != MarketingType.DISCOUNT) {
            return null;
        }
        MarketingFullDiscountLevel level = marketingFullDiscountLevelRepository.findById(tradeMarketingDTO.getMarketingLevelId())
                .orElseThrow(() -> new SbcRuntimeException("K-050312"));

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

        BigDecimal amount = price.multiply(level.getDiscount()).setScale(2, BigDecimal.ROUND_DOWN);

        // 获取叠加倍数
        BoolFlag isOverlap = marketing.getIsOverlap();
        BigDecimal reMarketingFold = BigDecimal.ZERO;
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(level)){
            // 当前活动的门槛
            BigDecimal thresholdAmount = level.getFullAmount();
            reMarketingFold = price.divide(thresholdAmount, RoundingMode.FLOOR);
        }

        MarketingCalculatorResult.MarketingCalculatorResultBuilder builder = MarketingCalculatorResult.builder()
                .currentFullDiscountLevel(KsBeanUtil.convert(level, MarketingFullDiscountLevelVO.class))
                .profitAmount(price.subtract(amount))
                .payableAmount(amount)
                .marketingId(marketing.getMarketingId())
                .marketingName(marketing.getMarketingName())
                .marketingType(marketing.getMarketingType())
                .goodsInfoIds(tradeMarketingDTO.getSkuIds())
                .subType(marketing.getSubType())
                .isOverlap(marketing.getIsOverlap());

        if (reMarketingFold.compareTo(BigDecimal.ZERO) > 0) {
            builder = builder.reMarketingFold(reMarketingFold);
        }
        return builder.build();
    }

    @Override
    public MarketingCalculatorResult calculateIgnoreLevelThreshold(DevanningGoodsInfoVO devanningGoodsInfoVO, Marketing marketing) {
        MarketingSubType subType = marketing.getSubType();
        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = marketing.getMarketingFullDiscountLevels();
        if(CollectionUtils.isEmpty(marketingFullDiscountLevels)){
            return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(BigDecimal.ZERO).build();
        }
        // 满金额减
        if(MarketingSubType.DISCOUNT_FULL_AMOUNT.equals(subType)){
            marketingFullDiscountLevels = marketingFullDiscountLevels.stream()
                    .sorted(Comparator.comparing(MarketingFullDiscountLevel::getFullAmount))
                    .collect(Collectors.toList());

            // 获取最低的门槛
            MarketingFullDiscountLevel marketingFullDiscountLevel = marketingFullDiscountLevels.get(0);
            BigDecimal discount = marketingFullDiscountLevel.getDiscount();

            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();// 市场价
            BigDecimal fullAmount = marketingFullDiscountLevel.getFullAmount();// 满减的门槛
            // 达到门槛必须购买多少件
            BigDecimal buyCount = fullAmount.divide(marketPrice,0, BigDecimal.ROUND_UP);
            BigDecimal totalAmount = marketPrice.multiply(buyCount);


            BigDecimal multiply = totalAmount.multiply(discount);
            multiply = multiply.setScale(2, BigDecimal.ROUND_DOWN);

            BigDecimal subtract = totalAmount.subtract(multiply);
            return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(subtract).build();
        }

        // 满数量折扣
        marketingFullDiscountLevels = marketingFullDiscountLevels.stream()
                .sorted(Comparator.comparing(MarketingFullDiscountLevel::getFullCount))
                .collect(Collectors.toList());

        // 获取最低的门槛
        MarketingFullDiscountLevel marketingFullDiscountLevel = marketingFullDiscountLevels.get(0);
        BigDecimal discount = marketingFullDiscountLevel.getDiscount();

        BigDecimal fullCount = BigDecimal.valueOf(marketingFullDiscountLevel.getFullCount());
        BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();
        BigDecimal totalAmount = marketPrice.multiply(fullCount);
        BigDecimal discountAmount = totalAmount.multiply(discount);
        discountAmount = discountAmount.setScale(2, BigDecimal.ROUND_DOWN);

        BigDecimal subtract = totalAmount.subtract(discountAmount);
        return MarketingCalculatorResult.builder().marketingId(marketing.getMarketingId()).profitAmount(subtract).build();
    }

    /**
     * 满金额折
     * @param devanningGoodsInfoVOList
     * @param marketing
     * @return
     */
    private MarketingCalculatorResult reachAmount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){
        // 营销活动当前达到的最大优惠门槛
        MarketingFullDiscountLevel currentFullDiscountLevel = null;
        // 营销活动的下一门槛
        MarketingFullDiscountLevel nextFullDiscountLevel = null;
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

        // 按照活动门槛从大到小排列
        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = marketing.getMarketingFullDiscountLevels();
        marketingFullDiscountLevels = marketingFullDiscountLevels.stream()
                .sorted(Comparator.comparing(MarketingFullDiscountLevel::getFullAmount).reversed())
                .collect(Collectors.toList());
        // 计算优惠前的总价
        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();
            BigDecimal payableAmount = marketPrice.multiply(buyCount);
            beforeAmount = beforeAmount.add(payableAmount);
        }

        for (MarketingFullDiscountLevel marketingFullDiscountLevel : marketingFullDiscountLevels) {
            BigDecimal thresholdAmount = marketingFullDiscountLevel.getFullAmount();
            // 未达到活动门槛
            if(beforeAmount.compareTo(thresholdAmount) < 0){
                nextFullDiscountLevel = marketingFullDiscountLevel;
                continue;
            }
            // 达到活动门槛
            currentFullDiscountLevel = marketingFullDiscountLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 获取叠加倍数
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullDiscountLevel)){
            // 当前活动的门槛
            BigDecimal thresholdAmount = currentFullDiscountLevel.getFullAmount();
            // 计算叠加倍数(忽略四舍五入，向下取整)
            reMarketingFold = beforeAmount.divide(thresholdAmount,0, BigDecimal.ROUND_DOWN);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }

        // 计算优惠金额
        if(Objects.nonNull(currentFullDiscountLevel)){
            BigDecimal discount = currentFullDiscountLevel.getDiscount();
            int re = reMarketingFold.intValue();
            BigDecimal multiply = beforeAmount.multiply(discount);
            if(re > 1){
                for (int i = re; re > 1; re--){
                    multiply = multiply.multiply(discount);
                }
            }
            totalPayableAmount = multiply.setScale(2, BigDecimal.ROUND_DOWN);// 向下取整保留两位小数

        }
        profitAmount = beforeAmount.subtract(totalPayableAmount);

        // 计算距离下一活动门槛的差值，还差多少钱
        if(Objects.nonNull(nextFullDiscountLevel)){
            BigDecimal thresholdAmount = nextFullDiscountLevel.getFullAmount();
            diffNextLevel = thresholdAmount.subtract(beforeAmount);
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachDiscountMarketingCalculator reachAmount=>beforeAmount:{},payableAmount:{},profitAmount:{}",beforeAmount,totalPayableAmount,profitAmount);


        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullDiscountLevel(KsBeanUtil.convert(currentFullDiscountLevel, MarketingFullDiscountLevelVO.class))
                .nextFullDiscountLevel(KsBeanUtil.convert(nextFullDiscountLevel, MarketingFullDiscountLevelVO.class))
                .build();
    }

    /**
     * 满数量折
     * @param devanningGoodsInfoVOList
     * @param marketing
     * @return
     */
    private MarketingCalculatorResult reachCount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, Marketing marketing){
        // 营销活动当前达到的最大优惠门槛
        MarketingFullDiscountLevel currentFullDiscountLevel = null;
        // 营销活动的下一门槛
        MarketingFullDiscountLevel nextFullDiscountLevel = null;
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

        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = marketing.getMarketingFullDiscountLevels();
        marketingFullDiscountLevels = marketingFullDiscountLevels.stream()
                .sorted(Comparator.comparing(MarketingFullDiscountLevel::getFullCount).reversed())
                .collect(Collectors.toList());

        for (DevanningGoodsInfoVO devanningGoodsInfoVO: devanningGoodsInfoVOList) {
            if(Objects.isNull(devanningGoodsInfoVO)){ continue; }
            BigDecimal buyCount = BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount());
            BigDecimal marketPrice = devanningGoodsInfoVO.getMarketPrice();
            BigDecimal multiply = marketPrice.multiply(buyCount);
            beforeAmount = beforeAmount.add(multiply);
            totalBuyCount = totalBuyCount.add(buyCount);
        }

        for (MarketingFullDiscountLevel marketingFullDiscountLevel : marketingFullDiscountLevels) {
            // 活动门槛
            BigDecimal fullCount = BigDecimal.valueOf(marketingFullDiscountLevel.getFullCount());
            // 未达到活动门槛
            if(totalBuyCount.compareTo(fullCount) < 0){
                nextFullDiscountLevel = marketingFullDiscountLevel;
                continue;
            }
            // 达到活动门槛
            currentFullDiscountLevel = marketingFullDiscountLevel;
            reachLevel = Boolean.TRUE;
            break;
        }

        // 获取叠加倍数
        BoolFlag isOverlap = marketing.getIsOverlap();
        if(BoolFlag.YES.equals(isOverlap) && Objects.nonNull(currentFullDiscountLevel)){
            // 当前活动的门槛
            BigDecimal fullCount = BigDecimal.valueOf(currentFullDiscountLevel.getFullCount());
            // 计算叠加倍数(忽略四舍五入，向下取整)
            reMarketingFold = totalBuyCount.divide(fullCount,0, BigDecimal.ROUND_DOWN);
            if(reMarketingFold.compareTo(BigDecimal.ONE) < 0) {
                reMarketingFold = BigDecimal.ONE;
            }
        }
        log.info("满数量折扣，叠加倍数：{}",reMarketingFold);

        // 计算优惠金额
        // 计算优惠金额
        if(Objects.nonNull(currentFullDiscountLevel)){
            BigDecimal discount = currentFullDiscountLevel.getDiscount();
            int re = reMarketingFold.intValue();
            BigDecimal multiply = beforeAmount.multiply(discount);
            if(re > 1){
                for (int i = re; re > 1; i--){
                    multiply = multiply.multiply(discount);
                }
            }
            log.info("currentFullDiscountLevel multiply:{}", multiply);
            totalPayableAmount = multiply.setScale(2, BigDecimal.ROUND_DOWN);// 保留两位小数

        }
        profitAmount = beforeAmount.subtract(totalPayableAmount);

        // 计算距离下一活动门槛的差值，还差多少钱
        if(Objects.nonNull(nextFullDiscountLevel)){
            BigDecimal thresholdCount = BigDecimal.valueOf(nextFullDiscountLevel.getFullCount());
            diffNextLevel = thresholdCount.subtract(totalBuyCount);
        }

        List<String> devanningGoodsInfoIds = devanningGoodsInfoVOList.stream().filter(x -> Objects.nonNull(x)).map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        log.info("ReachDiscountMarketingCalculator reachCount=>beforeAmount:{},payableAmount:{},profitAmount:{}",beforeAmount,totalPayableAmount,profitAmount);

        return MarketingCalculatorResult.builder()
                .beforeAmount(beforeAmount)
                .payableAmount(totalPayableAmount)
                .profitAmount(profitAmount)
                .reachLevel(reachLevel)
                .reMarketingFold(reMarketingFold)
                .diffNextLevel(diffNextLevel)
                .marketingId(marketing.getMarketingId())
                .goodsInfoIds(devanningGoodsInfoIds)
                .currentFullDiscountLevel(KsBeanUtil.convert(currentFullDiscountLevel, MarketingFullDiscountLevelVO.class))
                .nextFullDiscountLevel(KsBeanUtil.convert(nextFullDiscountLevel, MarketingFullDiscountLevelVO.class))
                .build();
    }

    private Boolean checkParams(Marketing marketing){
        MarketingSubType subType = marketing.getSubType();
        MarketingSubType[] marketingSubTypes = {
                MarketingSubType.DISCOUNT_FULL_COUNT,
                MarketingSubType.DISCOUNT_FULL_AMOUNT
        };
        if(!ArrayUtils.contains(marketingSubTypes, subType)){
            return false;
        }

        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = marketing.getMarketingFullDiscountLevels();
        if(CollectionUtils.isEmpty(marketingFullDiscountLevels)){
            return false;
        }

        return true;
    }

}
