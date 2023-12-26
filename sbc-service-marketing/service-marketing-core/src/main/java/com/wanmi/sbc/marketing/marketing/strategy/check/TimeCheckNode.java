package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 检查营销活动的时间：
 * （1）开始时间小于结束时间。
 * （2）开始时间必须大于当前的时间
 */
@Slf4j
@Component
public class TimeCheckNode implements MarketingCheckChainNode {
    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();

        if (Objects.isNull(request)) {
            result.setSuccess(false);
            return result;
        }


        if(BooleanUtils.isTrue( request.getBeginTime().isAfter(request.getEndTime()) )){
            result.setSuccess(false);
            result.setResultCode(MarketingErrorCode.MARKETING_CREATE_TIME_AFTER_END_TIME);
            result.setShowMessage("营销活动开始时间不能晚于结束时间");
            return result;
        }

        // 营销活动的开始时间早于当前时间
        if(BooleanUtils.isTrue( LocalDateTime.now().isAfter(request.getBeginTime()) )){
            result.setSuccess(false);
            result.setResultCode(MarketingErrorCode.MARKETING_CREATE_TIME_BEFORE_NOW);
            result.setShowMessage("营销活动开始时间早于当前时间");
            return result;
        }

        result.setSuccess(true);

        return result;
    }
}
