package com.wanmi.sbc.marketing.api.provider.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingEffectiveRespose;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: ZhangLingKe
 * @Description: 营销查询接口Feign客户端
 * @Date: 2018-11-16 16:56
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingScopeQueryProvider")
public interface MarketingScopeQueryProvider {

    /**
     * 根据营销编号查询营销等级集合
     * @param marketingScopeByMarketingIdRequest  {@link MarketingScopeByMarketingIdRequest}
     * @return {@link MarketingScopeByMarketingIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/list-by-marketing-id")
    BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingId(@RequestBody @Valid MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest);

    /**
     * 根据营销编号、商品skuIds查询营销等级集合
     * @param marketingScopeByMarketingIdRequest  {@link MarketingScopeByMarketingIdRequest}
     * @return {@link MarketingScopeByMarketingIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/list-by-marketing-id-and-skuIds")
    BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingIdAndSkuId(@RequestBody @Valid MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest);


    /**
     * 根据营销编号、商品skuIds查询营销等级集合 缓存
     * @param marketingScopeByMarketingIdRequest  {@link MarketingScopeByMarketingIdRequest}
     * @return {@link MarketingScopeByMarketingIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/list-by-marketing-id-and-skuIds-cache")
    BaseResponse<MarketingScopeByMarketingIdResponse> listByMarketingIdAndSkuIdAndCache(@RequestBody @Valid MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest);



    /**
     * 根据营销编号、商品skuIds查询营销等级集合
     * @param marketingScopeByMarketingIdRequest  {@link MarketingScopeByMarketingIdRequest}
     * @return {@link MarketingScopeByMarketingIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/list-by-marketing-id-and-skuIds-not")
    BaseResponse<MarketingScopeByMarketingIdResponse> findByMarketingIdAndScopeIdNotTermination(@RequestBody @Valid MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest);




    @PostMapping("/marketing/${application.marketing.version}/scope/list-by-marketing-purchase")
    BaseResponse<MarketingEffectiveRespose> getMarketingScopeLimitPurchase(@RequestBody @Valid MarketingEffectiveRequest request);

}
