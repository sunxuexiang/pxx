package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 营销活动通用检查节点
 */
@Slf4j
@Component
public class MarketingCommonCheckNode implements MarketingCheckChainNode {

    @Autowired
    private MarketingRepository marketingRepository;

    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();
        if (Objects.isNull(request)) {
            return result;
        }

        Long marketingId = request.getMarketingId();
        if(Objects.nonNull(marketingId)){
            Optional<Marketing> marketingRecord = marketingRepository.findById(marketingId);

            // 编辑状态要保证该实体在数据库中存在
            if(!marketingRecord.isPresent()){
                result.setShowMessage("请刷新页面");
                result.setSuccess(false);
                return result;
            }

            // 已经结束和已经终止的营销活动不能进行编辑
            Marketing marketing = marketingRecord.get();
            boolean marketingIsNotGoingOn = LocalDateTime.now().isAfter(marketing.getEndTime());
            boolean marketingTerminated = Objects.equals(BoolFlag.YES, marketing.getTerminationFlag());
            if(marketingIsNotGoingOn || marketingTerminated){
                result.setResultCode(MarketingErrorCode.CANNOT_EDIT_WHEN_MARKETING_TERMINATED);
                result.setShowMessage("已结束或已终止的营销活动不能进行编辑");
                result.setSuccess(false);
                return result;
            }
        }
        result.setSuccess(true);
        return result;
    }

}
