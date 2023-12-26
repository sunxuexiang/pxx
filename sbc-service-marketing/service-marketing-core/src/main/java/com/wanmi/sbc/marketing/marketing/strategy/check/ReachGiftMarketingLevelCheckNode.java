package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.api.request.market.latest.ReachDiscountLevelRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingGiveGoodItemRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.ReachGiftLevelRequest;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 检查营销活动的梯度
 */
@Slf4j
@Component
public class ReachGiftMarketingLevelCheckNode implements MarketingCheckChainNode {
    /**
     * 活动梯度的上线
     */
    public static final Integer MARKETING_LEVEL_NUM = 5;

    @Override
    public  MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();
        if (Objects.isNull(request)) {
            result.setSuccess(false);
            return result;
        }

        // 未配置满赠等级
        List<ReachGiftLevelRequest> reachGiftLevelRequests = request.getReachGiftLevelRequests();
        if(Objects.isNull(reachGiftLevelRequests)) {
            result.setResultCode(MarketingErrorCode.MARKETING_NOT_HAVE_LEVEL);
            result.setSuccess(false);
            result.setShowMessage("营销活动未配置满减等级");
            return result;
        }

        int levelNum = reachGiftLevelRequests.size();

        // 梯度和叠加只能选一个
        boolean b = request.getIsOverlap() == BoolFlag.YES
                && (levelNum > 1);
        if(b) {
            result.setResultCode(MarketingErrorCode.MARKETING_LEVEL_AND_IS_OVERLAP_ONLY_CHOOSE_ONE);
            result.setSuccess(false);
            result.setShowMessage("满赠活动梯度和叠加只能设置一个");
            return result;
        }

        // 活动的梯度不能大于5
        if(levelNum > MARKETING_LEVEL_NUM){
            result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THAN_FIVE);
            result.setSuccess(false);
            result.setShowMessage("营销活动梯度最多配置"+MARKETING_LEVEL_NUM+"级");
            return result;
        }

        // 每一个阶梯赠品数量不能为空
        for (ReachGiftLevelRequest reachGiftLevelRequest: reachGiftLevelRequests) {
            List<MarketingGiveGoodItemRequest> marketingGiveGoodItemRequest = reachGiftLevelRequest.getMarketingGiveGoodItemRequest();
            if(CollectionUtils.isEmpty(marketingGiveGoodItemRequest)){
                result.setSuccess(false);
                result.setShowMessage("满赠活动中的赠品数量不能为空");
                return result;
            }
        }

        BigDecimal threshold = BigDecimal.ZERO;
        for (ReachGiftLevelRequest reachGiftLevel : reachGiftLevelRequests) {
            BigDecimal currentThreshold = reachGiftLevel.getThreshold();
            // 校验营销活动门槛设置是否正确
            if(Objects.isNull(currentThreshold)){
                result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THRESHOLD_IS_NULL);
                result.setSuccess(false);
                result.setShowMessage("请设置营销活动门槛");
                return result;
            }
            // 校验营销活动的门槛是否梯度自增
            if(currentThreshold.compareTo(threshold) <= 0){
                result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THRESHOLD_NOT_INCREMENT);
                result.setSuccess(false);
                result.setShowMessage("营销活动门槛值不符合自增要求");
                return result;
            }
            threshold = currentThreshold;
        }

        result.setSuccess(true);
        return result;
    }
}
