package com.wanmi.sbc.marketing.api.provider.plugin;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingTradeBatchTryCatchWrapperResponse;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingTradeBatchWrapperResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>订单营销插件服务操作接口</p>
 * author: daiyitian
 * Date: 2018-11-15
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingTradePluginProvider")
public interface MarketingTradePluginProvider {

    /**
     * 订单营销批量处理
     * @param request 包含多个营销处理结构 {@link MarketingTradeBatchWrapperRequest}
     * @return 处理结果 {@link MarketingTradeBatchWrapperResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/plugin/trade/batch-wrapper")
    BaseResponse<MarketingTradeBatchWrapperResponse> batchWrapper(@RequestBody @Valid
                                                                          MarketingTradeBatchWrapperRequest request);


    /**
     * 订单营销批量处理 拆箱
     * @param request 包含多个营销处理结构 {@link MarketingTradeBatchWrapperRequest}
     * @return 处理结果 {@link MarketingTradeBatchWrapperResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/plugin/trade/batch-wrapper-devaning")
    BaseResponse<MarketingTradeBatchWrapperResponse> batchWrapperDevaning(@RequestBody @Valid
                                                                          MarketingTradeBatchWrapperRequest request);


    /**
     * 订单营销批量处理
     * @param request 包含多个营销处理结构 {@link MarketingTradeBatchWrapperRequest}
     * @return 处理结果 {@link MarketingTradeBatchWrapperResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/plugin/trade/batch-wrapper-try-catch")
    BaseResponse<MarketingTradeBatchTryCatchWrapperResponse> batchWrapperTryCatch(@RequestBody @Valid
                                                                          MarketingTradeBatchWrapperRequest request);

}
