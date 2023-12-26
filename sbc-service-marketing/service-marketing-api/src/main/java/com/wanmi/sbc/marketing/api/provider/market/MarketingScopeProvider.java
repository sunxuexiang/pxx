package com.wanmi.sbc.marketing.api.provider.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.market.TerminationMarketingScopeRequest;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 更新状态
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingScopeProvider")
public interface MarketingScopeProvider {

    /**
     * 根据scopeId marketId查询
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/update-scopeId")
    BaseResponse terminationMarketingIdAndScopeId(@RequestBody @Valid TerminationMarketingScopeRequest request);

    /**
     * 根据marketId, goodsInfoId查询
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/termination-by-marketingid-and-scopeId")
    BaseResponse terminationByMarketingIdAndScopeId(@RequestBody @Valid TerminationMarketingScopeRequest request);

    /**
     * 更新保存营销关联信息
     * @param marketingScopeVO
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/scope/save-marketing-scope")
    BaseResponse saveMarketingScope(@RequestBody @Valid MarketingScopeVO marketingScopeVO);
}
