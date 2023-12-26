package com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.market.*;

import com.wanmi.sbc.marketing.api.request.marketingpurchaselimit.MarketingPurchaseLimitRequest;
import com.wanmi.sbc.marketing.bean.vo.MarketingPurchaseLimitVO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingPurchaseLimitProvider")
public interface MarketingPurchaseLimitProvider {

    @PostMapping("/marketing/${application.marketing.version}/query-list-by-marketingid-and-goodsinfoids")
    BaseResponse<List<MarketingPurchaseLimitVO>> queryListByMarketingIdAndGoodsInfoIds(@RequestBody @Valid MarketingPurchaseLimitRequest request);


    @PostMapping("/marketing/${application.marketing.version}/query-list-parm")
    BaseResponse<List<MarketingPurchaseLimitVO>> queryListByParm(@RequestBody @Valid Map<String,Object> request);


    @PostMapping("/marketing/${application.marketing.version}/query-list-parm-no-user")
    BaseResponse<List<MarketingPurchaseLimitVO>> queryListByParmNoUser(@RequestBody @Valid Map<String,Object> request);


    @PostMapping("/marketing/${application.marketing.version}/add-list")
    BaseResponse add(@RequestBody @Valid List<MarketingPurchaseLimitVO> request);


}
