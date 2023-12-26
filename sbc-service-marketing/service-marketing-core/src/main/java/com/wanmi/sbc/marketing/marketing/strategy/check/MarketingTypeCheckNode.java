package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

/**
 * 检查营销活动子类型是否正确
 */
@Slf4j
@Component
public class MarketingTypeCheckNode implements MarketingCheckChainNode {

    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {

        MarketingCheckResult result = new MarketingCheckResult();
        if (!Objects.isNull(request)) {
            boolean doCheckResult = this.doCheck(request.getMarketingType(), request.getSubType());
            if(BooleanUtils.isFalse(doCheckResult)){
                result.setSuccess(false);
                result.setResultCode(MarketingErrorCode.MARKETING_TYPE_NOT_MATCH_SUB_TYPE);
                result.setShowMessage("营销类型与当前活动子类型不匹配");
            }
            result.setSuccess(true);
        }
        return result;
    }

    private boolean doCheck(MarketingType marketingType, MarketingSubType subType) {

        boolean validParam = false;

        if ( MarketingType.REDUCTION == marketingType) {
            MarketingSubType[] marketingSubTypes = {
                    MarketingSubType.REDUCTION_FULL_AMOUNT,
                    MarketingSubType.REDUCTION_FULL_COUNT,
                    MarketingSubType.REDUCTION_FULL_ORDER
            };
            validParam = Arrays.asList(marketingSubTypes).contains(subType);
        } else if (MarketingType.DISCOUNT == marketingType) {
            MarketingSubType[] marketingSubTypes = {
                    MarketingSubType.DISCOUNT_FULL_AMOUNT,
                    MarketingSubType.DISCOUNT_FULL_COUNT,
                    MarketingSubType.DISCOUNT_FULL_ORDER
            };
            validParam = Arrays.asList(marketingSubTypes).contains(subType);
        } else if (MarketingType.GIFT == marketingType) {
            MarketingSubType[] marketingSubTypes = {
                    MarketingSubType.GIFT_FULL_AMOUNT,
                    MarketingSubType.GIFT_FULL_COUNT,
                    MarketingSubType.GIFT_FULL_ORDER
            };
            validParam = Arrays.asList(marketingSubTypes).contains(subType);
        }

        return validParam;
    }
}
