package com.wanmi.sbc.marketing.marketing.strategy.check;

import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingGoodsItemRequest;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.marketing.MarketService;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品的单用户的限购量不得大于总用户的限购量
 */
@Slf4j
@Component
public class MarketingScopeCheckNode implements MarketingCheckChainNode {

    public static final Integer TOTAL_BUY_LIMIT = 99999;
    public static final Integer PER_USER_BUY_LIMIT = 99999;

    @Autowired
    private MarketingRepository marketingRepository;

    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {

        MarketingCheckResult result = new MarketingCheckResult();
        result.setSuccess(true);
        if (Objects.isNull(request)) {
            return result;
        }
        List<MarketingGoodsItemRequest> goodsItemRequests = request.getGoodsItemRequest();

        // 未关联商品
        if(CollectionUtils.isEmpty(goodsItemRequests)){
            result.setSuccess(false);
            result.setResultCode(MarketingErrorCode.MARKETING_NOT_RELATE_GOODS);
            result.setShowMessage("当前活动未关联商品");
            return result;
        }

        for (MarketingGoodsItemRequest goodsItemRequest : goodsItemRequests) {
            Long totalBuyLimitQuantity = goodsItemRequest.getPurchaseNum();
            if(Objects.isNull(totalBuyLimitQuantity)){
                totalBuyLimitQuantity = 0L;
            }
            Long perUserBuyLimitQuantity = goodsItemRequest.getPerUserPurchaseNum();
            if(Objects.isNull(perUserBuyLimitQuantity)){
                perUserBuyLimitQuantity = 0L;
            }

            // 商品的数据范围不能超出 999999
            if(Long.compare(totalBuyLimitQuantity,TOTAL_BUY_LIMIT) > 0
                || Long.compare(perUserBuyLimitQuantity,PER_USER_BUY_LIMIT) > 0 ){
                result.setSuccess(false);
                result.setResultCode(MarketingErrorCode.PER_USER_BUY_OR_TOTAL_BUY_OVER_LIMIT);
                result.setShowMessage("单用户限购量或总限购量不能大于"+TOTAL_BUY_LIMIT+"，请检查");
                return result;
            }

            int compare = Long.compare(totalBuyLimitQuantity, perUserBuyLimitQuantity);
            if(compare < 0){
                result.setSuccess(false);
                result.setResultCode(MarketingErrorCode.PER_USER_BUY_THAN_TOTAL_BUY);
                result.setShowMessage("商品中的单用户的限购量不得大于总用户的限购量");
                return result;
            }
        }

        /**
         * 营销活动重复SKU校验
         */
        Long marketingId = request.getMarketingId();
        List<MarketingSubType> subTypes=new ArrayList<>();
        subTypes.add(MarketingSubType.REDUCTION_FULL_ORDER);
        subTypes.add(MarketingSubType.DISCOUNT_FULL_ORDER);

        List<String> skuIds = request.getGoodsItemRequest().stream().map(MarketingGoodsItemRequest::getSkuId).collect(Collectors.toList());
        List<String> existsSkuByMarketingType = marketingRepository.getExistsSkuByMarketingType(skuIds
                , request.getMarketingType(), request.getBeginTime()
                , request.getEndTime(), request.getStoreId(), marketingId, subTypes);
        if(!CollectionUtils.isEmpty(existsSkuByMarketingType)){
            result.setSuccess(false);
            result.setResultCode(CommonErrorCode.SPECIFIED);
            result.setShowMessage(existsSkuByMarketingType.size()+"款商品活动时间冲突，请删除后再保存");
            return result;
        }

        return result;
    }
}
