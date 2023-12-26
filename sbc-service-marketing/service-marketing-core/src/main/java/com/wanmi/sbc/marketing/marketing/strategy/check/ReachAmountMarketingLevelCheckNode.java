package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.ReachAmountLevelRequest;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 检查营销活动的梯度
 */
@Slf4j
@Component
public class ReachAmountMarketingLevelCheckNode implements MarketingCheckChainNode {
    /**
     * 活动梯度的上线
     */
    public static final Integer MARKETING_LEVEL_NUM = 5;

    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();

        if (Objects.isNull(request)) {
            result.setSuccess(false);
            return result;
        }

        // 未配置满折等级
        List<ReachAmountLevelRequest> reachAmountLevelRequest = request.getReachAmountLevelRequest();
        if(Objects.isNull(reachAmountLevelRequest)) {
            result.setResultCode(MarketingErrorCode.MARKETING_NOT_HAVE_LEVEL);
            result.setShowMessage("未配置满减营销活动等级");
            result.setSuccess(false);
            return result;
        }

        int levelNum = reachAmountLevelRequest.size();

        // 梯度和叠加只能选一个
        boolean b = request.getIsOverlap() == BoolFlag.YES
                && (levelNum > 1);
        if(b) {
            result.setResultCode(MarketingErrorCode.MARKETING_LEVEL_AND_IS_OVERLAP_ONLY_CHOOSE_ONE);
            result.setShowMessage("不能同时参加多级营销活动和活动叠加");
            result.setSuccess(false);
            return result;
        }

        // 活动的梯度数量不能大于该阈值
        if(levelNum > MARKETING_LEVEL_NUM){
            result.setSuccess(false);
            result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THAN_FIVE);
            result.setShowMessage("活动梯度最多配置"+MARKETING_LEVEL_NUM+"级");
            return result;
        }

        BigDecimal threshold = BigDecimal.ZERO;
        for (ReachAmountLevelRequest reachAmountLevel : reachAmountLevelRequest) {
            BigDecimal currentThreshold = reachAmountLevel.getThreshold();
            // 校验营销活动门槛设置是否正确
            if(Objects.isNull(currentThreshold)){
                result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THRESHOLD_IS_NULL);
                result.setSuccess(false);
                result.setShowMessage("营销活动门槛不能设置为空");
                return result;
            }
            // 校验营销活动的门槛是否梯度自增
            if(currentThreshold.compareTo(threshold) <= 0){
                result.setResultCode(MarketingErrorCode.MARKETIN_LEVEL_THRESHOLD_NOT_INCREMENT);
                result.setSuccess(false);
                result.setShowMessage("请检查营销活动门槛是否按照等级增加");
                return result;
            }
            threshold = currentThreshold;
        }

        result.setSuccess(true);
        return result;
    }
}
