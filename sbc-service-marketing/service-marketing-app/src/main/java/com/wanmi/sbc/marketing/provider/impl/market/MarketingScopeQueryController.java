package com.wanmi.sbc.marketing.provider.impl.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingEffectiveRespose;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.service.MarketingScopeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-21 14:27
 */
@Validated
@RestController
public class MarketingScopeQueryController implements MarketingScopeQueryProvider {

    @Autowired
    private MarketingScopeService marketingScopeService;

    @Override
    public BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingId(@RequestBody @Valid MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest) {
        List<MarketingScope> marketingFullDiscountLevelList =  marketingScopeService.findByMarketingId(marketingScopeByMarketingIdRequest.getMarketingId());
        if (CollectionUtils.isEmpty(marketingFullDiscountLevelList)){
            return BaseResponse.success(new MarketingScopeByMarketingIdResponse(Collections.EMPTY_LIST));
        }
        List<MarketingScopeVO> marketingFullDiscountLevelVOList = KsBeanUtil.convert(marketingFullDiscountLevelList, MarketingScopeVO.class);
        return BaseResponse.success(new MarketingScopeByMarketingIdResponse(marketingFullDiscountLevelVOList));
    }

    @Override
    public BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingIdAndSkuId(MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest) {
        List<MarketingScope> marketingScopeList = marketingScopeService.findByMarketingIdAndScopeId(marketingScopeByMarketingIdRequest.getMarketingId(),
                marketingScopeByMarketingIdRequest.getSkuId());
        if (CollectionUtils.isEmpty(marketingScopeList)){
            return BaseResponse.success(new MarketingScopeByMarketingIdResponse(Collections.EMPTY_LIST));
        }
        List<MarketingScopeVO> marketingScopeVOList = KsBeanUtil.convert(marketingScopeList,MarketingScopeVO.class);
        return BaseResponse.success(new MarketingScopeByMarketingIdResponse(marketingScopeVOList));
    }

    @Override
    public BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingIdAndSkuIdAndCache(MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest) {
        List<MarketingScope> marketingScopeList = marketingScopeService.findByMarketingIdAndScopeIdAndCache(marketingScopeByMarketingIdRequest.getMarketingId(),
                marketingScopeByMarketingIdRequest.getSkuId());
        if (CollectionUtils.isEmpty(marketingScopeList)){
            return BaseResponse.success(new MarketingScopeByMarketingIdResponse(Collections.EMPTY_LIST));
        }
        List<MarketingScopeVO> marketingScopeVOList = KsBeanUtil.convert(marketingScopeList,MarketingScopeVO.class);
        return BaseResponse.success(new MarketingScopeByMarketingIdResponse(marketingScopeVOList));
    }

    @Override
    public BaseResponse<MarketingScopeByMarketingIdResponse> findByMarketingIdAndScopeIdNotTermination(MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest) {
        List<MarketingScope> marketingScopeList = marketingScopeService.findByMarketingIdAndScopeIdNotTermination(marketingScopeByMarketingIdRequest.getMarketingId(),
                marketingScopeByMarketingIdRequest.getSkuId());
        if (CollectionUtils.isEmpty(marketingScopeList)){
            return BaseResponse.success(new MarketingScopeByMarketingIdResponse(Collections.EMPTY_LIST));
        }
        List<MarketingScopeVO> marketingScopeVOList = KsBeanUtil.convert(marketingScopeList,MarketingScopeVO.class);
        return BaseResponse.success(new MarketingScopeByMarketingIdResponse(marketingScopeVOList));
    }

    @Override
    public BaseResponse<MarketingEffectiveRespose> getMarketingScopeLimitPurchase(MarketingEffectiveRequest request) {
        List<TradeMarketingDTO> marketingScopeLimitPurchase = marketingScopeService.getMarketingScopeLimitPurchase(request.getTradeMarketingList());
        return BaseResponse.success(MarketingEffectiveRespose.builder().tradeMarketingList(marketingScopeLimitPurchase).build());
    }
}
